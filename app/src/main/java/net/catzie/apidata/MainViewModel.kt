package net.catzie.apidata

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request

class MainViewModel : ViewModel() {

    val requestUrl = BuildConfig.TEST_REQUEST_URL
    val apiKey = BuildConfig.API_KEY
    val apiKeyHeader = Headers.Builder().add("x-api-key", apiKey).build()

    fun getViaOkhttp() {

        // Create instance of OkHttpClient
        val okhttpClient = OkHttpClient()

        // Launch a Coroutine (Omitting Dispatchers.IO will throw NetworkOnMainThreadException)
        viewModelScope.launch(Dispatchers.IO) {

            // Build a request with your API url and header with the api key
            val request = Request.Builder()
                .url(requestUrl)
                .headers(apiKeyHeader)
                .build()

            // Execute the network request and get the response
            val response = okhttpClient.newCall(request).execute()

            // Print out the response
            Log.d("OKHTTP", "response: ${response.body?.string()}")

        }
    }
}