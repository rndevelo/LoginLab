package io.rndev.loginlab.usecases

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.rndev.loginlab.data.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FacebookSignInUseCase(
    private val firebaseAuth: FirebaseAuth,
    private val authRepository: AuthRepository,
) {

//    private val _resultChannel = Channel<Result<Unit>>(Channel.BUFFERED)
//    val results = _resultChannel.receiveAsFlow()
//
//    fun startPhoneSignIn(phoneNumber: String, activity: Activity) {
//        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
//            .setPhoneNumber(phoneNumber)
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(activity)
//            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    // Log in con credencial y emitir resultado
//                    CoroutineScope(Dispatchers.IO).launch {
//                        authRepository.credentialSingIn(credential).collect { result ->
//                            _resultChannel.send(result)
//                        }
//                    }
//                }
//                override fun onVerificationFailed(e: FirebaseException) {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        _resultChannel.send(Result.Error(e))
//                    }
//                }
//                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                    // Emitir evento para que UI navegue a pantalla de verificación, por ejemplo
//                    // Puedes crear otro canal o usar UiEvent directamente
//                }
//            })
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
}