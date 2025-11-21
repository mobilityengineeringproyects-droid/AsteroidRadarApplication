package com.udacity.asteroidradar.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udacity.asteroidradar.Asteroid

@Composable
fun AsteroidListScreen(
    asteroids: List<Asteroid>,
    onAsteroidClicked: (Asteroid) -> Unit
) {
    LazyColumn {
        items(asteroids) { asteroid ->
            AsteroidItem(asteroid = asteroid, onAsteroidClicked = onAsteroidClicked)
        }
    }
}

@Composable
fun AsteroidItem(
    asteroid: Asteroid, 
    onAsteroidClicked: (Asteroid) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onAsteroidClicked(asteroid) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = asteroid.codename)
            Text(text = asteroid.closeApproachDate)
        }
    }
}
