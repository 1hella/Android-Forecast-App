package com.zenva.forecastapp.data.responsejson

import com.google.gson.annotations.SerializedName

data class ForecastEntry (
        @SerializedName("dt_txt")
    val dateText: String,
        val main: Main,
        val weather: List<Weather>
)