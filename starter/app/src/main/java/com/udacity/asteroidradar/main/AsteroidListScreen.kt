package com.udacity.asteroidradar.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R

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
            .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
            .clickable { onAsteroidClicked(asteroid) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Aligns items vertically in the center
        ) {
            // This Column will now take up all available horizontal space
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = asteroid.codename)
                Text(text = asteroid.closeApproachDate)
            }

            // This image will be pushed to the right
            if (asteroid.isPotentiallyHazardous) {
                Image(
                    painter = painterResource(id = R.drawable.ic_status_potentially_hazardous),
                    contentDescription = stringResource(id = R.string.accessibility_potentially_hazardous_asteroid)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_status_normal),
                    contentDescription = stringResource(id = R.string.accessibility_normal_asteroid)
                )
            }
        }
    }
}

