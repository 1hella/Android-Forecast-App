package com.zenva.forecastapp.data

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import com.zenva.forecastapp.data.responsejson.ForecastResponse
import com.zenva.forecastapp.data.responsexml.ForecastResponseXml
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = ""
const val UNIT_SYSTEM = "metric"

// Retrofit turns HTTP API into an interface which is simple to work with from the code.
// It then takes this interface and generates code behind the scenes which actually does
// all the work of structuring URLs and returning Kotlin classes instead of plain JSON strings.
interface OpenWeatherApiService {
    // This function will form the following URL:
    // http://api.openweathermap.org/data/2.5/forecast?q=Los%20Angeles,us
    // &units=imperial&appid=API_KEY_HERE

    // Fetching data from the API takes some time and we don't want to freeze the app
    // while waiting for the response.
    // We can use Kotlin's coroutines which simplify asynchronous operations a whole lot.
    // Prefixing the function with "suspend" allows us to wait until the response is gotten from the API.
    @GET("forecast")
    suspend fun getFutureWeather(
        @Query("q") cityAndCountryCode: String
    ): ForecastResponseXml

    // Kotlin doesn't support Java-like static class members.
    // Instead, whatever is put into a companion object can be accessed directly on the class.
    // e.g. OpenWeatherApiService.someFunction()
    companion object {
        // The invoke operator function will allow us to call it like this: OpenWeatherApiService()
        // It's exactly the same as if we called OpenWeatherApiService.invoke()

        // This adds a nice syntax - it looks like we're instantiating
        // a new object with a constructor, while in fact we're just calling a function.
        operator fun invoke(): OpenWeatherApiService {
            // Interceptors run before each request that the OkHttpClient makes.
            val apiKeyInterceptor = Interceptor { chain ->
                val url = chain.request()
                    // Get the current url
                    .url()
                    .newBuilder()
                    .addQueryParameter("appid", API_KEY)
                    .addQueryParameter("units", UNIT_SYSTEM)
                    .addQueryParameter("mode", "xml")
                    .build()

                // Set the request's URL to be the updated one
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                // Continue the request with the new URL
                return@Interceptor chain.proceed(request)
            }

            // Retrofit uses OkHttp behind the scenes.
            // Interceptors are actually a part of OkHttp, not of Retrofit directly.
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(ConnectivityInterceptor())
                .build()

            return Retrofit.Builder()
                // Set the client to be our custom instance with interceptors.
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                // Makes Retrofit automatically convert the JSON response to our Kotlin classes
                .addConverterFactory(TikXmlConverterFactory.create(getInitializedTikXml()))
                .build()
                .create(OpenWeatherApiService::class.java)
        }

        private fun getInitializedTikXml(): TikXml {
            return TikXml.Builder()
                // GSON ignores unconverted JSON by default.
                // TikXml, however, throws an exception if you ignore some parts of the XML
                // unless we specify that we don't wish it to do that.
                .exceptionOnUnreadXml(false)
                .build()
        }

    }
}