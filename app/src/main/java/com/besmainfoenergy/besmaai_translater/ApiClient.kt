package com.besmainfoenergy.besmaai_translater

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    // Remplacer par l'URL de votre tunnel (localtunnel, ngrok ou render)
    private const val WEBHOOK_URL = "https://votre-url-n8n.loca.lt/webhook/translate"

    // Configuration du client avec des délais adaptés pour l'IA
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS) // On laisse 1 min à l'IA pour répondre
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * Envoie la demande à n8n
     * @param text : Le texte à traiter
     * @param lang : La langue cible (FR, EN, AR)
     * @param callback : Ce qu'on fait quand la réponse arrive
     */
    fun sendTranslationRequest(text: String, lang: String, callback: (String?) -> Unit) {
        // Préparation du JSON pour n8n
        val jsonPayload = JSONObject()
        jsonPayload.put("content", text)
        jsonPayload.put("target_lang", lang)
        jsonPayload.put("app_name", "BesmaAI_Translater")

        val body = jsonPayload.toString().toRequestBody(JSON_MEDIA_TYPE)
        
        val request = Request.Builder()
            .url(WEBHOOK_URL)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // En cas d'erreur réseau
                callback("Erreur de connexion : ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    // On renvoie la réponse propre de n8n
                    callback(responseData)
                } else {
                    callback("Erreur serveur : ${response.code}")
                }
            }
        })
    }
}
