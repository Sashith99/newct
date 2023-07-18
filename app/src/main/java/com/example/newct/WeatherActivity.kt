package com.example.newct

import WeatherViewModel
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

class WeatherActivity : AppCompatActivity() {
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        setContent {
            WeatherScreen(weatherViewModel)
        }
    }
}




@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    val weatherData by weatherViewModel.weatherData.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        weatherData?.let { weather ->
            Text(text = "Current Temperature: ${weather.temperature}°C")
        }

        Button(
            onClick = {
                weatherViewModel.fetchWeatherData(
                    GLASGOW_LATITUDE,
                    GLASGOW_LONGITUDE,
                    "d5aa07fb54d526d1095827c15e82fb6e"
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Refresh")
        }
    }
}

private const val GLASGOW_LATITUDE = 55.8642
private const val GLASGOW_LONGITUDE = -4.2518

