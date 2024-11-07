package com.example.googlemapsexample.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.googlemapsexample.utils.LocationUtils
import com.example.googlemapsexample.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(viewModel: LocationViewModel, locationUtils: LocationUtils) {
    val context = LocalContext.current  // Получаем текущий контекст для работы с локализацией
    // Получаем данные о местоположении пользователя и выбранной локации
    val userLocation by viewModel.userLocationData.collectAsState()
    val pickedLocation by viewModel.pickedLocationData.collectAsState()
    val userLocationAddress by viewModel.userLocationAddress.collectAsState()
    val pickedLocationAddress by viewModel.pickedLocationAddress.collectAsState()

    // Настроим начальную позицию камеры на карте (по умолчанию - локация пользователя)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0), 15f
        )  // Если нет данных о местоположении, будет использовать (0, 0)
    }

    Column {
        // Создаём карту с настройками камеры
        GoogleMap(
            modifier = Modifier.fillMaxSize(),  // Карта занимает весь экран
            cameraPositionState = cameraPositionState,  // Устанавливаем состояние камеры
            onMapClick = { latLng ->
                // Если пользователь кликает на карту, сохраняем выбранные координаты
                viewModel.updatePickedLocation(latLng)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))  // Добавляем немного пространства между картой и текстом

        // Отображаем информацию о текущем местоположении пользователя
        userLocation?.let {
            Text("Текущие координаты: ${it.latitude}, ${it.longitude}")
        }
        Text("Адрес: $userLocationAddress")  // Показываем адрес пользователя

        // Отображаем информацию о выбранной локации
        pickedLocation?.let {
            Text("Координаты выбранного места: ${it.latitude}, ${it.longitude}")
        }
        Text("Адрес выбранного места: $pickedLocationAddress")  // Показываем адрес выбранного места
    }

    // Запрашиваем местоположение, когда экран загружается
    LaunchedEffect(Unit) {
        val locationUtils = LocationUtils(context, viewModel)  // Создаём экземпляр для получения местоположения
        locationUtils.getLocation()  // Запрашиваем местоположение
    }
}
