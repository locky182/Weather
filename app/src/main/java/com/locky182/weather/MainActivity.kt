package com.locky182.weather


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.locky182.weather.data.WeatherModel
import com.locky182.weather.screens.DialogSearch
import com.locky182.weather.screens.MainCard
import com.locky182.weather.screens.TabLayout

import com.locky182.weather.ui.theme.WeatherTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import org.json.JSONObject

const val API_KEY = "6cddc29e119d42a6bbd84148242004"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {

                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }

                val dialogState = remember {
                    mutableStateOf(false)
                }


                val currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "10.0",
                        "",
                        "",
                        "10.0",
                        "10.0",
                        ""
                    ))
                }


                if (dialogState.value) { // Когда хотим показать диалог то в dialogState -true
                    DialogSearch(
                        dialogState,
                        onSubmit = { getData(it, context = this, daysList, currentDay)})
                }



                getData("Saratov", context = this, daysList, currentDay)

                Image(
                    painter = painterResource(R.drawable.sky),
                    contentDescription = "image1",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f)
                )

                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("Saratov", context = this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {dialogState.value = true})
                    TabLayout(daysList, currentDay)
                }


            }
        }
    }


    private fun getData(
        city: String,
        context: Context,
        daysList: MutableState<List<WeatherModel>>,
        currentDay: MutableState<WeatherModel>
        ) {


        val url = "https://api.weatherapi.com/v1/forecast.json?" +
                "key=$API_KEY" +
                "&q=$city" +
                "&days=10" +
                "&aqi=no" +
                "&alerts=no"

        val queue = Volley.newRequestQueue(context)

        CoroutineScope(Dispatchers.IO).launch {

            val request = StringRequest(

                Request.Method.GET,
                url,
                { response ->
                    val list = getWeatherByDays(response)
                    currentDay.value = list[0]//1 day
                    daysList.value = list//list of days

                },
                { error ->
                    Log.d("My", "Error $error")
                }


            )

            queue.add(request)

        }

    }

    private fun getWeatherByDays(response: String): List<WeatherModel> {//parsim
        if (response.isEmpty()) return listOf()
        val mainObject = JSONObject(response)

        val list = ArrayList<WeatherModel>()//временный лист для обьектов модели

        val city = mainObject.getJSONObject("location").getString("name")

        val days = mainObject
            .getJSONObject("forecast")
            .getJSONArray("forecastday")

        for (i in 0 until days.length()) { //проходим циклом по обьекту
            val item = days[i] as JSONObject // каждый день превращаем в обьект json
            list.add(
                WeatherModel(
                    city,
                    item.getString("date"),
                    currentTemp = "",
                    item
                        .getJSONObject("day")
                        .getJSONObject("condition")
                        .getString("text"),

                    item
                        .getJSONObject("day")
                        .getJSONObject("condition")
                        .getString("icon"),

                    item
                        .getJSONObject("day")

                        .getString("maxtemp_c"),

                    item
                        .getJSONObject("day")

                        .getString("mintemp_c"),

                    item.getJSONArray("hour").toString()
                )
            )
        }
        list[0] = list[0].copy(
            time = mainObject.getJSONObject("current").getString("last_updated"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c")
        )
        return list
    }
}

