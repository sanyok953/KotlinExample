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
            //println("HH * $login")
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

    fun importUsers(list: List<String>): List<User> {
        val userList = arrayListOf<User>()
        list.forEach {
            val (fullName, email, access, phone) =
                it.split(";").map { it.trim().ifBlank { null } }.subList(0, 4)
            userList.add(User.makeUser(
                fullName = fullName!!, email = email, phone = phone,
                password = access!!.substringAfter(":"), salt = access.substringBefore(":")
            )
                .also { map[it.login] = it })
        }
        return userList
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