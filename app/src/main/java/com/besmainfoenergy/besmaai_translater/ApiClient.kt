package com.besmainfoenergy.besmaai_translater

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Client API pour l'application BesmaAI de BesmaInfoEnergy.
 * Connecte l'application mobile au workflow n8n hébergé sur Render.
 */
object ApiClient {
    // URL de production pour votre service Render
    private const val WEBHOOK_URL = "https://besma-ai-translater.onrender.com/webhook/translate"

    // Configuration optimisée pour les temps de réponse de l'IA Gemini
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS) // Important : laisse le temps à Gemini de générer le texte
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * Envoie la demande de traduction à n8n
     * @param textToTranslate : Le message saisi par l'utilisateur
     * @param targetLanguage : La langue cible (ex: "Arabe", "Français", "Anglais")
     * @param callback : Gère l'affichage du résultat ou de l'erreur
     */
    fun sendTranslationRequest(textToTranslate: String, targetLanguage: String, callback: (String?) -> Unit) {
        try {
            // Création du JSON avec les clés attendues par votre nœud Gemini sur n8n
            val jsonPayload = JSONObject()
            jsonPayload.put("text", textToTranslate)     // Doit correspondre à {{ $json.body.text }}
            jsonPayload.put("language", targetLanguage) // Doit correspondre à {{ $json.body.language }}
            jsonPayload.put("app_name", "BesmaAI_Translater")

            val body = jsonPayload.toString().toRequestBody(JSON_MEDIA_TYPE)
            
            val request = Request.Builder()
                .url(WEBHOOK_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Erreur réseau (ex: pas d'internet ou serveur éteint)
                    callback("Erreur de connexion : ${e.localizedMessage}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    
                    if (response.isSuccessful && responseData != null) {
                        // Renvoie directement le texte traduit par Gemini
                        callback(responseData.trim())
                    } else if (response.code == 404) {
                        callback("Erreur 404 : Vérifiez que le workflow n8n est bien 'Active'.")
                    } else {
                        callback("Erreur serveur (${response.code}) : Réessayez dans 30 secondes.")
                    }
                }
            })
        } catch (e: Exception) {
            callback("Erreur technique : ${e.message}")
        }
    }
}
