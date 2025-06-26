package com.powidev.coffeshop.repository

import com.powidev.coffeshop.data.local.User
import com.powidev.coffeshop.data.local.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        val existingUser = userDao.getUserByEmail(user.email)
        return if (existingUser == null) {
            userDao.registerUser(user)
            true
        } else {
            false // ya existe el usuario
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        val user = userDao.loginUser(email, password)
        return user != null
    }
}
