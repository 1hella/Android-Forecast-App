package com.zenva.forecastapp.data.responsexml

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import com.zenva.forecastapp.data.responsejson.ForecastEntry
import com.zenva.forecastapp.data.responsejson.ForecastResponse
import com.zenva.forecastapp.data.responsejson.Main
import com.zenva.forecastapp.data.responsejson.Weather


// Xml annotation makes the class (de)serializable with TikXml
// The name should match the XML element, if not provided,
// class name in lower case will be used
@Xml(name = "weatherdata")
data class ForecastResponseXml(
    // The "forecast" element contains multiple "time" elements.
    // We want to store these "time" elements as ForecastEntries inside a List
    @Path("forecast")
    // Element annotation takes an XML element and puts it into another @Xml annotated class.
    // Conversion of multiple elements into a list is simple - just make the type to be a List.
    @Element(name = "time")
    val list: List<ForecastEntryXml>
) {
    fun toJSONForecastResponse(): ForecastResponse {
        return ForecastResponse(
            // Map function takes in objects of one type and returns objects of another type
            // Converting a List<ForecastEntryXml> to List<ForecastEntry>
            this.list.map { xmlEntry ->
                // Pass in the data gotten from XML into the JSON ForecastEntry
                ForecastEntry(
                    xmlEntry.dateText,
                    // XML temperature is "flattened", in JSON it's nested inside a Main object
                    Main(xmlEntry.temperature),
                    listOf(Weather(
                        xmlEntry.weather.description,
                        xmlEntry.weather.icon
                    ))
                )
            }
        )
    }
}