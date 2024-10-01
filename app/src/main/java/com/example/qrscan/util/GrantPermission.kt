package com.example.qrscan.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.qrscan.App
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


object GrantPermission {
    const val CASE_ALL_PERMISSION_GRANTED = 1
    const val CASE_ALL_PERMISSION_DENIED = 2
    const val CASE_ALL_PERMISSION_DENIED_WITH_QUESTION = 3
    const val ERROR = 0

    var arrayPermission = arrayListOf<String>()

    fun hasMediaPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun buildPermissionStorage(): GrantPermission {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayPermission = arrayListOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayPermission = arrayListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return this
    }

    fun buildPermissionCam(): GrantPermission {
        arrayPermission = arrayListOf(
            Manifest.permission.CAMERA
        )
        return this
    }

    fun buildPermissionLocation(): GrantPermission {
        arrayPermission = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return this
    }

    fun buildPermissionSaveImage(): GrantPermission {
        arrayPermission = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return this
    }

    fun checkPermission(
        checkPermissionListener: (Int) -> Unit?
    ) {
        Dexter.withContext(App.appContext())
            .withPermissions(arrayPermission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        when {
                            report.areAllPermissionsGranted() -> {
                                checkPermissionListener.invoke(CASE_ALL_PERMISSION_GRANTED)
                            }

                            report.isAnyPermissionPermanentlyDenied -> {
                                checkPermissionListener.invoke(
                                    CASE_ALL_PERMISSION_DENIED_WITH_QUESTION
                                )
                            }

                            report.deniedPermissionResponses.size != 0 -> {
                                checkPermissionListener.invoke(CASE_ALL_PERMISSION_DENIED)
                            }

                            else -> {
                                checkPermissionListener.invoke(CASE_ALL_PERMISSION_GRANTED)
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                checkPermissionListener.invoke(ERROR)
            }
            .check()
    }

}