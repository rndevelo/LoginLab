package io.rndev.loginlab.data

import io.rndev.loginlab.Result
import io.rndev.loginlab.User
import io.rndev.loginlab.data.datasource.AuthRemoteDataSource
import io.rndev.loginlab.data.datasource.TokenRemoteDataSource
import io.rndev.loginlab.domain.generateFakeUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @Mock
    private lateinit var authRemoteDataSource: AuthRemoteDataSource

    @Mock
    private lateinit var tokenRemoteDataSource: TokenRemoteDataSource

    private lateinit var repository: AuthRepositoryImpl


    @Before
    fun setUp() {
        repository =
            AuthRepositoryImpl(
                authRemoteDataSource = authRemoteDataSource,
                tokenRemoteDataSource = tokenRemoteDataSource
            )
    }

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

    @Test
    fun `Get valid email auth from remote data source`() = runBlocking {
        val authResult = Result.Success(true)

        whenever(authRemoteDataSource.emailSignIn(any(), any())).thenReturn(flowOf(authResult))

        val result = repository.emailSignIn(any(), any())

        assertEquals(authResult, result.first())
    }

    @Test
    fun `Get invalid email auth from remote data source`() = runBlocking {
        val authResult = Result.Error(Exception("Error"))

        whenever(authRemoteDataSource.emailSignIn(any(), any())).thenReturn(flowOf(authResult))

        val result = repository.emailSignIn("", "")

        assertEquals(authResult, result.first())
    }




}