import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newct.R
import com.example.newct.WeatherActivity
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.core.content.ContextCompat.getSystemService


data class Weather(val temperature: Double)

interface WeatherApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<Weather>
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApiService = retrofit.create(WeatherApiService::class.java)

    private val _weatherData = MutableLiveData<Weather>()
    val weatherData: LiveData<Weather> = _weatherData

    private val notificationManager: NotificationManager by lazy {
        getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun fetchWeatherData(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            val response = weatherApiService.getWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val weather = response.body()
                _weatherData.value = weather

                weather?.let { weatherData ->
                    if (weatherData.temperature in 10.0..20.0) {
                        showNotification(weatherData.temperature)
                    }
                }
            } else {
                // Handle error case
            }
        }
    }

    private fun showNotification(temperature: Double) {
        val channelId = "weather_channel"
        val channelName = "Weather Notifications"
        val notificationId = 109

        val intent = Intent(getApplication(), WeatherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, 0)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(getApplication(), channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Weather Update")
            .setContentText("Current Temperature: $temperature°C")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "weather_channel"
            val channelName = "Weather Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}