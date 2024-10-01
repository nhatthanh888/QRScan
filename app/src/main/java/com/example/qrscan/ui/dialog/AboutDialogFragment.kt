package com.example.qrscan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.qrscan.R
import com.example.qrscan.databinding.DialogFragmentAboutBinding
import com.example.qrscan.extension.setOnSingleClickListener

class AboutDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivBack.setOnSingleClickListener { dismiss() }
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    companion object {
        const val ABOUT = "about"
    }

}