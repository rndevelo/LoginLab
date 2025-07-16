package io.rndev.loginlab.domain

import io.rndev.loginlab.Result
import io.rndev.loginlab.data.AuthRepository
import javax.inject.Inject

class EmailSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        if (email.isBlank() || password.isBlank()) { // Validación básica
            return Result.Error(IllegalArgumentException("Email and password cannot be empty."))
        }
        return authRepository.emailSignIn(email, password)
    }
}