package com.locky182.weather.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.locky182.weather.R
import com.locky182.weather.data.WeatherModel
import com.locky182.weather.ui.theme.BlueLight
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(
    currentDay: MutableState<WeatherModel>,
    onClickSync: () -> Unit,
    onClickSearch: () -> Unit

) {


    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(BlueLight)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp)
                    )

                    AsyncImage(
                        model = "https:" + currentDay.value.icon,
                        contentDescription = "imageWeather",
                        modifier = Modifier
                            .size(35.dp)
                            .padding(end = 8.dp)
                    )

                }

                Text(
                    text = currentDay.value.city,
                    fontSize = 24.sp,
                    color = (Color.White)
                )

                Text(
                    text = if (currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() + "°c"
                    else
                        "${currentDay.value.maxTemp.toFloat().toInt()}°c " +
                                "/${currentDay.value.minTemp.toFloat().toInt()}°c",
                    fontSize = 65.sp,
                    color = (Color.White)
                )

                Text(
                    text = currentDay.value.condition,
                    fontSize = 15.sp,
                    color = (Color.White)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = { onClickSearch.invoke() }) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "image3",
                            tint = Color.White
                        )


                    }

                    Text(
                        text =
                        "${currentDay.value.maxTemp.toFloat().toInt()}°c/" +
                                "${currentDay.value.minTemp.toFloat().toInt()}°c",
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )

                    IconButton(
                        onClick = {
                            onClickSync.invoke()// вызов функции


                        }) {
                        Icon(
                            painter = painterResource(R.drawable.sync),
                            contentDescription = "image4",
                            tint = Color.White
                        )

                    }

                }


            }

        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAY")

    val pagerState = rememberPagerState()// состояние списка пейджера снизу
    val tabIndex = pagerState.currentPage// индекс
    val coroutineScope = rememberCoroutineScope() //анимация в другом потоке



    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(
                RoundedCornerShape(5.dp)

            )

    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabIndex]),

                    height = 2.dp,
                    color = Color.White
                )

            },
            containerColor = BlueLight
        ) {
            tabList.forEachIndexed { index, textList ->

                Tab(
                    selected = false,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(text = textList)
                    }
                )
            }
        }

        HorizontalPager(
            count = tabList.size,
            state = pagerState,
            modifier = Modifier.weight(1.0f)
        ) { index ->

            val list = when(index) {
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value

            }
            MainList(list,currentDay)


        }

    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()//esli pesto to vibrato pus-toy spisok
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°c",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }

    return list
}