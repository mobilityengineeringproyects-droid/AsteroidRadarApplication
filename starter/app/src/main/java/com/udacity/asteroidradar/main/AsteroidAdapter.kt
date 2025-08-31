package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.databinding.ItemViewAsteroidBinding
import com.udacity.asteroidradar.viewmodel.AsteroidViewModel

class AsteroidAdapter(val clickListener: AsteroidClickListener) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(AsteroidDiffCallback()) {


    class AsteroidViewHolder private constructor(private val binding: ItemViewAsteroidBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: AsteroidClickListener, asteroid: Asteroid) {
            binding.asteroidListener = clickListener
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): AsteroidViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //A RecyclerView’s children should be inflated with attachToRoot passed in as false.
                // The child views are inflated in onCreateViewHolder().
                val binding = ItemViewAsteroidBinding.inflate(layoutInflater, parent, false)
                return AsteroidViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {

        return AsteroidViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {

        val asteroid = getItem(position) as Asteroid
        holder.bind(clickListener, asteroid)
    }
}

class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {

    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}

class AsteroidClickListener(val onClickListener: (asteroid: Asteroid) -> Unit) {

    fun onClick(asteroid: Asteroid) = onClickListener(asteroid)
}
