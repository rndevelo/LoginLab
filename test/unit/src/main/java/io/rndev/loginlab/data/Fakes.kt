package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.datasource.AuthRemoteDataSource
import io.rndev.loginlab.datasource.FacebookLoginHandler
import io.rndev.loginlab.datasource.GoogleTokenRemoteDataSource
import io.rndev.loginlab.domain.generateFakeUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun buildAuthRepositoryWith(
    authResult: Result<Boolean>,
    phoneAuthEvent: PhoneAuthEvent,
    verifiedEmail: Boolean,
    googleToken: Result<String>,
    facebookToken: String
): AuthRepository {

    val authRemoteDataSource = FakeAuthDataSource().apply {
        inMemoryAuthResult.value = authResult
        inMemoryPhoneAuthEvent.value = phoneAuthEvent
        inMemoryVerifiedEmail.value = verifiedEmail
    }

    val tokenRemoteDataSource = FakeGoogleTokenDataSource().apply {
        inMemoryGoogleTokenResult = googleToken as Result.Success<String>
    }

    val facebookLoginHandler = FakeFacebookHandler().apply {

        registerCallback(
            onSuccess = { inMemoryFbTokenResult = facebookToken },
            onError = { inMemoryFbTokenResult = "facebookError" },
            onCancel = { inMemoryFbTokenResult = "facebookCancel" }
        )
    }

    return AuthRepositoryImpl(
        authRemoteDataSource = authRemoteDataSource,
        googleTokenRemoteDataSource = tokenRemoteDataSource,
        facebookLoginHandler = facebookLoginHandler
    )
}

class FakeAuthDataSource : AuthRemoteDataSource {

    var inMemoryAuthResult = MutableStateFlow<Result<Boolean>>(Result.Success(false))
    var inMemoryPhoneAuthEvent = MutableStateFlow<PhoneAuthEvent>(
        PhoneAuthEvent.VerificationCompleted(flowOf(Result.Success(true)))
    )
    var inMemoryVerifiedEmail = MutableStateFlow<Boolean>(false)

    override fun currentUser() = flowOf(Result.Success(generateFakeUser()))
    override fun isAuthenticated() = inMemoryAuthResult
    override fun emailSignIn(email: String, password: String) = inMemoryAuthResult
    override fun emailSignUp(email: String, password: String) = inMemoryAuthResult
    override fun googleSingIn(idToken: String) = inMemoryAuthResult
    override fun facebookSingIn(idToken: String) = inMemoryAuthResult
    override fun phoneWithOtpSignIn(verificationId: String, otpCode: String) = inMemoryAuthResult
    override fun phoneSingIn(phoneNumber: String, activity: Activity) = inMemoryPhoneAuthEvent
    override fun resetPassword(email: String) = inMemoryAuthResult
    override fun isEmailVerified() = inMemoryVerifiedEmail
    override fun signOut() {}
}

class FakeGoogleTokenDataSource : GoogleTokenRemoteDataSource {

    var inMemoryGoogleTokenResult = Result.Success("google_token")

    override suspend fun getGoogleIdToken(context: Context) = inMemoryGoogleTokenResult
}

class FakeFacebookHandler : FacebookLoginHandler {

    var inMemoryFbTokenResult = "fb_token"


    override fun getLoginActivityResultContract(): com.facebook.login.LoginManager.FacebookLoginActivityResultContract {
        TODO("Not yet implemented")
    }

    override fun registerCallback(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        onSuccess(inMemoryFbTokenResult)
        onError(inMemoryFbTokenResult)
        onError("Fb Cancel")
    }
}
