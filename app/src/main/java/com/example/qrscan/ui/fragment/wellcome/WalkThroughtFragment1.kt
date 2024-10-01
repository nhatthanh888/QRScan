package com.example.qrscan.ui.fragment.wellcome

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentWalkthrounght1Binding
import com.example.qrscan.extension.setOnSingleClickListener

class WalkThroughtFragment1 : BaseFragment<FragmentWalkthrounght1Binding>() {
    override fun getLayout(): Int = R.layout.fragment_walkthrounght1

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.apply {
            btnContinue.setOnSingleClickListener {
                mNavController = Navigation.findNavController(requireActivity(), R.id.main_nav_host)
                mNavController.navigate(R.id.action_walkThroughtFragment1_to_walkThroughtFragment2)
            }
        }
    }
}