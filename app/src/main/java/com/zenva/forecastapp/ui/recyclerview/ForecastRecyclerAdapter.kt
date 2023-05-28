package com.zenva.forecastapp.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zenva.forecastapp.R
import com.zenva.forecastapp.data.responsejson.ForecastEntry

// Adapters are used for populating the RecyclerView with data.
class ForecastRecyclerAdapter(
    private val forecastEntries: List<ForecastEntry>
) : RecyclerView.Adapter<ForecastRecyclerAdapter.ForecastEntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastEntryViewHolder {
        // Similar to how we must inflate the layout for the menu inside the Toolbar, we also have to
        // inflate the layout file for individual ForecastEntries and then pass it to the ViewHolder to display it.
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_forecast_entry, parent, false)
        return ForecastEntryViewHolder(inflatedView)
    }

    override fun getItemCount() = forecastEntries.size

    override fun onBindViewHolder(holder: ForecastEntryViewHolder, position: Int) {
        // Get the appropriate ForecastEntry.
        // The "position" parameter signifies the index inside the list whose data is currently needed
        // to populate the ViewHolders layout.
        val currentForecastEntry = forecastEntries[position]
        holder.bind(currentForecastEntry)
    }

    // ViewHolders hold reference to the views from the XML layout.
    class ForecastEntryViewHolder(
        private val containerView: View
    ) : RecyclerView.ViewHolder(containerView) {

        fun bind(forecastEntry: ForecastEntry) {
            containerView.findViewById<TextView>(R.id.time_textview).text = forecastEntry.dateText
            containerView.findViewById<TextView>(R.id.weather_condition_textview).text =
                forecastEntry.weather[0].description
            containerView.findViewById<TextView>(R.id.temperature_textview).text =
                containerView.context.getString(
                    R.string.forecast_temp_message,
                    forecastEntry.main.temp.toString()
                )

            val iconId = forecastEntry.weather[0].icon
            //Changes: use Glide instead of GlideApp below
            Glide.with(containerView)
                .load("https://openweathermap.org/img/w/$iconId.png")
                .into(containerView.findViewById(R.id.weather_condition_imageview))
        }
    }
}