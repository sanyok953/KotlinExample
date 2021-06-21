package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            if (user.login in map)
                throw IllegalArgumentException("A user with this email already exists")
            else
                map[user.login] = user
        }

    fun requestAccessCode(login: String): Unit {
        map[login.replace("""[^+\d]""".toRegex(), "")]?.let {
            println("HH * $login")
            it.requestAccessCode(login)
        }
    }

    fun loginUser(login: String, password: String): String? =
        if (login.trim() in map) {
            map[login.trim()]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
        } else {
            map[login?.replace("""[^+\d]""".toRegex(), "")]?.let {
                if (it.checkPassword(password)) it.userInfo
                else null
            }
        }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User =
        User.makeUser(fullName = fullName, phone = rawPhone)
            .also { user ->
                if (user.phone in map)
                    throw IllegalArgumentException("A user with this phone already exists")
                else
                    map[user.login] = user
            }
}