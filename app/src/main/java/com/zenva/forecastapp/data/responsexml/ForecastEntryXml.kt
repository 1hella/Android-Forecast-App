package com.zenva.forecastapp.data.responsexml

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

// This class holds elements and attributes from the "time" element
@Xml
data class ForecastEntryXml(
    // As opposed to the JSON API, XML comes with a "from" & "to" time range.
    // We want the "from" starting time of the forecast.
    // This is defined as an attribute called "from" present on the time element.
    @Attribute(name = "from")
    val dateText: String,

    // The @Path attribute can also be used for flattening of hierarchies.
    // Temperature is stored inside a "temperature" element
    // which is nested within the current "time" element.
    @Path("temperature")
    @Attribute(name = "value")
    val temperature: Double,

    // Sometimes it's better not to flatten the hierarchy
    // if we want to group values together inside a class.

    // WeatherXml icon ID and description are inside a "symbol" element.
    @Element(name = "symbol")
    val weather: WeatherXml
)