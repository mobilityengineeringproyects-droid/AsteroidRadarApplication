package com.udacity.asteroidradar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid constructor(val id: Long, val codename: String, val closeApproachDate: String,
                                val absoluteMagnitude: Double, val estimatedDiameter: Double,
                                val relativeVelocity: Double, val distanceFromEarth: Double,
                                val isPotentiallyHazardous: Boolean) : Parcelable