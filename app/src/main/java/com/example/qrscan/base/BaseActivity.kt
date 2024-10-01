package com.example.qrscan.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil


import androidx.viewbinding.ViewBinding


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(), navigator {
    lateinit var mDataBinding: T
    abstract fun getContentView(): Int
    abstract fun initView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, getContentView())
        initView()
        hideSoftKeyboard()
    }

    override fun showActivity(activity: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, activity)
        intent.putExtras(bundle ?: Bundle())
        startActivity(intent)
    }
    fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}