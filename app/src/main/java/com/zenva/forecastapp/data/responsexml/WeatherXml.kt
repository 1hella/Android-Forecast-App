package com.zenva.forecastapp.data.responsexml

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class WeatherXml(
    @Attribute(name = "name")
    val description: String,
    @Attribute(name = "var")
    val icon: String
)