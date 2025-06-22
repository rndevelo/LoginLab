package io.rndev.loginlab.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.rndev.loginlab.Result
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class AuthRepositoryImpl @Inject constructor(val auth: FirebaseAuth) : AuthRepository {

    override fun currentUser(): Flow<Result<User>> = callbackFlow {
        val currentUser = auth.currentUser

        currentUser?.reload()?.addOnSuccessListener {
            launch {
                Log.d("AuthState", "reload success: ${currentUser.email}")
                send(Result.Success(currentUser.toUser()))
            }
        }?.addOnFailureListener {
            launch {
                Log.d("AuthState", "reload error: $it")

                send(Result.Error(it))
            }
        }
        awaitClose()
    }

    override fun isAuthenticated() = channelFlow {
        auth.addAuthStateListener {
            Log.d("AuthState", "addAuthStateListener: ${it.currentUser?.email}")

            launch {
                send(Result.Success(it.currentUser != null))
            }
        }
        awaitClose()
    }

    override fun signIn(email: String, password: String) = channelFlow {
        auth.signInWithEmailAndPassword(email, password)
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
    }

    override fun signUp(email: String, password: String) = channelFlow {
        auth.createUserWithEmailAndPassword(email, password)
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

    override fun signOut() {
        auth.signOut()
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
