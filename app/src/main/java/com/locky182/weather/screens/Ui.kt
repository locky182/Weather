package com.locky182.weather.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.locky182.weather.data.WeatherModel
import com.locky182.weather.ui.theme.BlueLight

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        itemsIndexed(
            list
        ) { _, item ->
            ListItem(item, currentDay)
        }


    }

}


@Composable
fun ListItem(itemWeather: WeatherModel, currentDay: MutableState<WeatherModel>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, top = 4.dp, end = 1.dp, bottom = 3.dp)
            .clickable {
                if (itemWeather.hours.isEmpty()) return@clickable
                currentDay.value = itemWeather
            }
            .clip(RoundedCornerShape(3.dp)),
        colors = CardDefaults.cardColors(BlueLight),

        )

    {

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.padding(start = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = itemWeather.time)
                Text(text = itemWeather.condition, color = Color.White)

            }

            Text(
                text = itemWeather
                    .currentTemp
                    .ifEmpty { "${itemWeather.minTemp.toFloat().toInt()}°c" +
                            "/${itemWeather.maxTemp.toFloat().toInt()}°c" },//inline f ifEmpty
                //если что то есть то записывает current, а если нет то все остальное

                color = Color.White,
                style = TextStyle(fontSize = 25.sp)
                )

            AsyncImage(
                model = "https:${itemWeather.icon}",
                 contentDescription = "image5",
                modifier = Modifier.size(35.dp)
            )


        }

    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {

    val dialogText = remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = {dialogState.value = false },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false  }) {
                Text(text = "ok")
                
            }
        },
        
        dismissButton = {
            TextButton(onClick = { dialogState.value = false }) {
                Text(text = "cancel")
                
            }
        },

        title = {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(text = "input name of city: " )

                TextField(
                    value = dialogText.value ,
                    onValueChange = {dialogText.value = it} )

            }

        }
    )




}