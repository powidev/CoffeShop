package com.powidev.coffeshop.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.powidev.coffeshop.Activity.AdminActivity
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.data.local.User
import com.powidev.coffeshop.databinding.ActivityRegisterBinding
import com.powidev.coffeshop.manager.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Complete todos los campos")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Correo inválido")
                return@setOnClickListener
            }

            if (password.length < 6) {
                showToast("La contraseña debe tener al menos 6 caracteres")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast("Las contraseñas no coinciden")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val role = if (email.contains("@admin.")) "admin" else "user"
                        val user = User(email, password, role)
                        SessionManager.setCurrentUser(this, user)
                        redirectUser(role)
                    } else {
                        showToast("Error: ${task.exception?.message}")
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
