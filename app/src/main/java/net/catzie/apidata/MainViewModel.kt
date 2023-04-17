package net.catzie.apidata

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainViewModel : ViewModel() {

    // Get secret API data
    val requestUrl = BuildConfig.TEST_REQUEST_URL
    val apiKey = BuildConfig.API_KEY

    // Create a GsonBuilder instance as needed
    val gson by lazy { GsonBuilder().setPrettyPrinting().create() }

    fun getMethod() {

        // Launch coroutine where we'll run the network request
        viewModelScope.launch(Dispatchers.IO) {

            // Create reference to the API URL
            val url = URL(requestUrl)

            // Declare and initialize an HttpsURLConnection
            val httpsURLConnection = url.openConnection() as HttpsURLConnection

            // Setup up the HttpsURLConnection
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            ) // The format of response we want to get from the server
            httpsURLConnection.setRequestProperty("x-api-key", apiKey)
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false

            // Create reference to the responseCode
            val responseCode = httpsURLConnection.responseCode

            if (responseCode == HttpsURLConnection.HTTP_OK) { // Handle successful request

                // Get response from the request
                val response = httpsURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8

                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val prettyJson = gson.toJson(JsonParser.parseString(response))
                    Log.d("HTTPURLCONNECTION:", prettyJson)

                }

            } else {  // Handle failed request

                Log.e("HTTPURLCONNECTION", "error: " + responseCode.toString())
            }
        }
    }
}