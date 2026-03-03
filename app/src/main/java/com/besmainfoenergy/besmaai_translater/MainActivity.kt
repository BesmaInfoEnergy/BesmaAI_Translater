package com.besmainfoenergy.besmaai_translater

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.besmainfoenergy.besmaai_translater.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = checkNotNull(_binding) { "Activity has been destroyed" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Action au clic du bouton
        binding.btnGenerate.setOnClickListener {
            val textToProcess = binding.etInput.text.toString().trim()
            
            if (textToProcess.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir un texte", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Récupérer la langue sélectionnée
            val selectedLang = when (binding.toggleGroup.checkedButtonId) {
                R.id.btnFr -> "Français"
                R.id.btnEn -> "Anglais"
                R.id.btnAr -> "Arabe"
                else -> "Français"
            }

            // Lancer l'animation de chargement
            startLoading()

            // Appeler n8n via notre ApiClient
            ApiClient.sendTranslationRequest(textToProcess, selectedLang) { response ->
                // Retourner sur le thread principal pour mettre à jour l'UI
                runOnUiThread {
                    stopLoading()
                    if (response != null) {
                        binding.tvResult.text = response
                    } else {
                        binding.tvResult.text = "Erreur : Impossible de joindre le serveur."
                    }
                }
            }
        }
    }

    private fun startLoading() {
        binding.btnGenerate.isEnabled = false
        binding.loader.visibility = View.VISIBLE
        binding.tvResult.text = "Génération en cours..."
    }

    private fun stopLoading() {
        binding.btnGenerate.isEnabled = true
        binding.loader.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
