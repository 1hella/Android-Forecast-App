package com.zenva.forecastapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zenva.forecastapp.R
import com.zenva.forecastapp.ui.recyclerview.ForecastRecyclerAdapter

class ForecastActivity : AppCompatActivity() {

    private lateinit var viewModel: ForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        // We want to use the Toolbar from the layout as the ActionBar
        setSupportActionBar(findViewById(R.id.toolbar))

        // ViewModelProvider grants us a ForecastViewModel tied to this Activity.
        // If the activity is recreated (because of an orientation change, let's say),
        // we'll obtain the same instance of ForecastViewModel as we've had before (data is not lost).
        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)

        bindUI()
        bindErrorMessage()

//        // A quick and dirty way to launch a coroutine, so that fetching data from the API doesn't freeze the app
//        // You should never launch coroutines on GlobalScope from an activity - more on that later.
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                // Here we simply don't have do call "await()", as opposed to the video
//                val response = OpenWeatherApiService().getFutureWeather("San Diego")
//                findViewById<TextView>(R.id.weather_placeholder_textview).text =
//                    response.list[0].main.temp.toString()
//            }
//            catch(e: ConnectivityInterceptor.NoConnectivityException) {
//                Toast.makeText(this@ForecastActivity, e.message,
//                    Toast.LENGTH_LONG).show()
//            }
//        }
    }

    private fun bindUI() {
        // Observers of LiveData run whenever the data changes.
        viewModel.forecastEntries.observe(this, Observer { forecastEntries ->
            hideLoading()

            val forecastAdapter = ForecastRecyclerAdapter(forecastEntries)
            // apply is Kotlin's higher-order function allowing us to access multiple members of an object
            // without redundantly specifying its name multiple times in a row.
            findViewById<RecyclerView>(R.id.forecast_recyclerview).apply {
                adapter = forecastAdapter
                // LinearLayoutManager shows items below each other
                layoutManager = LinearLayoutManager(this@ForecastActivity)
            }
        })
    }

    private fun bindErrorMessage() {
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            val errorMessageTextView = findViewById<TextView>(R.id.error_message_textview)
            if (errorMessage == null) {
                errorMessageTextView.visibility = View.GONE
                return@Observer
            }
            showErrorMessage()
            errorMessageTextView.text = errorMessage
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Make forecast_menu to be displayed as the menu in the Toolbar
        menuInflater.inflate(R.menu.forecast_menu, menu)

        // Get the action_search menu item and store its SearchView in a variable
        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showLoading()

                // ?: is a null coalescing operator
                // Either pass in non-null query or return from the function
                viewModel.loadForecastEntries(query ?: return false)

                //Set the title of the toolbar
                supportActionBar?.title = query

                // If the SearchView isn't showing as an icon yet, make it so.
                if (!searchView.isIconified)
                    searchView.isIconified = true
                // Collapse the search view and show only the icon
                searchMenuItem.collapseActionView()
                return false
            }

            // Don't do anything on text change
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        // Display the menu
        return true
    }

    private fun showLoading() {
        // There's no need to hide the error_message_textview explicitly.
        // It's hidden in the errorMessage's Observer - showLoading() is called before initiating
        // loadForecastEntries() which sets the errorMessage to null in the ViewModel.
        findViewById<RecyclerView>(R.id.forecast_recyclerview).visibility = View.GONE
        findViewById<Group>(R.id.group_loading).visibility = View.VISIBLE
    }

    private fun hideLoading() {
        findViewById<RecyclerView>(R.id.forecast_recyclerview).visibility = View.VISIBLE
        findViewById<Group>(R.id.group_loading).visibility = View.GONE
    }

    private fun showErrorMessage() {
        findViewById<RecyclerView>(R.id.forecast_recyclerview).visibility = View.GONE
        findViewById<Group>(R.id.group_loading).visibility = View.GONE
        findViewById<TextView>(R.id.error_message_textview).visibility = View.VISIBLE
    }

}