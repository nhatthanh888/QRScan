package com.example.qrscan.ui.fragment.history

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
import com.example.qrscan.adapter.ResultAdapterCreatedHistory
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentCreateHistoryBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.result.ResultActivity
import com.example.qrscan.ui.fragment.scan.ListenerListCreated
import com.example.qrscan.ui.fragment.scan.ListenerListFavorite
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateHistoryFragment : BaseFragment<FragmentCreateHistoryBinding>(), ListenerListCreated {
    private val handleResultVM: HandleResultVM by activityViewModels {
        HandleResultFactory((requireActivity().application as App).handleResultScanRepository)
    }
    private lateinit var resultAdapter: ResultAdapterCreatedHistory
    val navController by lazy {
        Navigation.findNavController(
            requireActivity(), R.id.fragmentContainerHistory
        )
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }
        }

    private var isDelete = false

    override fun getLayout(): Int = R.layout.fragment_create_history
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        updateQuantityItemDelete()
        addDestinationChangeListener()
        setUpListCreated()
        observerUIActiveDelete()
        mBinding?.apply {
            ivBack.setOnSingleClickListener {
                navController.popBackStack()
            }
            ivDeleteAction.setOnSingleClickListener {
                if (isDelete) {
                    if (handleResultVM.listCreateHis.value?.toList()
                            ?.count { item -> item.isCheck }!! > 0
                    ) {
                        showDialogDeleteConfirm()
                    } else {
                        isDelete = false
                        handleResultVM.apply {
                            setIsDeleteCreatedAll(false)
                            updateIsDeleteItemCreated(isDelete = false, created = true)
                        }
                    }
                } else {
                    isDelete = true
                    handleResultVM.apply {
                        setIsDeleteCreatedAll(true)
                        updateIsDeleteItemCreated(isDelete = true, created = true)
                    }
                }
            }

            ckDeleteAll.setOnSingleClickListener {
                if (ckDeleteAll.isChecked) {
                    handleResultVM.apply {
                        setDeleteAllItemCreated(true)
                        updateIsCheckItemCreated(isCheck = true, created = true)
                    }
                    ckDeleteAll.isChecked = true
                } else {
                    handleResultVM.apply {
                        setDeleteAllItemCreated(false)
                        updateIsCheckItemCreated(isCheck = false, created = true)
                    }
                    ckDeleteAll.isChecked = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityItemDelete() {
        mBinding?.apply {
            handleResultVM.listCreateHis.observe(viewLifecycleOwner) {
                if (it.toList().count { item -> item.isCheck } > 0) {
                    tvDelete.text = "${requireContext().getString(R.string.Delete)} (${
                        it.toList().count { item -> item.isCheck }
                    })"
                }else {
                    if (isDelete) {
                        tvDelete.text = requireContext().getString(R.string.Delete)
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
                handleResultVM.deleteCreatedAll(true)
            } else {
                handleResultVM.deleteCreated(true, isCheck = true)
            }
            dialog.dismiss()
        }
        tvNo.setOnSingleClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun observerUIActiveDelete() {
        mBinding?.apply {
            handleResultVM.apply {
                isDeleteActiveCreated.observe(viewLifecycleOwner) {
                    if (it) {
                        ckDeleteAll.visible()
                        ckDeleteAll.isChecked = false
                        tvDelete.visible()
                        tvTitleToolbar.gone()
                        ivDeleteAction.setImageResource(R.drawable.delete_multiple_scan_active)
                        deleteItemCreatedActive(true)
                    } else {
                        ckDeleteAll.gone()
                        tvDelete.gone()
                        tvTitleToolbar.visible()
                        ivDeleteAction.setImageResource(R.drawable.delete_multiple_scan)
                        deleteItemCreatedActive(false)
                        setDeleteAllItemCreated(false)
                    }
                }
            }
        }
    }

    private fun setUpListCreated() {
        resultAdapter = ResultAdapterCreatedHistory(requireContext(), onClickItem = { model ->
            val intent = Intent(requireContext(), ResultActivity::class.java)
            intent.putExtra(HistoryScanFragment.MODEL_SCAN, model)
            startActivity(intent)
        }, this)
        mBinding?.apply {
            handleResultVM.listCreateHis.observe(viewLifecycleOwner) {
                Log.e("List Created History",it.toString())
                resultAdapter.submitList(it)
            }
            rvCreated.adapter = resultAdapter
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

    override fun listenUpdateItem(id: Int, check: Boolean) {
        handleResultVM.apply {
            setCheckItemCreatedDelete(id, check)
            updateCheckItemCreated(id, check, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handleResultVM.apply {
            //clearVMCreated()
        }
    }

}