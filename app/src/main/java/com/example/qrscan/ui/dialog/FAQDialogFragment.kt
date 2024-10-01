package com.example.qrscan.ui.dialog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentDialogFaqsBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.viewmodel.ShareDataVM

class FAQDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogFaqsBinding
    private val scanVM: ShareDataVM by activityViewModels()
    private var isFAQ = false
    private var isFAQ2 = false
    private var isFAQ3 = false
    private var isFAQ4 = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogFaqsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnSingleClickListener { dismiss() }
            ivBack.clearAnimation()

            layoutQuestion1.setOnSingleClickListener {
                if (isFAQ) {
                    isFAQ = false
                    scanVM.setFAQsStatus(false)
                } else {
                    isFAQ = true
                    scanVM.setFAQsStatus(true)
                }
                initFAQ(layoutAnswer1, ivExpland1, layoutQuestion1,1)
            }

            layoutQuestion2.setOnSingleClickListener {
                if (isFAQ2) {
                    isFAQ2 = false
                    scanVM.setFAQsStatus2(false)
                } else {
                    isFAQ2 = true
                    scanVM.setFAQsStatus2(true)
                }
                initFAQ(layoutAnswer2, ivExpland2, layoutQuestion2,2)
            }

            layoutQuestion3.setOnSingleClickListener {
                if (isFAQ3) {
                    isFAQ3 = false
                    scanVM.setFAQsStatus3(false)
                } else {
                    isFAQ3 = true
                    scanVM.setFAQsStatus3(true)
                }
                initFAQ(layoutAnswer3, ivExpland3, layoutQuestion3,3)
            }

            layoutQuestion4.setOnSingleClickListener {
                if (isFAQ4) {
                    isFAQ4 = false
                    scanVM.setFAQsStatus4(false)
                } else {
                    isFAQ4 = true
                    scanVM.setFAQsStatus4(true)
                }
                initFAQ(layoutAnswer4, ivExpland4, layoutQuestion4,4)
            }

        }
    }

    private fun initFAQ(
        layout: LinearLayoutCompat,
        imageView: ImageView,
        background: RelativeLayout,
        status: Int
    ) {
        binding.apply {
            when (status) {
                1 -> {
                    scanVM.isFAQsStatus.observe(viewLifecycleOwner) { status ->
                        if (status == true) {
                            layout.visible()
                            background.setBackgroundResource(R.drawable.custom_bg_item_faq)
                            rotationShow(imageView)
                            imageView.clearAnimation()
                        } else {
                            layout.gone()
                            background.background = null
                            rotationHide(imageView)
                            imageView.clearAnimation()
                        }
                    }
                }

                2 -> {
                    scanVM.isFAQsStatus2.observe(viewLifecycleOwner) { status ->
                        if (status == true) {
                            layout.visible()
                            background.setBackgroundResource(R.drawable.custom_bg_item_faq)
                            rotationShow(imageView)
                            imageView.clearAnimation()
                        } else {
                            layout.gone()
                            background.background = null
                            rotationHide(imageView)
                            imageView.clearAnimation()
                        }
                    }
                }

                3 -> {
                    scanVM.isFAQsStatus3.observe(viewLifecycleOwner) { status ->
                        if (status == true) {
                            layout.visible()
                            background.setBackgroundResource(R.drawable.custom_bg_item_faq)
                            rotationShow(imageView)
                            imageView.clearAnimation()
                        } else {
                            layout.gone()
                            background.background = null
                            rotationHide(imageView)
                            imageView.clearAnimation()
                        }
                    }
                }

                4 -> {
                    scanVM.isFAQsStatus4.observe(viewLifecycleOwner) { status ->
                        if (status == true) {
                            layout.visible()
                            background.setBackgroundResource(R.drawable.custom_bg_item_faq)
                            rotationShow(imageView)
                            imageView.clearAnimation()
                        } else {
                            layout.gone()
                            background.background = null
                            rotationHide(imageView)
                            imageView.clearAnimation()
                        }
                    }
                }
            }

        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun rotationShow(view: ImageView) {
        val rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 180f)
        rotation.duration = 500
        rotation.start()
    }

    private fun rotationHide(view: ImageView) {
        val rotation = ObjectAnimator.ofFloat(view, "rotation", 180f, 0f)
        rotation.duration = 500
        rotation.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanVM.clearVM()
    }

    companion object {
        const val FAQ = "faq"
    }
}