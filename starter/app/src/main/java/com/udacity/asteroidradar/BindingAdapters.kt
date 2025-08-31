package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.viewmodel.AsteroidViewModel
import com.udacity.asteroidradar.viewmodel.Progress

@BindingAdapter("asteroidId")
fun TextView.setAsteroidId(asteroid: Asteroid) {
    asteroid?.let {
        text = it.id.toString()
    }
}

@BindingAdapter("asteroidDate")
fun TextView.setAsteroidApproachDate(asteroid: Asteroid) {
    asteroid?.let {
        text = asteroid.closeApproachDate
    }

}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

/*
@BindingAdapter("listData")
fun bindListData(recylerView:RecyclerView, data:List<Asteroid>) {
    val asteroidAdapter = recylerView.adapter  as AsteroidAdapter
        asteroidAdapter.submitList(data)
}

 */

@BindingAdapter("progressStatus")
fun ProgressBar.reflectProgress(progress: Progress) {

    visibility = (when (progress) {
        Progress.LOADING -> View.VISIBLE
        Progress.ERROR, Progress.FINISH -> View.GONE
    })
}

/*
@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imgUrl: String?) {
    val context = imageView.context
        imgUrl?.let {
            Picasso.with(context).load(imgUrl).into(imageView);
        }
}

 */
