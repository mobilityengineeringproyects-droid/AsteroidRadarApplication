package com.udacity.asteroidradar.network

import android.icu.text.CaseMap
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class AsteroidMedia(
    @Json(name = "media_type")
    val mediaType: String,
    val title: String,
    val url:String
)
