package com.example.qrscan.ui.fragment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentHistoryNAVBinding
import com.example.qrscan.ui.fragment.HomeFragment

class FragmentHistoryNAV : BaseFragment<FragmentHistoryNAVBinding>() {
    override fun getLayout(): Int = R.layout.fragment_history_n_a_v

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.apply {  }
    }


}