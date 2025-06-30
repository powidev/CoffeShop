package com.powidev.coffeshop.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.powidev.coffeshop.databinding.ActivityProfileBinding
import androidx.core.content.edit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.powidev.coffeshop.R
import com.powidev.coffeshop.manager.SessionManager
import com.powidev.coffeshop.ui.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val listaIdiomas = arrayOf("English", "Español")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val adminFab = findViewById<FloatingActionButton>(R.id.adminFab)

        // Verificar si el usuario es administrador usando SessionManager
        val isAdmin = SessionManager.isAdmin(this)
        if (isAdmin) {
            adminFab.visibility = View.VISIBLE
            adminFab.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            adminFab.visibility = View.GONE
        }

        setupUI()
        setupSpinner()
        setVariable()
    }

    private fun setupUI() {
        // Configuración del botón "Volver"
        binding.backBtn.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }

        // Configuración del botón "Cerrar Sesión"
        binding.logoutButton.setOnClickListener {
            logout()
        }

        // Mostrar el botón admin si el usuario es administrador
        val isAdmin = SessionManager.isAdmin(this)
        if (isAdmin) {
            binding.adminFab.visibility = View.VISIBLE
            binding.adminFab.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            binding.adminFab.visibility = View.GONE
        }
    }

    private fun logout() {
        // Limpiar la sesión del usuario
        SessionManager.logout(this)

        // Mostrar un mensaje de confirmación
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    private fun setupSpinner() {
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaIdiomas)
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        binding.languagesSpinner.adapter = adapter

        val currentLanguage = preferences.getString("language_preference", "en")
        binding.languagesSpinner.setSelection(listaIdiomas.indexOf(if (currentLanguage == "es") "Español" else "English"))

        binding.languagesSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val idioma = when (listaIdiomas[position]) {
                        "English" -> {
                            preferences.edit { putString("language_preference", "en") }
                            "en"
                        }

                        "Español" -> {
                            preferences.edit { putString("language_preference", "es") }
                            "es"
                        }

                        else -> preferences.getString("language_preference", "en")
                    }

                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(idioma))
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}
