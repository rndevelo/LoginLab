package io.rndev.loginlab.data

import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.datasource.AuthRemoteDataSource
import io.rndev.loginlab.datasource.GoogleTokenRemoteDataSource
import io.rndev.loginlab.domain.generateFakeUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @Mock
    private lateinit var authRemoteDataSource: AuthRemoteDataSource

    @Mock
    private lateinit var googleTokenRemoteDataSource: GoogleTokenRemoteDataSource

    private lateinit var repository: AuthRepositoryImpl


    @Before
    fun setUp() {
        repository =
            AuthRepositoryImpl(
                authRemoteDataSource = authRemoteDataSource,
                googleTokenRemoteDataSource = googleTokenRemoteDataSource
            )
    }

    //    User && Auth
    @Test
    fun `Get current user from remote data source`() = runBlocking {
        val userResult = Result.Success(generateFakeUser())

        whenever(authRemoteDataSource.currentUser()).thenReturn(flowOf(userResult))

        val result = repository.currentUser()

        assertEquals(userResult, result.first())
    }

    @Test
    fun `Get is authenticated from remote data source`() = runBlocking {
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.isAuthenticated()).thenReturn(flowOf(authResult))

        val result = repository.isAuthenticated()

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get is not authenticated from remote data source`() = runBlocking {
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.isAuthenticated()).thenReturn(flowOf(authResult))

        val result = repository.isAuthenticated()

        assertEquals(authResult, result.first())
    }

    //    Email
    @Test
    fun `Get valid email sign in auth from remote data source`() = runBlocking {
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.emailSignIn("", "")).thenReturn(flowOf(authResult))

        val result = repository.emailSignIn("", "")

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid email sign in auth from remote data source`() = runBlocking {
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.emailSignIn("", "")).thenReturn(flowOf(authResult))

        val result = repository.emailSignIn("", "")

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get valid email sign up auth from remote data source`() = runBlocking {
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.emailSignUp("", "")).thenReturn(flowOf(authResult))

        val result = repository.emailSignUp("", "")

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid email sign up auth from remote data source`() = runBlocking {
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.emailSignUp("", "")).thenReturn(flowOf(authResult))

        val result = repository.emailSignUp("", "")

        assertEquals(authResult, result.first())
    }

    //    Google
    @Test
    fun `Get valid google sign in auth from remote data source`() = runBlocking {
        val mockIdToken = "mock_token"
        val authResult = Result.Success(true)

        whenever(googleTokenRemoteDataSource.getGoogleIdToken(any())).thenReturn(
            Result.Success(
                mockIdToken
            )
        )
        whenever(authRemoteDataSource.googleSingIn(eq(mockIdToken))).thenReturn(flowOf(authResult))

        val result = repository.googleSignIn(mock())

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid google sign in auth from remote data source`() = runBlocking {
        val mockIdToken = "mock_token"
        val authResult = Result.Error(Exception("Error"))

        whenever(googleTokenRemoteDataSource.getGoogleIdToken(any())).thenReturn(
            Result.Success(
                mockIdToken
            )
        )
        whenever(authRemoteDataSource.googleSingIn(eq(mockIdToken))).thenReturn(flowOf(authResult))

        val result = repository.googleSignIn(mock())

        assertEquals(authResult, result.first())
    }

    //    Facebook
    @Test
    fun `Get valid facebook sign in auth from remote data source`() = runBlocking {
        val mockIdToken = "mock_token"
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.facebookSingIn(eq(mockIdToken)))
            .thenReturn(flowOf(authResult))

        val result = repository.facebookSignIn(mockIdToken)

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid facebook sign in auth from remote data source`() = runBlocking {
        val mockIdToken = "mock_token"
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.facebookSingIn(eq(mockIdToken)))
            .thenReturn(flowOf(authResult))

        val result = repository.facebookSignIn(mockIdToken)

        assertEquals(authResult, result.first())
    }

    //    Phone otp
    @Test
    fun `Get valid otp phone sign in auth from remote data source`() = runBlocking {
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.phoneWithOtpSignIn("", ""))
            .thenReturn(flowOf(authResult))

        val result = repository.phoneWithOtpSignIn("", "")

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid otp phone sign in auth from remote data source`() = runBlocking {
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.phoneWithOtpSignIn("", ""))
            .thenReturn(flowOf(authResult))

        val result = repository.phoneWithOtpSignIn("", "")

        assertEquals(authResult, result.first())
    }


    //    PhoneAuthEvent
    @Test
    fun `Get verification completed event phone sign in auth from remote data source`() =
        runBlocking {
            val phoneAuthEventResult =
                PhoneAuthEvent.VerificationCompleted(flowOf(Result.Success(true)))

            whenever(authRemoteDataSource.phoneSingIn(eq(""), any()))
                .thenReturn(flowOf(phoneAuthEventResult))

            val result = repository.phoneSignIn("", mock())

            assertEquals(phoneAuthEventResult, result.first())
        }

    @Test
    fun `Get code sent event phone sign in auth from remote data source`() = runBlocking {
        val verificationId = "mock_verification_id"
        val phoneAuthEventResult =
            PhoneAuthEvent.CodeSent(verificationId)

        whenever(authRemoteDataSource.phoneSingIn(eq(""), any()))
            .thenReturn(flowOf(phoneAuthEventResult))

        val result = repository.phoneSignIn("", mock())

        assertEquals(phoneAuthEventResult, result.first())
    }

    @Test
    fun `Get verification failed event phone sign in auth from remote data source`() = runBlocking {
        val phoneAuthEventResult =
            PhoneAuthEvent.VerificationFailed(Exception("Error"))

        whenever(authRemoteDataSource.phoneSingIn(eq(""), any()))
            .thenReturn(flowOf(phoneAuthEventResult))

        val result = repository.phoneSignIn("", mock())

        assertEquals(phoneAuthEventResult, result.first())
    }

    //    Forgot and reset password
    @Test
    fun `Get valid reset password from remote data source`() = runBlocking {
        val resetResult = Result.Success(true)

        whenever(authRemoteDataSource.resetPassword(""))
            .thenReturn(flowOf(resetResult))

        val result = repository.resetPassword("")

        assertEquals(resetResult, result.first())
    }

    @Test
    fun `Get invalid reset password from remote data source`() = runBlocking {
        val resetResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.resetPassword(""))
            .thenReturn(flowOf(resetResult))

        val result = repository.resetPassword("")

        assertEquals(resetResult, result.first())
    }

    //    Verify email
    @Test
    fun `Get valid verify email from remote data source`() = runBlocking {
        val resetResult = true

        whenever(authRemoteDataSource.isEmailVerified())
            .thenReturn(flowOf(resetResult))

        val result = repository.isEmailVerified()

        assertEquals(resetResult, result.first())
    }

    @Test
    fun `Get invalid verify email from remote data source`() = runBlocking {
        val resetResult = false

        whenever(authRemoteDataSource.isEmailVerified())
            .thenReturn(flowOf(resetResult))

        val result = repository.isEmailVerified()

        assertEquals(resetResult, result.first())
    }

    //    Sign out
    @Test
    fun `Get sign out from remote data source`() = runBlocking {
        repository.signOut()
        verify(authRemoteDataSource).signOut()
    }
}
