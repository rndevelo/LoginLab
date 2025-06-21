package io.rndev.loginlab.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import io.rndev.loginlab.Result

class AuthRepositoryImpl(val auth: FirebaseAuth) : AuthRepository {

    override fun currentUser(): Flow<Result<User?>> = callbackFlow {
        send(Result.Loading)
        auth.currentUser?.reload()?.addOnSuccessListener {
            launch {
                send(Result.Success(auth.currentUser?.toUser()))
            }
            Log.d("isAuthenticated", "reloadSuccess: ${auth.currentUser?.email} ")
        }?.addOnFailureListener {
            launch {
                send(Result.Error(it))
            }
            Log.d("isAuthenticated", "reloadFailure: ${it.message} ")
        }
        awaitClose()
    }

    override fun isAuthenticated() = channelFlow {
        send(Result.Loading)
        auth.addAuthStateListener {
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

    override fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
//                Log.d("isAuthenticated", "signUp: ${it.user != null} ")

//                send(it.user != null)
            }
            .addOnFailureListener {
//                send(false)
            }
    }

    override fun signOut() {
        auth.signOut()
    }
}

private fun FirebaseUser.toUser(): User {
    return User(
        email = this.email ?: "",
        displayName = this.displayName ?: "",
        photoUrl = this.photoUrl?.toString() ?: ""
    )
}
