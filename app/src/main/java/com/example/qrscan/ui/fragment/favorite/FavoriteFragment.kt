package com.example.qrscan.ui.fragment.favorite

import android.Manifest
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
import androidx.fragment.app.activityViewModels
import com.example.qrscan.App
import com.example.qrscan.R
import com.example.qrscan.adapter.ResultAdapterFavorite
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentFavoriteBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.MainActivity
import com.example.qrscan.ui.activity.result.ResultActivity
import com.example.qrscan.ui.fragment.HomeFragment
import com.example.qrscan.ui.fragment.history.HistoryScanFragment
import com.example.qrscan.ui.fragment.scan.ListenerListFavorite
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(), ListenerListFavorite {

    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA
    )
    private val activity by lazy {
        requireActivity() as MainActivity
    }

    private val handleResultVM: HandleResultVM by activityViewModels {
        HandleResultFactory((requireActivity().application as App).handleResultScanRepository)
    }
    private var isDelete = false
    private lateinit var resultAdapter: ResultAdapterFavorite

    override fun getLayout(): Int = R.layout.fragment_favorite

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PermissionsHelper.areAllPermissionsGranted(requireActivity(), cameraPermissions)) {
            visibleScan()
        }
        val homeFragment = requireParentFragment().parentFragment as HomeFragment

        updateQuantityItemDelete()
        setUpListFavorite()
        observerUIActiveDelete()

        mBinding?.apply {
            layoutNoData.btnScan.setOnSingleClickListener {
                if (!PermissionsHelper.areAllPermissionsGranted(
                        requireActivity(),
                        cameraPermissions
                    )
                ) {
                    activity.requestPermissionCamera()
                }
                homeFragment.openCam()
            }
            ivBack.setOnSingleClickListener {
                clearVM()
            }

            ivDeleteAction.setOnSingleClickListener {
                if (isDelete) {
                    if (handleResultVM.listFavorite.value?.toList()
                            ?.count { item -> item.isCheck }!! > 0
                    ) {
                        showDialogDeleteConfirm()
                    } else {
                        isDelete = false
                        handleResultVM.apply {
                            setIsDeleteFavoriteAll(false)
                            updateIsDeleteItemFavorite(isDelete = false, favorite = true)
                        }
                    }
                } else {
                    isDelete = true
                    handleResultVM.apply {
                        setIsDeleteFavoriteAll(true)
                        updateIsDeleteItemFavorite(isDelete = true, favorite = true)
                    }
                }
            }

            ckDeleteAll.setOnSingleClickListener {
                if (ckDeleteAll.isChecked) {
                    handleResultVM.apply {
                        setDeleteAllItemFavorite(true)
                        updateIsCheckItemFavorite(isCheck = true, favorite = true)
                    }
                    ckDeleteAll.isChecked = true
                } else {
                    handleResultVM.apply {
                        setDeleteAllItemFavorite(false)
                        updateIsCheckItemFavorite(isCheck = false, favorite = true)
                    }
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
                handleResultVM.deleteFavoriteAll(update = false, favorite = true)
            } else {
                handleResultVM.deleteFavorite(update = false, favorite = true, isCheck = true)
            }
            dialog.dismiss()
        }
        tvNo.setOnSingleClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpListFavorite() {
        resultAdapter = ResultAdapterFavorite(requireContext(), onClickItem = { model ->
            val intent = Intent(requireContext(), ResultActivity::class.java)
            intent.putExtra(HistoryScanFragment.MODEL_SCAN, model)
            startActivity(intent)
        }, this)
        mBinding?.apply {
            handleResultVM.listFavorite.observe(viewLifecycleOwner) {
                Log.e("List Favorite History",it.toString())
                resultAdapter.submitList(it)
            }
            rvFavorite.adapter = resultAdapter
        }
    }

    private fun observerUIActiveDelete() {
        mBinding?.apply {
            handleResultVM.apply {
                isDeleteActiveFavorite.observe(viewLifecycleOwner) {
                    if (it) {
                        ckDeleteAll.visible()
                        ckDeleteAll.isChecked = false
                        tvDelete.visible()
                        ivBack.visible()
                        tvTitleToolbar.gone()
                        ivDeleteAction.setImageResource(R.drawable.delete_multiple_scan_active)
                        deleteItemFavoriteActive(true)
                    } else {
                        ckDeleteAll.gone()
                        tvDelete.gone()
                        ivBack.gone()
                        tvTitleToolbar.visible()
                        ivDeleteAction.setImageResource(R.drawable.delete_multiple_scan)
                        deleteItemFavoriteActive(false)
                        setDeleteAllItemFavorite(false)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityItemDelete() {
        mBinding?.apply {
            handleResultVM.listFavorite.observe(viewLifecycleOwner) {
                if (it.toList().count { item -> item.isCheck } > 0) {
                    tvDelete.text = "${requireContext().getString(R.string.Delete)} (${
                        it.toList().count { item -> item.isCheck }
                    })"
                } else {
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

    private fun visibleScan() {
        mBinding?.apply {
            handleResultVM.listFavorite.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    layoutNoData.layout.gone()
                    rvFavorite.visible()
                    ivDeleteAction.visible()
                } else {
                    layoutNoData.layout.visible()
                    rvFavorite.gone()
                    ivDeleteAction.gone()
                    ckDeleteAll.gone()
                    tvDelete.gone()
                    ivBack.gone()
                    tvTitleToolbar.visible()
                }
            }

        }
    }

    private fun clearVM() {
        handleResultVM.apply {
            //clearVMFavorite()
        }
    }

    override fun listenUpdateItem(id: Int, check: Boolean) {
        handleResultVM.setCheckItemFavoriteDelete(id, check)
        handleResultVM.updateCheckItemFavorite(id, check, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearVM()
    }
}