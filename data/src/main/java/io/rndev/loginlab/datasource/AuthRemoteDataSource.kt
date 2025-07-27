package io.rndev.loginlab.datasource

import android.app.Activity
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.User
import kotlinx.coroutines.flow.Flow
import io.rndev.loginlab.Result

//Interfaz del repositorio de autenticación, ha sido creada porque ahora mismo la autenticación se hace con Firebase
interface AuthRemoteDataSource {
    fun currentUser(): Flow<Result<User>>
    fun isAuthenticated(): Flow<Result<Boolean>>
    fun emailSignIn(email: String, password: String): Flow<Result<Boolean>>
    fun emailSignUp(email: String, password: String): Flow<Result<Boolean>>
    fun googleSingIn(idToken: String): Flow<Result<Boolean>>
    fun facebookSingIn(idToken: String): Flow<Result<Boolean>>
    fun phoneWithOtpSignIn(verificationId: String, otpCode: String): Flow<Result<Boolean>>
    fun phoneSingIn(phoneNumber: String, activity: Activity): Flow<PhoneAuthEvent>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun isEmailVerified(): Flow<Boolean>
    fun signOut()
}