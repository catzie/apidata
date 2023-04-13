package net.catzie.apidata

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import net.catzie.apidata.databinding.ActivityItemDetailBinding
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ItemDetailHostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    // Get secret API data
    val requestUrl = BuildConfig.TEST_REQUEST_URL
    val apiKey = BuildConfig.API_KEY

    // Create a GsonBuilder instance
    val gson = GsonBuilder().setPrettyPrinting().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_item_detail) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Run the get method
        getMethod()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getMethod() {

        // Launch coroutine where we'll run the network request
        GlobalScope.launch(Dispatchers.IO) {

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