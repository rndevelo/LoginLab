package io.rndev.loginlab.data

import android.app.Activity
import android.content.Context
import io.rndev.loginlab.AuthRepository
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.datasource.AuthRemoteDataSource
import io.rndev.loginlab.data.datasource.TokenRemoteDataSource
import io.rndev.loginlab.domain.generateFakeUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

fun buildAuthRepositoryWith(
    authResult: Result<Boolean>,
    phoneAuthEvent: PhoneAuthEvent,
    verifiedEmail: Boolean,
    googleToken: Result<String>
): AuthRepository {

    val authRemoteDataSource = FakeAuthDataSource().apply {
        inMemoryAuthResult.value = authResult
        inMemoryPhoneAuthEvent.value = phoneAuthEvent
        inMemoryVerifiedEmail.value = verifiedEmail
    }

    val tokenRemoteDataSource = FakeTokenDataSource().apply {
        inMemoryTokenResult = googleToken as Result.Success<String>
    }

    return AuthRepositoryImpl(
        authRemoteDataSource = authRemoteDataSource,
        tokenRemoteDataSource = tokenRemoteDataSource
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

class FakeTokenDataSource : TokenRemoteDataSource {

    var inMemoryTokenResult = Result.Success("token")

    override suspend fun getGoogleIdToken(context: Context) = inMemoryTokenResult
}