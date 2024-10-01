package com.example.qrscan.ui.fragment.wellcome

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentWalkthrounght3Binding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.fragment.scan.FragmentScan

class WalkThroughtFragment3 : BaseFragment<FragmentWalkthrounght3Binding>() {
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }

    private val editConfig by lazy {
        sharedPreferences.edit()
    }

    override fun getLayout(): Int = R.layout.fragment_walkthrounght3
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(requireActivity(), R.id.main_nav_host)
        mBinding?.apply {
            btnTryFree.setOnSingleClickListener {
                premium()
                //   mNavController.navigate(R.id.action_walkThroughtFragment3_to_homeFragment)
            }
            tvRestore.setOnSingleClickListener {
                reStore()
            }
            ivClose.setOnSingleClickListener {
                mNavController.navigate(R.id.action_walkThroughtFragment3_to_homeFragment)
            }
            layoutTerm.apply {
                tvTerm.setOnSingleClickListener {
                    Bridge.getInstance().openTerms(requireActivity())
                }
                tvPrivacy.setOnSingleClickListener {
                    Bridge.getInstance().openPrivacy(requireActivity())
                }
            }

        }
    }

    private fun reStore() {
        editConfig.putBoolean(TRY_FREE, false).apply()
        mBinding?.apply {
            layoutCustomQRCode.ivTickBasic.setImageResource(R.drawable.untick)
            layoutUnlimitedScan.ivTickBasic.setImageResource(R.drawable.untick)
            layoutAdvanced.ivTickBasic.setImageResource(R.drawable.untick)
        }
    }

    private fun premium() {
        editConfig.putBoolean(TRY_FREE, true).apply()
        mBinding?.apply {
            layoutCustomQRCode.ivTickBasic.setImageResource(R.drawable.tick)
            layoutUnlimitedScan.ivTickBasic.setImageResource(R.drawable.tick)
            layoutAdvanced.ivTickBasic.setImageResource(R.drawable.tick)
        }
    }

    companion object {
        const val TRY_FREE = "try_free"
    }
}