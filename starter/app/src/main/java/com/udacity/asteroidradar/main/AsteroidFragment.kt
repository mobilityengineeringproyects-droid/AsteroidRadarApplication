package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentAsteroidBinding
import com.udacity.asteroidradar.util.Utils.getFormattedDate
import com.udacity.asteroidradar.util.Utils.getFormattedWeekBackFromDate
import com.udacity.asteroidradar.viewmodel.AsteroidViewModel
import java.util.*

class AsteroidFragment : Fragment() {

    lateinit var viewModel: AsteroidViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = FragmentAsteroidBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel =
            ViewModelProvider(this, AsteroidViewModel.Factory(requireActivity().application)).get(
                AsteroidViewModel::class.java
            )
        binding.viewModel = viewModel

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {

            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(AsteroidFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayAsteroidDetailsComplete()

            }

        })

        viewModel.asteroidMedia.observe(viewLifecycleOwner, Observer {
            if (it.mediaType.equals("image")) {
                Picasso.with(context).load(it.url).into(binding.activityMainImageOfTheDay)
            }
        })

        binding.composeView.setContent {
            val asteroids = viewModel.asteroids.observeAsState(initial = emptyList())
            AsteroidListScreen(asteroids = asteroids.value) {
                viewModel.displayAsteroidDetails(it)
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var menuHost: MenuHost? = null

        val activity = requireActivity()
        if (activity is MenuHost) {
            menuHost = activity

            menuHost.addMenuProvider(object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                    menuInflater.inflate(R.menu.main_overflow_menu, menu)
                }

                @RequiresApi(Build.VERSION_CODES.N)
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    val format = "yyyy-MM-dd"
                    val today = Date()
                    return when (menuItem.itemId) {
                        R.id.show_all_menu -> {


                            viewModel.getAsteroidsFromPeriod(
                                getFormattedWeekBackFromDate(format, today),
                                getFormattedDate(format, today)
                            )
                            viewModel.databaseFiltered = true
                            true
                        }

                        R.id.show_rent_menu -> {
                            val todayFormatted = getFormattedDate(format, today)
                            viewModel.getAsteroidsFromPeriod(todayFormatted, todayFormatted)
                            viewModel.databaseFiltered = true
                            true
                        }

                        R.id.show_buy_menu -> {
                            viewModel.getAsteroids()
                            viewModel.databaseFiltered = true
                            true
                        }

                        R.id.clear_database_menu -> {
                            viewModel.clear()
                            true
                        }

                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }
}
