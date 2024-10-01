package com.example.qrscan.ui.fragment.create

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.qrscan.R
import com.example.qrscan.adapter.GridSpacingItemDecoration
import com.example.qrscan.adapter.QRCreateSocialAdapter
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.data.model.QRCreate
import com.example.qrscan.databinding.CreateFragmentBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.ui.activity.create.CreateQrActivity
import com.example.qrscan.ui.dialog.TryFreeDialogFragment
import com.example.qrscan.ui.fragment.scan.FragmentScan
import com.example.qrscan.viewmodel.HomeGraphVM

@Suppress("DEPRECATION")
@SuppressLint("UseCompatLoadingForDrawables")
class CreateQRFragment : BaseFragment<CreateFragmentBinding>() {
    override fun getLayout(): Int = R.layout.create_fragment

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(FragmentScan.CONFIG_APP, Context.MODE_PRIVATE)
    }

    private val editConfig by lazy {
        sharedPreferences.edit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        mBinding?.apply {
            rvSocialMedia.adapter =
                QRCreateSocialAdapter(createDataSocial(), clickItem = { position ->
                    when (position) {
                        FB -> {
                            startActivityCreateQr(9)
                        }

                        IG -> {
                            startActivityCreateQr(10)
                        }

                        TW -> {
                            startActivityCreateQr(11)
                        }

                        TK -> {
                            startActivityCreateQr(12)
                        }

                        Y -> {
                            startActivityCreateQr(13)
                        }

                        else -> {
                            startActivityCreateQr(14)
                        }
                    }
                })

            rvSocialMedia.addItemDecoration(GridSpacingItemDecoration(3, 50, false))

            btnCreateWeb.setOnSingleClickListener {
                startActivityCreateQr(0)
            }
            btnCreatePhone.setOnSingleClickListener {
                startActivityCreateQr(1)
            }
            btnCreateEmail.setOnSingleClickListener {
                startActivityCreateQr(2)
            }
            btnCreateText.setOnSingleClickListener {
                startActivityCreateQr(3)
            }
            btnCreateContact.setOnSingleClickListener {
                startActivityCreateQr(4)
            }
            btnCreateWifi.setOnSingleClickListener {
                startActivityCreateQr(5)
            }
            btnCreateCalendar.setOnSingleClickListener {
                startActivityCreateQr(6)
            }
            btnCreateLocation.setOnSingleClickListener {
                startActivityCreateQr(7)
            }
            btnCreateBarcode.setOnSingleClickListener {
                startActivityCreateQr(8)
            }

            lottieAnim.imageAssetsFolder = "images2"

            lottieAnim.setOnSingleClickListener {
                TryFreeDialogFragment().show(
                    requireActivity().supportFragmentManager,
                    TryFreeDialogFragment.TRY_FREE
                )
            }
        }
    }
    private fun initUI(){
        if (sharedPreferences.getBoolean(TryFreeDialogFragment.TRY_FREE,false)){
            mBinding?.lottieAnim?.gone()
        }
    }

    private fun createDataSocial(): List<QRCreate> {
        val list = ArrayList<QRCreate>()
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_fb),
                this.getString(R.string.Facebook)
            )
        )
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_instagram),
                this.getString(R.string.Instagram)
            )
        )
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_twitter),
                this.getString(R.string.Twitter)
            )
        )
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_tiktok),
                this.getString(R.string.Tiktok)
            )
        )
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_youtobe),
                this.getString(R.string.YouTube)
            )
        )
        list.add(
            QRCreate(
                this.resources.getDrawable(R.drawable.create_whatsapp),
                this.getString(R.string.WhatsApp)
            )
        )
        return list
    }

    private fun startActivityCreateQr(itemId: Int) {
        val intent = Intent(requireContext(), CreateQrActivity::class.java)
        intent.putExtra("KEY_QR_TYPE", itemId)
        startActivity(intent)
    }

    companion object {
        const val FB = 0
        const val IG = 1
        const val TW = 2
        const val TK = 3
        const val Y = 4
    }
}