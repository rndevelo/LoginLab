package io.rndev.loginlab.domain

import com.google.firebase.auth.AuthCredential
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.AuthRepository
import javax.inject.Inject

class SignInWithCredentialUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credential: AuthCredential): Result<Boolean> {
        return authRepository.credentialSingIn(credential)
    }
}