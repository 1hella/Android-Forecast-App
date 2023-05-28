package com.zenva.forecastapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenva.forecastapp.data.ConnectivityInterceptor
import com.zenva.forecastapp.data.OpenWeatherApiService
import com.zenva.forecastapp.data.responsejson.ForecastEntry
import kotlinx.coroutines.launch

class ForecastViewModel : ViewModel() {
    private val openWeatherApiService = OpenWeatherApiService()

    // MutableLiveData is an observable "data holder" which can be changed, hence "mutable".
    // LiveData is lifecycle-aware, meaning that it does the book keeping for us and
    // we don't have to deal with cleaning up resources ourselves to prevent memory leaks.
    private val _forecastEntries = MutableLiveData<List<ForecastEntry>>()

    // We will finally want to observe this data inside the Activity to display a list.
    // However, it shouldn't be changed (mutated) from outside the ViewModel!
    // Other classes, such as the Activity,
    // should only have the option to passively observe the data.
    // To accomplish this, we can expose only LiveData (non-mutable),
    // and leave the MutableLiveData private.
    val forecastEntries: LiveData<List<ForecastEntry>>
        get() = _forecastEntries

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun loadForecastEntries(cityAndCountryCode: String) = viewModelScope.launch {
        try {
            // Reset the error message
            _errorMessage.postValue(null)
            // Code is changed here, because we're using Retrofit's built-in coroutine support,
            // we simply don't need to call ".await()" on the result of getFutureWeather.
            val forecastResponse = openWeatherApiService.getFutureWeather(cityAndCountryCode)
            // Posting a value will notify all of the observers (e.g. an Activity) to update.
            // In our case, we will update the UI with new forecasts.
            _forecastEntries.postValue(forecastResponse.toJSONForecastResponse().list)
        } catch (e: retrofit2.HttpException) {
            // 404 HTTP error code means "not found". In our case, the city name is not found.
            if (e.code() == 404) _errorMessage.postValue("Please, input a valid city name")
            else _errorMessage.postValue(e.message())
        } catch (e: ConnectivityInterceptor.NoConnectivityException) {
            _errorMessage.postValue(e.message)
        }
    }
}