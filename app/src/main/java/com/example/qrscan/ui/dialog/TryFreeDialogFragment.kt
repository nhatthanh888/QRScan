package com.example.qrscan.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.databinding.TryFreeDialogBinding
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.wellcome.WalkThroughtFragment3

class TryFreeDialogFragment : DialogFragment() {
    private lateinit var binding: TryFreeDialogBinding
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }

    private val editConfig by lazy {
        sharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TryFreeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initConfig()

        binding.apply {
            ivClose.setOnSingleClickListener { dismiss() }
            btnTryFree.setOnSingleClickListener {
                premium()
            }

            tvRestore.setOnSingleClickListener {
                reStore()
            }

            layoutTerm.apply {
                tvPrivacy.setOnSingleClickListener {
                    Bridge.getInstance().openPrivacy(requireActivity())
                }
                tvTerm.setOnSingleClickListener {
                    Bridge.getInstance().openTerms(requireActivity())
                }
            }
        }
    }

    private fun premium() {
        editConfig.putBoolean(TRY_FREE, true).apply()
        binding.apply {
            layoutCustomQRCode.ivTickBasic.setImageResource(R.drawable.tick)
            layoutUnlimitedScan.ivTickBasic.setImageResource(R.drawable.tick)
            layoutAdvanced.ivTickBasic.setImageResource(R.drawable.tick)
        }
    }
    private fun reStore() {
        editConfig.putBoolean(TRY_FREE, false).apply()
        binding.apply {
            layoutCustomQRCode.ivTickBasic.setImageResource(R.drawable.untick)
            layoutUnlimitedScan.ivTickBasic.setImageResource(R.drawable.untick)
            layoutAdvanced.ivTickBasic.setImageResource(R.drawable.untick)
        }
    }

    private fun initConfig(){
        if (sharedPreferences.getBoolean(TRY_FREE,false)){
            binding.apply {
                layoutCustomQRCode.ivTickBasic.setImageResource(R.drawable.tick)
                layoutUnlimitedScan.ivTickBasic.setImageResource(R.drawable.tick)
                layoutAdvanced.ivTickBasic.setImageResource(R.drawable.tick)
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }


    companion object {
        const val TRY_FREE = "try_free"
    }
}