package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.Locale

private lateinit var binding: ActivityMainBinding
private lateinit var response: String

class MainActivity : AppCompatActivity() {

    // Red Rock Canyon
    val LAT: String = "36.19"
    val LONG: String = "115.43"
    val API: String = "3d3e2baeef5802544aa75b8f728cafca"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(IO).launch {
            callAPI()
        }

    }

    private suspend fun callAPI() {
        val result = getResultFromAPI()
        populateInfo(result)
    }

    private suspend fun getResultFromAPI(): String {
        //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
        try {
            val myURL = "https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LONG&appid=$API&units=imperial"
            response = URL(myURL).readText(
                Charsets.UTF_8
            )
        } catch (e: java.lang.Exception) {
            response = "ERR: $e"
        }

        return response
    }

    private suspend fun populateInfo(incomingJSON: String) {
        withContext(Main) {
            val jsonObject = JSONObject(incomingJSON)
            val main = jsonObject.getJSONObject("main")
            val wind = jsonObject.getJSONObject("wind")
            val rainArray = jsonObject.optJSONObject("rain")
            val overviewArray = jsonObject.getJSONArray("weather")

            val temp = main.getString("temp")
            val wSpeed = wind.getString("speed")
            val rainPHr = if (rainArray != null) rainArray.getString("1h") else "0"
            val descrip = overviewArray.getJSONObject(0).getString("description")

            // Change background and text color based on descrip
            when (descrip.lowercase(Locale.ROOT)) {
                "snow" -> {
                    binding.root.setBackgroundColor(resources.getColor(R.color.grey))
                    binding.txtTemp.setTextColor(resources.getColor(R.color.black))
                    binding.txtOver.setTextColor(resources.getColor(R.color.black))
                    binding.txtRain.setTextColor(resources.getColor(R.color.black))
                    binding.txtWind.setTextColor(resources.getColor(R.color.black))
                }
                "rain" -> {
                    binding.root.setBackgroundColor(resources.getColor(R.color.greyblue))
                    binding.txtTemp.setTextColor(resources.getColor(R.color.white))
                    binding.txtOver.setTextColor(resources.getColor(R.color.white))
                    binding.txtRain.setTextColor(resources.getColor(R.color.white))
                    binding.txtWind.setTextColor(resources.getColor(R.color.white))
                }
                else -> {
                    binding.root.setBackgroundColor(resources.getColor(R.color.blue))
                    binding.txtTemp.setTextColor(resources.getColor(R.color.white))
                    binding.txtOver.setTextColor(resources.getColor(R.color.white))
                    binding.txtRain.setTextColor(resources.getColor(R.color.white))
                    binding.txtWind.setTextColor(resources.getColor(R.color.white))
                }
            }

            binding.txtTemp.text = "$temp \u2109"
            binding.txtOver.text = descrip
            binding.txtRain.text = "$rainPHr inches of rain in the next hour"
            binding.txtWind.text = "$wSpeed mph"


        }
    }
}
