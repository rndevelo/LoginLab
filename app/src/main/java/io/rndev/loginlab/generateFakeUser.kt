package io.rndev.loginlab

import io.rndev.loginlab.domain.User
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun generateFakeUser(): User {
    val uid = UUID.randomUUID().toString()
    val email = "user${Random.nextInt(1000, 9999)}@example.com"
    val displayName =
        listOf("Alice Johnson", "Carlos Pérez", "Emma Smith", "Luca Rossi", "Marta López").random()
    val photoUrl =
        "https://randomuser.me/api/portraits/${if (Random.nextBoolean()) "men" else "women"}/${
            Random.nextInt(
                1,
                100
            )
        }.jpg"
    val phoneNumber = "+34 6${Random.nextInt(10000000, 99999999)}"

    val now = System.currentTimeMillis()
    val creationTimestamp = now - TimeUnit.DAYS.toMillis(Random.nextLong(30, 365))
    val lastSignInTimestamp = creationTimestamp + TimeUnit.DAYS.toMillis(Random.nextLong(1, 29))

    return User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        phoneNumber = phoneNumber,
        creationTimestamp = creationTimestamp,
        lastSignInTimestamp = lastSignInTimestamp
    )
}

fun generateNullFakeUser(): User {

    return User(
        uid = null,
        email = null,
        displayName = null,
        photoUrl = null,
        phoneNumber = null,
        creationTimestamp = null,
        lastSignInTimestamp = null
    )
}