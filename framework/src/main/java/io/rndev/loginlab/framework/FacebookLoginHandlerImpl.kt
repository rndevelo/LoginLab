package io.rndev.loginlab.framework

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.rndev.loginlab.datasource.FacebookLoginHandler
import javax.inject.Inject

class FacebookLoginHandlerImpl @Inject constructor(
    private val loginManager: LoginManager,
    private val callbackManager: CallbackManager,
) : FacebookLoginHandler {

    override fun getLoginActivityResultContract() =
        loginManager.createLogInActivityResultContract(callbackManager, null)

    override fun registerCallback(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    ) {
        loginManager.registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) = onSuccess(result.accessToken.token)
                override fun onCancel() = onCancel()
                override fun onError(error: FacebookException) =
                    onError(error.localizedMessage ?: "Unknown error")
            }
        )
    }
}
