import app.cash.turbine.test
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.Result
import io.rndev.loginlab.data.buildAuthRepositoryWith
import io.rndev.loginlab.feature.auth.UiEvent
import io.rndev.loginlab.feature.auth.login.LoginAction
import io.rndev.loginlab.feature.auth.login.LoginViewModel
import io.rndev.loginlab.testrules.CoroutinesTestRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `handleEmailSignIn is success`() = runTest {

        val vm = buildViewModelWith()

        vm.onAction(LoginAction.OnEmailSignIn)

        vm.uiState.test {
            assertEquals(vm.uiState.value, awaitItem())
        }

        vm.events.test {
            assertEquals(UiEvent.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `handleEmailSignIn is error`() = runTest {

        val vm = buildViewModelWith(authResult = Result.Error(Exception("Error")))

        vm.onAction(LoginAction.OnEmailSignIn)

        vm.uiState.test {
            assertEquals(vm.uiState.value, awaitItem())
        }

        vm.events.test {
            assertEquals(UiEvent.ShowError("Error"), awaitItem())
        }
    }


    private fun buildViewModelWith(
        authResult: Result<Boolean> = Result.Success(true),
        phoneAuthEvent: PhoneAuthEvent = PhoneAuthEvent.VerificationCompleted(
            flowOf(
                Result.Success(
                    true
                )
            )
        ),
        verifiedEmail: Boolean = true,
        googleToken: Result<String> = Result.Success("googleToken"),
        facebookToken: String = "facebookToken"
    ): LoginViewModel {
        val authRepository = buildAuthRepositoryWith(
            authResult = authResult,
            phoneAuthEvent = phoneAuthEvent,
            verifiedEmail = verifiedEmail,
            googleToken = googleToken,
            facebookToken = facebookToken
        )
        return LoginViewModel(authRepository)
    }
}