package com.powidev.coffeshop.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.powidev.coffeshop.databinding.ActivityProfileBinding
import androidx.core.content.edit

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val listaIdiomas = arrayOf("English", "Español")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupSpinner()
        setVariable()
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
