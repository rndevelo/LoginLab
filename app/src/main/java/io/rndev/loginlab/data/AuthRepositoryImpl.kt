package io.rndev.loginlab.data

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.rndev.loginlab.Result
import jakarta.inject.Inject
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl @Inject constructor(val auth: FirebaseAuth) : AuthRepository {

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

    override suspend fun emailSignIn(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await().let { authResult ->
                Result.Success(authResult.user != null)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun emailSignUp(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await().let { authResult ->
                return try {
                    authResult.user?.sendEmailVerification()?.await()
                    Result.Success(true)
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun credentialSingIn(credential: AuthCredential) = channelFlow {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                launch {
                    send(Result.Success(it.user != null))
                }
            }
            .addOnFailureListener {
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
}

private fun ProducerScope<Result<Boolean>>.emailSendVerification(result: AuthResult?) {
    Log.d("EmailSendVerification", "UNIT")
    result?.user?.sendEmailVerification()?.addOnSuccessListener {
        launch {
            Log.d("EmailSendVerification", "true")
            send(Result.Success(true))
        }
    }
        ?.addOnFailureListener {
            launch {
                Log.d("EmailSendVerification", "true")
                send(Result.Error(it))
            }
        }
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
