package io.rndev.loginlab.datasource

import com.facebook.login.LoginManager

interface FacebookLoginHandler {
    fun getLoginActivityResultContract(): LoginManager.FacebookLoginActivityResultContract
    fun registerCallback(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit
    )
}