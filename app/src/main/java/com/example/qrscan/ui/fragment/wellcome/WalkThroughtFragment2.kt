package com.example.qrscan.ui.fragment.wellcome

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentWalkthrounght2Binding
import com.example.qrscan.extension.setOnSingleClickListener

class WalkThroughtFragment2 : BaseFragment<FragmentWalkthrounght2Binding>() {

    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(CONFIG_NAV, Context.MODE_PRIVATE)
    }

    private val editConfig by lazy {
        sharedPreferences.edit()
    }

    override fun getLayout(): Int = R.layout.fragment_walkthrounght2

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.apply {
            btnContinue.setOnSingleClickListener {
                mNavController = Navigation.findNavController(requireActivity(), R.id.main_nav_host)
                mNavController.navigate(R.id.action_walkThroughtFragment2_to_walkThroughtFragment3)
                editConfig.putBoolean(isNav,true).apply()
            }
        }
    }

    companion object {
        const val CONFIG_NAV = "config_nav"
        const val isNav = "nav"
    }

}