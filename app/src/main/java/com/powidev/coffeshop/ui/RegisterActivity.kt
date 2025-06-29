package com.powidev.coffeshop.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.powidev.coffeshop.Activity.AdminActivity
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.data.local.User
import com.powidev.coffeshop.data.local.UserDatabase
import com.powidev.coffeshop.databinding.ActivityRegisterBinding
import com.powidev.coffeshop.manager.SessionManager
import com.powidev.coffeshop.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = UserDatabase.getInstance(this).userDao()
        userRepository = UserRepository(userDao)

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = User(
                        email = email,
                        password = password,
                        role = if (email.contains("@admin.")) "admin" else "user"
                    )

                    try {
                        if (userRepository.registerUser(user)) {
                            // Pasa el contexto (this@RegisterActivity) como primer parámetro
                            SessionManager.setCurrentUser(this@RegisterActivity, user)

                            val destination = if (SessionManager.isAdmin(this@RegisterActivity)) {
                                AdminActivity::class.java
                            } else {
                                MainActivity::class.java
                            }
                            startActivity(Intent(this@RegisterActivity, destination))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Error en el registro", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun redirectUser(role: String) {
        val destination = if (role == "admin") AdminActivity::class.java else MainActivity::class.java
        startActivity(Intent(this, destination))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}