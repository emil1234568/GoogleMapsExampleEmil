package com.example.googlemapsexample.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.googlemapsexample.utils.LocationUtils
import com.example.googlemapsexample.viewmodel.LocationViewModel

@Composable
fun MainScreen(
    context: Context,  // Это контекст для работы с приложением
    locationUtils: LocationUtils,  // Это наш класс, который работает с локацией
    viewModel: LocationViewModel,  // ViewModel для управления состоянием
    navHostController: NavHostController  // Навигация для перехода на другие экраны
) {
    // Получаем состояние из ViewModel (это нужно для отображения локации пользователя)
    val pickedLocation by viewModel.pickedLocationData.collectAsState()
    val pickedLocationAddress by viewModel.pickedLocationAddress.collectAsState()
    val userLocationData by viewModel.userLocationData.collectAsState()
    val userLocationAddress by viewModel.userLocationAddress.collectAsState()

    // Логика запроса разрешений на доступ к местоположению
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permission ->
            // Если разрешения получены, запрашиваем местоположение
            if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permission[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                locationUtils.getLocation()  // Тут можно запросить местоположение
            } else {
                // Если разрешения не получены, показываем Toast
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    )) {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    // Основной экран
    Column(
        modifier = Modifier.fillMaxSize(),  // Заполняем весь экран
        verticalArrangement = Arrangement.Center,  // Выравнивание по вертикали
        horizontalAlignment = Alignment.CenterHorizontally  // Выравнивание по горизонтали
    ) {
        // Печатаем информацию о местоположении пользователя
        if (userLocationData != null && userLocationAddress == "") {
            Text("${userLocationData!!.latitude} ${userLocationData!!.longitude}")
        } else if (userLocationData != null && userLocationAddress != "") {
            Text(userLocationAddress)  // Если адрес найден, показываем его
        } else {
            Text("No user location")  // Если локация не найдена
        }

        // Печатаем информацию о выбранной локации
        if (pickedLocation != null && pickedLocationAddress == "") {
            Text("${pickedLocation!!.latitude} ${pickedLocation!!.longitude}")
        } else if (pickedLocation != null && pickedLocationAddress != "") {
            Text(pickedLocationAddress)  // Адрес выбранной локации
        } else {
            Text("No picked location")  // Если локация не выбрана
        }

        Spacer(Modifier.height(20.dp))  // Добавляем отступ

        // Кнопка для выбора местоположения
        Button(
            onClick = {
                // Запрашиваем разрешения
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                // Переход к экрану карты
                navHostController.navigate(route = Graph.MAP_SCREEN)
            }
        ) {
            Text("Pick location")  // Текст на кнопке
        }
    }
}
