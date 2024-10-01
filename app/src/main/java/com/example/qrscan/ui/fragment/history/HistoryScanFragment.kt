package com.example.qrscan.ui.fragment.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.qrscan.App
import com.example.qrscan.R
import com.example.qrscan.adapter.ResultAdapterCreatedHistory
import com.example.qrscan.adapter.ResultAdapterScanned
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.HistoryScanFragmentBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.MainActivity
import com.example.qrscan.ui.activity.result.ResultActivity
import com.example.qrscan.ui.dialog.TryFreeDialogFragment
import com.example.qrscan.ui.fragment.HomeFragment
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.ui.fragment.scan.ListenerListCreated
import com.example.qrscan.ui.fragment.scan.ListenerListScanned
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM

class HistoryScanFragment : BaseFragment<HistoryScanFragmentBinding>(), ListenerListCreated,
    ListenerListScanned {
   // val homeFragment = requireParentFragment().parentFragment as HomeFragment
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }

    private val editConfig by lazy {
        sharedPreferences.edit()
    }

    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA
    )
    private val activity by lazy {
        requireActivity() as MainActivity
    }

    private val handleResultVM: HandleResultVM by activityViewModels {
        HandleResultFactory((requireActivity().application as App).handleResultScanRepository)
    }

    private val navController by lazy {
        Navigation.findNavController(
            requireActivity(), R.id.fragmentContainerHistory
        )
    }

    private lateinit var resultAdapter: ResultAdapterScanned
    private lateinit var resultCreatedAdapter: ResultAdapterCreatedHistory

    override fun getLayout(): Int = R.layout.history_scan_fragment
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }


    @SuppressLint("CommitTransaction")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        if (PermissionsHelper.areAllPermissionsGranted(requireActivity(), cameraPermissions)) {
            visibleScan()
        }
        resultAdapter = ResultAdapterScanned(requireContext(), onClickItem = { model ->
            val intent = Intent(requireContext(), ResultActivity::class.java)
            intent.putExtra(MODEL_SCAN, model)
            startActivity(intent)
        }, this)

        resultCreatedAdapter =
            ResultAdapterCreatedHistory(requireContext(), onClickItem = { model ->
                val intent = Intent(requireContext(), ResultActivity::class.java)
                intent.putExtra(MODEL_SCAN, model)
                startActivity(intent)
            }, this)

        mBinding?.apply {
            layoutNoData.btnScanNoData.setOnSingleClickListener {
                if (!PermissionsHelper.areAllPermissionsGranted(
                        requireActivity(),
                        cameraPermissions
                    )
                ) {
                    activity.requestPermissionCamera()
                }
              //  homeFragment.openCam()
            }

            lottieAnim.imageAssetsFolder = "images2"

            lottieAnim.setOnSingleClickListener {
                TryFreeDialogFragment().show(
                    requireActivity().supportFragmentManager,
                    TryFreeDialogFragment.TRY_FREE
                )
            }

            layoutHistory.apply {
                layoutScanned.setOnSingleClickListener { navController.navigate(R.id.action_historyScanFragment_to_scanHistoryFragment) }
                layoutCreated.setOnSingleClickListener { navController.navigate(R.id.action_historyScanFragment_to_createHistoryFragment) }
                tvViewAllScanScanned.setOnSingleClickListener { navController.navigate(R.id.action_historyScanFragment_to_scanHistoryFragment) }
                tvViewAllScanCreated.setOnSingleClickListener { navController.navigate(R.id.action_historyScanFragment_to_createHistoryFragment) }
                handleResultVM.listScannedHis.observe(viewLifecycleOwner) {
                    if (it.size >= 2) {
                        val list = listOf(it[0], it[1])
                        resultAdapter.submitList(list)
                        rvHistoryScanned.adapter = resultAdapter
                    } else if (it.isNotEmpty()) {
                        val list = listOf(it[0])
                        resultAdapter.submitList(list)
                        rvHistoryScanned.adapter = resultAdapter
                    }
                }

                handleResultVM.listCreateHis.observe(viewLifecycleOwner) {
                    if (it.size >= 2) {
                        val list = listOf(it[0], it[1])
                        resultCreatedAdapter.submitList(list)
                        rvHistoryCreated.adapter = resultCreatedAdapter
                    } else if (it.isNotEmpty()) {
                        val list = listOf(it[0])
                        resultCreatedAdapter.submitList(list)
                        rvHistoryCreated.adapter = resultCreatedAdapter
                    }
                }

            }

        }
    }

    private fun initUI() {
        if (sharedPreferences.getBoolean(TryFreeDialogFragment.TRY_FREE, false)) {
            mBinding?.lottieAnim?.gone()
        }
    }

    private fun visibleScan() {
        mBinding?.apply {
            handleResultVM.listCreateHis.observe(viewLifecycleOwner){listCreated->
                handleResultVM.listScannedHis.observe(viewLifecycleOwner){listScanned->
                    if (listCreated.isNotEmpty() || listScanned.isNotEmpty()){
                        layoutNoData.layout.gone()
                        layoutHistory.layout.visible()
                    }else{
                        layoutNoData.layout.visible()
                        layoutHistory.layout.gone()
                    }
                }
            }
        }
    }

    companion object {
        const val MODEL_SCAN = "model_scan"
    }

    override fun listenUpdateItem(id: Int, check: Boolean) {

    }

    override fun listenUpdateItemScanned(id: Int, check: Boolean) {
    }

    override fun onDestroy() {
        super.onDestroy()
        handleResultVM.apply {
//            clearVMScanned()
//            clearVMCreated()
        }
    }

}