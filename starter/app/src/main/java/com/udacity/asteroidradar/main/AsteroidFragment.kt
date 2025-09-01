package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.asDomainModel
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
        val adapter = AsteroidAdapter(
            AsteroidClickListener {
                viewModel.displayAsteroidDetails(it)

            }
        )
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {

            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(AsteroidFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayAsteroidDetailsComplete()

            }

        })
        viewModel.databaseAsteroids.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.asDomainModel())
        })

        viewModel.asteroidMedia.observe(viewLifecycleOwner, Observer {
            if (it.mediaType.equals("image")) {
                Picasso.with(context).load(it.url).into(binding.activityMainImageOfTheDay)
            }
        })

        //reference to the navGraphController
        //set the arguments from the selected item on the list, such functionallity implies to actually make use of the previously known
        //logic which makes use of the actually received as part of the asteroid that is binded only the functionllity applies explicitelly
        //such is part of the class, therefore it can be accesible externally as it is assumed part of the internals by the class

        // The actual argument is known forehand it is now simply a matter of passing it and let the magic glow
        // The arguments will get passed making use of the same logic as the MarsRealEstate application, what else is there different
        // from it to shake it, get ready for some shaking ass learning


        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            //list -> adapter.submitList(list)
        })


//  You can use transformation methods to carry information across the observer's lifecycle. The transformations
//  aren't calculated unless an observer is observing the returned LiveData object.
        viewModel.transformedDatabaseAsteroids.observe(viewLifecycleOwner, Observer {
            if (!viewModel.databaseFiltered) {
                adapter.submitList(it)
            }
            //  viewModel.detailViewEntered = false
            // }else{
            //     adapter.submitList(viewModel.databaseAsteroids.value?.asDomainModel())
        })


        // setHasOptionsMenu(true) Deprecated as of Fragment, Version 1.5.0-alpha05


        binding.asteroidRecycler.adapter = adapter
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



/* Deprecated as of Fragment, Version 1.5.0-alpha05

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

 */