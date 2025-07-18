package io.rndev.loginlab.domain

data class User(
    val uid: String?,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val phoneNumber: String?,
    val creationTimestamp: Long?,
    val lastSignInTimestamp: Long?
)