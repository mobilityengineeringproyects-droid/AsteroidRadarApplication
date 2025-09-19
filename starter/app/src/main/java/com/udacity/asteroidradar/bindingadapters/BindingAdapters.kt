package com.udacity.asteroidradar.bindingadapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R

@BindingAdapter("asteroidHazardStatusIcon") // This is the custom attribute name you'll use in XML
fun bindAsteroidHazardStatusIcon(imageView: ImageView, asteroid: Asteroid?) {
    asteroid?.let {
        if (it.isPotentiallyHazardous) {
            imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
            // Optional: Set a content description for accessibility
            imageView.contentDescription = imageView.context.getString(R.string.accessibility_potentially_hazardous_asteroid)
        } else {
            // Optional: Set a different image or clear the image if not hazardous
            // imageView.setImageResource(R.drawable.ic_status_normal) // Example for a non-hazardous icon
            imageView.setImageResource(R.drawable.ic_status_normal) // Or clear it if no icon should be shown
            // Optional: Set a content description for accessibility
            imageView.contentDescription = imageView.context.getString(R.string.accessibility_normal_asteroid)
        }
    } ?: run {
        // Handle the case where the asteroid object is null (e.g., clear the image)
        imageView.setImageDrawable(null)
        imageView.contentDescription = null // Clear content description
    }
}