package com.example.qrscan.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.qrscan.App
import com.example.qrscan.Bridge
import com.example.qrscan.R
import com.example.qrscan.data.database.AppDatabase
import com.example.qrscan.viewmodel.ShareDataVM
import com.example.qrscan.extension.toastMsg
import com.example.qrscan.util.GrantPermission
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.viewmodel.HandleMultipleSCanFactory
import com.example.qrscan.viewmodel.HandleMultipleScanVM
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import java.util.concurrent.Executor

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val handleMultipleScanVM: HandleMultipleScanVM by viewModels {
        HandleMultipleSCanFactory((application as App).handleMultipleScanRepository)
    }
    private val handleResultVM: HandleResultVM by viewModels {
        HandleResultFactory((application as App).handleResultScanRepository)
    }
    companion object {
        private const val PICK_IMAGE = 111

        const val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101

        const val PERMISSION_CAM = 1

        const val PERMISSION_STORAGE = 2
    }

    private var permissionType = PERMISSION_CAM
    private val scanVM: ShareDataVM by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleBackPress()
        Bridge.getInstance().setCurrentActivity(this)
        handleMultipleScanVM.deleteAll()
        handleResultVM.apply {
//            clearVMCreated()
//            clearVMScanned()
//            clearVMFavorite()
        }
    }

    private fun handleBackPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionsHelper.areAllPermissionsGranted(grantResults)) {
            when (requestCode) {
                REQUEST_STORAGE_READ_ACCESS_PERMISSION -> {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(
                        Intent.createChooser(
                            i,
                            getString(R.string.label_select_picture)
                        ), PICK_IMAGE
                    )
                }
            }
        } else {
            toastMsg(R.string.you_should_enable_storage_permission)
        }
    }
    fun requestPermissionStorage() {
        GrantPermission.buildPermissionStorage().checkPermission(
            checkPermissionListener = { case ->
                when (case) {
                    GrantPermission.CASE_ALL_PERMISSION_GRANTED -> {
                        scanVM.setPermissionStorage(true)
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED -> {
                        permissionType = PERMISSION_STORAGE
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED_WITH_QUESTION -> {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri =
                            Uri.fromParts(
                                "package",
                                packageName,
                                null
                            )
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            })
    }


    fun requestPermissionCamera() {
        GrantPermission.buildPermissionCam().checkPermission(
            checkPermissionListener = { case ->
                when (case) {
                    GrantPermission.CASE_ALL_PERMISSION_GRANTED -> {
                        scanVM.setPermissionCamera(true)
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED -> {
                        permissionType = PERMISSION_CAM
                    }

                    GrantPermission.CASE_ALL_PERMISSION_DENIED_WITH_QUESTION -> {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri =
                            Uri.fromParts(
                                "package",
                                packageName,
                                null
                            )
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        handleMultipleScanVM.deleteAll()
        handleResultVM.apply {
//            clearVMCreated()
//            clearVMScanned()
//            clearVMFavorite()
        }
    }

}