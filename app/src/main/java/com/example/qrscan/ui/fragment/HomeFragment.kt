package com.example.qrscan.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentHomeBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.navigation.NavigationFragment
import com.example.qrscan.ui.activity.MainActivity
import com.example.qrscan.viewmodel.HomeGraphVM


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var navController: NavController
    private val iconHistory by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.history_selected
        )
    }
    private val iconCreate by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.create_selected
        )
    }
    private val iconFavorite by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.favorite_selected
        )
    }
    private val iconSetting by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.setting_selected
        )
    }
    private val iconHistoryInactive by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.history
        )
    }
    private val iconCreateInactive by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.create
        )
    }
    private val iconFavoriteInactive by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.favorite
        )
    }
    private val iconSettingInactive by lazy {
        ContextCompat.getDrawable(
            requireContext(),
            R.drawable.setting
        )
    }
    private val textSelected by lazy {
        ContextCompat.getColor(requireContext(), R.color.bottom_nav_text)
    }
    private val textUnSelected by lazy {
        ContextCompat.getColor(requireContext(), R.color.bottom_nav_unselect)
    }
    private val homeGraphVM: HomeGraphVM by viewModels(ownerProducer = {
        findNavController().getBackStackEntry(R.id.homeFragment)
    })
    private val activity by lazy {
        requireActivity() as MainActivity
    }

    override fun getLayout(): Int = R.layout.fragment_home
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.apply {
            navController = fragmentContainer.getFragment<NavHostFragment>().navController
            layoutBottomNav.apply {
                layoutHistory.setOnSingleClickListener {
                    onBottomMenuItemClick(R.id.historyFragment, navController)
                }
                layoutCreate.setOnSingleClickListener {
                    onBottomMenuItemClick(R.id.createFragment, navController)
                }
                layoutFavorite.setOnSingleClickListener {
                    onBottomMenuItemClick(R.id.favoriteFragment, navController)
                }
                layoutSetting.setOnSingleClickListener {
                    onBottomMenuItemClick(R.id.settingFragment, navController)
                }
            }
            btnScan.setOnSingleClickListener {
                if (requireActivity().supportFragmentManager.findFragmentByTag("openCamera") == null) {
                    onBottomMenuItemClick(R.id.scanFragment, navController)
                }
            }
            addDestinationChangeListener(navController)
            addOnBackPressedCallback(navController)
        }
    }

    private fun navigateTo(destinationId: Int, navController: NavController) {
        navController.navigate(destinationId, null, navOptions {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        })
    }

    fun openCam() {
        if (navController.currentDestination?.id == R.id.scanFragment) {
            homeGraphVM.submitBackToGraphRootEvent()
        } else {
            navigateTo(R.id.scanFragment, navController)
        }
    }

    private fun onBottomMenuItemClick(destinationId: Int, navController: NavController) {
        if (navController.currentDestination?.id == destinationId) {
            homeGraphVM.submitBackToGraphRootEvent()
        } else {
            navigateTo(destinationId, navController)
        }
    }

    private fun addOnBackPressedCallback(navController: NavController) {
        activity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(false) {
                override fun handleOnBackPressed() {
                    navController.run {
                        val startDestinationId = graph.findStartDestination().id
                        isEnabled = currentBackStackEntry?.destination?.id != startDestinationId
                        if (isEnabled) {
                            popBackStack(startDestinationId, false)
                        }
                    }
                }
            })
    }

    private fun addDestinationChangeListener(navController: NavController) {
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?,
                ) {
                    mBinding?.apply {
                        layoutBottomNav.apply {
                            layoutHistory.apply {
                                ivHistory.setImageDrawable(iconHistoryInactive)
                                tvHistory.setTextColor(textUnSelected)
                            }
                            layoutCreate.apply {
                                ivCreate.setImageDrawable(iconCreateInactive)
                                tvCreate.setTextColor(textUnSelected)
                            }
                            layoutFavorite.apply {
                                ivFavorite.setImageDrawable(iconFavoriteInactive)
                                tvFavorite.setTextColor(textUnSelected)
                            }
                            layoutSetting.apply {
                                ivSetting.setImageDrawable(iconSettingInactive)
                                tvSetting.setTextColor(textUnSelected)
                            }
                            btnScan.apply {

                            }
                            destination.hierarchy.forEach {
                                when (it.id) {
                                    R.id.historyFragment -> {
                                        ivHistory.setImageDrawable(iconHistory)
                                        tvHistory.setTextColor(textSelected)
                                    }

                                    R.id.createFragment -> {
                                        ivCreate.setImageDrawable(iconCreate)
                                        tvCreate.setTextColor(textSelected)
                                    }

                                    R.id.favoriteFragment -> {
                                        ivFavorite.setImageDrawable(iconFavorite)
                                        tvFavorite.setTextColor(textSelected)
                                    }

                                    R.id.settingFragment -> {
                                        ivSetting.setImageDrawable(iconSetting)
                                        tvSetting.setTextColor(textSelected)
                                    }

                                    R.id.scanFragment -> {
                                        layoutHistory.apply {
                                            ivHistory.setImageDrawable(iconHistoryInactive)
                                            tvHistory.setTextColor(textUnSelected)
                                        }
                                        layoutCreate.apply {
                                            ivCreate.setImageDrawable(iconCreateInactive)
                                            tvCreate.setTextColor(textUnSelected)
                                        }
                                        layoutFavorite.apply {
                                            ivFavorite.setImageDrawable(iconFavoriteInactive)
                                            tvFavorite.setTextColor(textUnSelected)
                                        }
                                        layoutSetting.apply {
                                            ivSetting.setImageDrawable(iconSettingInactive)
                                            tvSetting.setTextColor(textUnSelected)
                                        }
                                    }
                                }
                            }
                        }
                    } ?: navController.removeOnDestinationChangedListener(this)
                }
            })
    }

}