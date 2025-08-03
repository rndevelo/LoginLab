import com.facebook.CallbackManager
import io.rndev.loginlab.PhoneAuthEvent
import io.rndev.loginlab.data.buildAuthRepositoryWith
import io.rndev.loginlab.feature.auth.login.LoginViewModel
import io.rndev.loginlab.testrules.CoroutinesTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import io.rndev.loginlab.Result

@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private fun buildViewModelWith(
        authResult: Result<Boolean>,
        phoneAuthEvent: PhoneAuthEvent,
        verifiedEmail: Boolean,
        googleToken: Result<String>
    ): LoginViewModel {
        val authRepository = buildAuthRepositoryWith(
            authResult = authResult,
            phoneAuthEvent = phoneAuthEvent,
            verifiedEmail = verifiedEmail,
            googleToken = googleToken
        )
        return LoginViewModel(authRepository, CallbackManager, loginManager)
    }

}