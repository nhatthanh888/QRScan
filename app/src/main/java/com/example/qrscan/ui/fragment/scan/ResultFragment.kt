package com.example.qrscan.ui.fragment.scan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.Navigation
import com.example.qrscan.App
import com.example.qrscan.R
import com.example.qrscan.adapter.MultipleScanAdapter
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.FragmentResultBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.result.ResultActivity
import com.example.qrscan.viewmodel.HandleMultipleSCanFactory
import com.example.qrscan.viewmodel.HandleMultipleScanVM

@SuppressLint("SetTextI18n")
class ResultFragment : BaseFragment<FragmentResultBinding>(), ListenerItemDelete {
    private val handleMultipleScanVM: HandleMultipleScanVM by activityViewModels {
        HandleMultipleSCanFactory((requireActivity().application as App).handleMultipleScanRepository)
    }
    private lateinit var multipleScanAdapterDiffUtil: MultipleScanAdapter
    private var isDelete = false
    val navController by lazy {
        Navigation.findNavController(
            requireActivity(), R.id.fragmentContainerScanAndResult
        )
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }
        }

    override fun getLayout(): Int = R.layout.fragment_result
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        addDestinationChangeListener()
        updateQuantityItemDelete()
        setUpListScan()
        observerDeleteUI()
        deleteAll()
        mBinding?.apply {
            ivBack.setOnSingleClickListener {
                navController.popBackStack()
            }
            ivDelete.setOnSingleClickListener {
                if (isDelete) {
                    if (handleMultipleScanVM.listResult.value?.toList()
                            ?.count { item -> item.isCheck }!! > 0
                    ) {
                        showDialogDeleteConfirm()
                    } else {
                        isDelete = false
                        handleMultipleScanVM.setStatusDelete(false)
                        handleMultipleScanVM.updateIsDeleteItem(false)
                    }
                } else {
                    isDelete = true
                    handleMultipleScanVM.setStatusDelete(true)
                    handleMultipleScanVM.updateIsDeleteItem(true)
                }
            }
        }
    }

    private fun observerDeleteUI() {
        mBinding?.apply {
            handleMultipleScanVM.statusDelete.observe(viewLifecycleOwner) {
                if (it) {
                    handleMultipleScanVM.setListChoiceAllDelete(false)
                    handleMultipleScanVM.setListActiveDelete(true)
                    ckDeleteAll.visible()
                    ckDeleteAll.isChecked = false
                    ivDelete.setImageResource(R.drawable.delete_multiple_scan_active)
                    tvBatchScan.text =
                        requireContext().getString(R.string.Delete)
                } else {
                    handleMultipleScanVM.setListActiveDelete(false)
                    ckDeleteAll.gone()
                    ivDelete.setImageResource(R.drawable.delete_multiple_scan)
                    tvBatchScan.text =
                        "${requireContext().getString(R.string.Batch_Scan)} (${handleMultipleScanVM.getQuantityScan()})"
                }
            }
        }
    }

    private fun deleteAll() {
        mBinding?.apply {
            ckDeleteAll.setOnClickListener {
                if (ckDeleteAll.isChecked) {
                    handleMultipleScanVM.setListChoiceAllDelete(true)
                    ckDeleteAll.isChecked = true
                    handleMultipleScanVM.updateIsCheckItem(true)
                } else {
                    handleMultipleScanVM.setListChoiceAllDelete(false)
                    ckDeleteAll.isChecked = false
                    handleMultipleScanVM.updateIsCheckItem(false)
                }
            }
        }
    }

    private fun updateQuantityItemDelete() {
        mBinding?.apply {
            handleMultipleScanVM.listResult.observe(viewLifecycleOwner) {
                if (it.toList().count { item -> item.isCheck } > 0) {
                    tvBatchScan.text = "${requireContext().getString(R.string.Delete)} (${
                        it.toList().count { item -> item.isCheck }
                    })"
                } else {
                    if (isDelete) {
                        tvBatchScan.text = requireContext().getString(R.string.Delete)
                    }
                }
                ckDeleteAll.isChecked = it.size == it.toList().count { item -> item.isCheck }

                if (it.isEmpty()) {
                    ckDeleteAll.isChecked = false
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showDialogDeleteConfirm() {
        val dialog = Dialog(requireContext())
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comfirm_delete_item)
        val window = dialog.window
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val windowAttribute = attributes
            windowAttribute.gravity = Gravity.CENTER
            attributes = windowAttribute
        }
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        val tvNo = dialog.findViewById<TextView>(R.id.tvNo)

        btnYes.setOnSingleClickListener {
            if (mBinding!!.ckDeleteAll.isChecked) {
                handleMultipleScanVM.deleteAll()
            } else {
                handleMultipleScanVM.deleteItem(true)
            }
            dialog.dismiss()
        }
        tvNo.setOnSingleClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpListScan() {
        mBinding?.apply {
            multipleScanAdapterDiffUtil = MultipleScanAdapter(
                onClickItem = { data ->
                    val model = ResultScanModel(
                        result = data.value,
                        time = data.time,
                        typeCodeScan = data.typeCode,
                        typeResult = data.type,
                        scanned = true
                    )
                    val intent = Intent(requireContext(), ResultActivity::class.java)
                    intent.putExtra(MODEL_SCAN, model)
                    startActivity(intent)
                },
                this@ResultFragment
            )
            handleMultipleScanVM.listResult.observe(viewLifecycleOwner) {
                multipleScanAdapterDiffUtil.submitList(it)
            }
            rvMultipleScan.adapter = multipleScanAdapterDiffUtil
            tvBatchScan.text =
                "${requireContext().getString(R.string.Batch_Scan)} (${handleMultipleScanVM.getQuantityScan()})"
        }
    }

    private fun addDestinationChangeListener() {
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?,
                ) {
                    if (mBinding == null) {
                        controller.removeOnDestinationChangedListener(this)
                        return
                    }
                    destination.hierarchy.forEach {
                        configureBackNavigation(it.id)
                    }
                }
            })
    }

    private fun configureBackNavigation(destinationId: Int) = when (destinationId) {
        navController.graph.findStartDestination().id -> {
            onBackPressedCallback.isEnabled = false
        }

        navController.graph.id -> Unit
        else -> {
            onBackPressedCallback.isEnabled = true
        }
    }

    companion object {
        const val MODEL_SCAN = "model_scan"
    }

    override fun listenUpdateItem(id: Int, check: Boolean) {
        handleMultipleScanVM.setCheckItemDelete(id, check)
        handleMultipleScanVM.updateCheckItem(id, check)
    }

}