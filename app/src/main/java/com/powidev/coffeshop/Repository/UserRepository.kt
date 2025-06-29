package com.powidev.coffeshop.repository

import com.powidev.coffeshop.data.local.User
import com.powidev.coffeshop.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val existingUser = userDao.getUserByEmail(user.email)
                if (existingUser == null) {
                    userDao.registerUser(user) > 0  // Ahora funciona con el Long retornado
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun loginUser(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.loginUser(email, password)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.getUserByEmail(email)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun createDefaultAdmin() {
        withContext(Dispatchers.IO) {
            try {
                val adminEmail = "admin@coffeshop.com"
                if (userDao.getUserByEmail(adminEmail) == null) {
                    val adminUser = User(
                        email = adminEmail,
                        password = "admin123",
                        role = "admin"
                    )
                    userDao.registerUser(adminUser)
                }
            } catch (e: Exception) {
                // Log error if needed
            }
        }
    }
}