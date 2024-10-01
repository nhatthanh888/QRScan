package com.example.qrscan.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.qrscan.App
import java.io.*


object FileUtils {
    fun isScopedStorage(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    fun saveBitmapToInternal(
        bitmapImage: Bitmap,
        name: String = "QR_${System.currentTimeMillis()}.png"
    ): File {
        val file = File(App.appContext().internalFile(), name)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    fun saveImage(bitmap: Bitmap, name: String): OutputStream? {
        var fos: OutputStream? = null

        if (isScopedStorage()) {
            val resolver: ContentResolver = App.appContext().contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "images/jpeg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                ExternalFileUtils.DIR_RELATIVE_PATH
            )
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.let {
                    fos = it
                }
            }
        } else {
            val imagesDir =
                File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES)
            if (!imagesDir.mkdirs()) {
                imagesDir.mkdirs()
            }
            val image = File(imagesDir, "$name.jpg")
            fos = FileOutputStream(image)

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(image.absolutePath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            App.instance().sendBroadcast(mediaScanIntent)
        }
        fos?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        fos?.flush()
        fos?.close()
        return fos
    }


    fun getBitmapAsserts(context: Context, path: String?): Bitmap? {
        val assetManager: AssetManager = context.assets
        var istr: InputStream? = null
        try {
            istr = path?.let {
                assetManager.open(it.substringAfter("file:///android_asset/"))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(istr)
    }
}