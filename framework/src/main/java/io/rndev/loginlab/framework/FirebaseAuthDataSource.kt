package io.rndev.loginlab.framework

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.User
import io.rndev.loginlab.datasource.AuthRemoteDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(val auth: FirebaseAuth) : AuthRemoteDataSource {

    override fun currentUser(): Flow<Result<User>> = callbackFlow {
        val currentUser = auth.currentUser

        currentUser?.reload()?.addOnSuccessListener {
            launch {
                send(Result.Success(currentUser.toUser()))
            }
        }?.addOnFailureListener {
            launch {
                send(Result.Error(it))
            }
        }
        awaitClose()
    }

    override fun isAuthenticated() = channelFlow {
        auth.addAuthStateListener {
            launch {
                send(Result.Success(it.currentUser != null))
            }
        }
        awaitClose()
    }

    //    Email
    override fun emailSignIn(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password).onResult()

    override fun emailSignUp(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password).onResult()

    //    Google
    override fun googleSingIn(idToken: String) =
        credentialSingIn(GoogleAuthProvider.getCredential(idToken, null))

    //    Facebook
    override fun facebookSingIn(idToken: String) =
        credentialSingIn(FacebookAuthProvider.getCredential(idToken))

    //    Verify sms phone
    override fun phoneWithOtpSignIn(
        verificationId: String,
        otpCode: String
    ) = credentialSingIn(PhoneAuthProvider.getCredential(verificationId, otpCode))

    //    Phone
    override fun phoneSingIn(
        phoneNumber: String,
        activity: Activity
    ) = callbackFlow {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                PhoneAuthEvent.VerificationCompleted(credentialSingIn(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(PhoneAuthEvent.VerificationFailed(e))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(PhoneAuthEvent.CodeSent(verificationId))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose()
    }

    override fun resetPassword(email: String) = channelFlow {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            launch {
                send(Result.Success(true))
            }
        }.addOnFailureListener {
            launch {
                send(Result.Error(it))
            }
        }
        awaitClose()
    }

    override fun isEmailVerified() = flow {
        while (true) {
            auth.currentUser?.reload()?.await()
            emit(auth.currentUser?.isEmailVerified == true)
            delay(1000)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    fun credentialSingIn(credential: AuthCredential) =
        auth.signInWithCredential(credential).onResult()
}

fun Task<AuthResult>.onResult(): Flow<Result<Boolean>> = channelFlow {
    addOnSuccessListener { authResult ->
        launch {
            send(Result.Success(authResult.user != null))
        }
    }.addOnFailureListener {
        launch {
            send(Result.Error(it))
        }
    }
    awaitClose()
}

private fun FirebaseUser.toUser(): User {
    return User(
        uid = this.uid,
        email = this.email,
        displayName = this.displayName,
        photoUrl = this.photoUrl?.toString(),
        phoneNumber = this.phoneNumber,
        creationTimestamp = this.metadata?.creationTimestamp,
        lastSignInTimestamp = this.metadata?.lastSignInTimestamp
    )
}