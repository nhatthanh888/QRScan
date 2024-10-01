package com.example.qrscan.ui.fragment.wellcome

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentSplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(WalkThroughtFragment2.CONFIG_NAV, Context.MODE_PRIVATE)
    }
    private var isNav=false
    override fun getLayout(): Int = R.layout.fragment_splash

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isNav=sharedPreferences.getBoolean(WalkThroughtFragment2.isNav,false)
        CoroutineScope(Dispatchers.Main).launch {
            rotationView()
            delay(3000)
            mBinding!!.ivLoading.clearAnimation()
            mNavController = Navigation.findNavController(requireActivity(), R.id.main_nav_host)
            if (isNav){
                mNavController.navigate(R.id.action_splashFragment_to_homeFragment)
            }else{
                mNavController.navigate(R.id.action_splashFragment_to_walkThroughtFragment1)
            }
        }
    }
    private fun rotationView(){
        val rotation = ObjectAnimator.ofFloat(mBinding!!.ivLoading, "rotation", 0f, 360f)
        rotation.duration = 1000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.start()
    }

}