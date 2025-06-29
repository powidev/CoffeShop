package com.powidev.coffeshop.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.powidev.coffeshop.Activity.AdminActivity
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.data.local.UserDatabase
import com.powidev.coffeshop.databinding.ActivityLoginBinding
import com.powidev.coffeshop.manager.SessionManager
import com.powidev.coffeshop.repository.UserRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = UserDatabase.getInstance(this).userDao()
        userRepository = UserRepository(userDao)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userRepository.loginUser(email, password)
                    if (user != null) {
                        // Pasa el contexto (this@LoginActivity) como primer parámetro
                        SessionManager.setCurrentUser(this@LoginActivity, user)

                        val destination = if (SessionManager.isAdmin(this@LoginActivity)) {
                            AdminActivity::class.java
                        } else {
                            MainActivity::class.java
                        }
                        startActivity(Intent(this@LoginActivity, destination))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goToRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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