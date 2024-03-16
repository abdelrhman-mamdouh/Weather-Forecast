package com.example.weatherguide

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class FavoritesFragment : Fragment() {

    private lateinit var editTextSearch: EditText
    private lateinit var locationListView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        editTextSearch = view.findViewById(R.id.editTextSearch)
        locationListView = view.findViewById(R.id.locationListView)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1)
        locationListView.adapter = adapter

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used, but required to implement TextWatcher
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used, but required to implement TextWatcher
            }

            override fun afterTextChanged(s: Editable?) {
                searchLocations(s.toString())
            }
        })

        return view
    }

    private fun searchLocations(query: String) {
        // Cancel any previous search coroutine job
        CoroutineScope(Dispatchers.IO).launch {
            val result = mutableListOf<String>() // Temporary storage for results
            if (query.isNotEmpty()) {
                val url = URL("https://nominatim.openstreetmap.org/search?q=$query&format=json")
                val connection = url.openConnection() as HttpURLConnection
                try {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var inputLine: String?
                    while (reader.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    reader.close()
                    val jsonArray = JSONArray(response.toString())
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val displayName = jsonObject.getString("display_name")
                        result.add(displayName)
                    }
                } finally {
                    connection.disconnect()
                }
            }
            // Switch back to the main thread before updating the adapter
            withContext(Dispatchers.Main) {
                adapter.clear()
                adapter.addAll(result)
            }
        }
    }
}


