package com.example.qrscan.ui.fragment.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.qrscan.App
import com.example.qrscan.R
import com.example.qrscan.analyzer.ImageCodeAnalysis
import com.example.qrscan.analyzer.ScannerListener
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.ResultScanModel
import com.example.qrscan.databinding.DialogSearchBarcodeBinding
import com.example.qrscan.databinding.QrScanFragmentBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.onAvoidDoubleClick
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.MainActivity
import com.example.qrscan.ui.activity.result.ResultActivity
import com.example.qrscan.ui.dialog.FAQDialogFragment
import com.example.qrscan.ui.dialog.TryFreeDialogFragment
import com.example.qrscan.ui.fragment.setting.SettingFragment
import com.example.qrscan.util.GetTypeCodeScan
import com.example.qrscan.util.GrantPermission
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.viewmodel.HandleMultipleSCanFactory
import com.example.qrscan.viewmodel.HandleMultipleScanVM
import com.example.qrscan.viewmodel.HandleResultFactory
import com.example.qrscan.viewmodel.HandleResultVM
import com.example.qrscan.viewmodel.ShareDataVM
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress(
    "DEPRECATION",
    "DEPRECATED_IDENTITY_EQUALS",
    "UseCompatLoadingForDrawables",
    "SimpleDateFormat"
)
class FragmentScan : BaseFragment<QrScanFragmentBinding>() {
    private val sharedPreferences by lazy {
        requireActivity().getSharedPreferences(CONFIG_APP, Context.MODE_PRIVATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)

    private val handleResultVM: HandleResultVM by activityViewModels {
        HandleResultFactory((requireActivity().application as App).handleResultScanRepository)
    }


    private var typeCode = ""
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var m: MediaPlayer
    private var isVibrate: Boolean = false
    private var isFAQs: Boolean = false
    private var isSound: Boolean = false
    private var isPickImage: Boolean = false
    private var isScanning: Boolean = false
    private var isBarcodeActive: Boolean = false
    private var isMultipleScanActive: Boolean = false
    private lateinit var vibrator: Vibrator
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    var idScan = 0
    private var searchEngine = ""
    private val scanVM: ShareDataVM by activityViewModels()
    private val handleMultipleScanVM: HandleMultipleScanVM by activityViewModels {
        HandleMultipleSCanFactory((requireActivity().application as App).handleMultipleScanRepository)
    }

    private val activity by lazy {
        requireActivity() as MainActivity
    }
    private val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA
    )

    companion object {
        const val CONFIG_APP = "config_app"
        const val CAMERA_ID_BACK = -1
        const val IMAGE_PICKED = 8
        const val SAVE_SOUND = "save_sound"
        const val SAVE_FAQ = "save_faq"
        const val SAVE_VIBRATE = "save_vibrate"
        const val BARCODE = "barcode"
        const val QRCODE = "qrcode"
        const val MODEL_SCAN = "model_scan"
    }

    override fun getLayout(): Int = R.layout.qr_scan_fragment
    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun onStart() {
        super.onStart()
        cameraListener()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams", "MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PermissionsHelper.areAllPermissionsGranted(requireActivity(), cameraPermissions)) {
            visibleScan()
        }
       // initPremium()
        bottomSheetDialog = BottomSheetDialog(
            requireContext(), R.style.CustomBottomSheetDialogTheme
        )
        isFAQs = sharedPreferences.getBoolean(SAVE_FAQ, false)
        isVibrate = sharedPreferences.getBoolean(SAVE_VIBRATE, false)
        isSound = sharedPreferences.getBoolean(SAVE_SOUND, false)
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (sharedPreferences.getString(
                SettingFragment.SAVE_SEARCH_ENGINE,
                "http://www.google.com/search?q="
            ) != null
        ) {
            searchEngine = sharedPreferences.getString(
                SettingFragment.SAVE_SEARCH_ENGINE,
                "http://www.google.com/search?q="
            )!!
        }

        cameraListener()
        initObserver()
        isActiveBarCode()
        isActiveMultipleScan()
        updateQuantityScan()
        mBinding?.apply {
            layoutNoAccessCamera.apply {
                if (sharedPreferences.getBoolean(TryFreeDialogFragment.TRY_FREE, false)) {
                    lottieAnim.gone()
                }
                lottieAnim.imageAssetsFolder = "images2"
                btnGrantAccess.onAvoidDoubleClick {
                    if (!PermissionsHelper.areAllPermissionsGranted(
                            requireActivity(), cameraPermissions
                        )
                    ) {
                        activity.requestPermissionCamera()
                        isPickImage = false
                    } else {
                        visibleScan()
                    }
                }

                layoutCameraScan.apply {
                    layoutScanOption.apply {

                        ivImageOption.setOnSingleClickListener {
                            if (!GrantPermission.hasMediaPermission(activity)) {
                                activity.requestPermissionStorage()
                                isPickImage = true
                            } else {
                                val intent = Intent(
                                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                startActivityForResult(intent, IMAGE_PICKED)
                            }
                        }

                        barCodeOption.setOnSingleClickListener {
                            if (isBarcodeActive) {
                                isBarcodeActive = false
                                scanVM.setBarcodeActive(false)
                            } else {
                                isBarcodeActive = true
                                scanVM.setBarcodeActive(true)
                            }
                        }

                        lottieAnim.imageAssetsFolder = "images"

                        ivbMultipleScan.setOnSingleClickListener {
                            if (isMultipleScanActive) {
                                isMultipleScanActive = false
                                scanVM.setMultipleScanActive(false)
                            } else {
                                isMultipleScanActive = true
                                scanVM.setMultipleScanActive(true)
                            }
                        }

                    }

                    layoutMultipleScan.apply {

                        ivbDelete.setOnSingleClickListener {
                            hideMultipleScan()
                        }

                        btnDone.setOnSingleClickListener {
                            findNavController().navigate(R.id.action_fragmentScan_to_scanAndResultFragment)
                        }

                    }

                    layoutFAQ.apply {
                        ivbDelete.setOnSingleClickListener {
                            hideFAQs()
                        }
                        btnFaq.setOnSingleClickListener {
                            FAQDialogFragment().show(
                                requireActivity().supportFragmentManager,
                                FAQDialogFragment.FAQ
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initPremium() {
        mBinding?.apply {
            layoutNoAccessCamera.apply {
                if (sharedPreferences.getBoolean(TryFreeDialogFragment.TRY_FREE, false)) {
                    lottieAnim.gone()
                }
            }
        }
    }

    private fun showFAQs() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutFAQ.layout.visible()
                tvNote.gone()
            }
        }
    }

    private fun hideFAQs() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutFAQ.layout.gone()
                tvNote.visible()
            }
        }
    }

    private fun showMultipleScan() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutMultipleScan.layout.visible()
                tvNote.gone()
            }
        }
    }

    private fun hideMultipleScan() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutMultipleScan.layout.gone()
                tvNote.visible()
            }
        }
    }

    private fun showDialogSearchBarcode() {
        val dialogBinding: DialogSearchBarcodeBinding =
            DialogSearchBarcodeBinding.inflate(layoutInflater, null, false)
        dialogBinding.apply {
            btnSearch.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            ivbClose.setOnSingleClickListener { bottomSheetDialog.dismiss() }
            btnSearch.setOnSingleClickListener {
                val uri =
                    Uri.parse("${searchEngine}${edBarcode.text.toString().trim()}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            edBarcode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edBarcode.text.toString().trim() == "") {
                        btnSearch.isEnabled = false
                        btnSearch.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                    } else {
                        btnSearch.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        btnSearch.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        bottomSheetDialog.apply {
            setContentView(dialogBinding.root)
            show()
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityScan() {
        mBinding?.apply {
            layoutCameraScan.apply {
                handleMultipleScanVM.listResult.observe(viewLifecycleOwner) { list ->
                    layoutMultipleScan.tvTotalScan.text =
                        "${list.size} ${requireActivity().getString(R.string.scans)}"
                }
            }
        }
    }

    private fun isActiveMultipleScan() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutScanOption.apply {
                    scanVM.multipleScanActive.observe(viewLifecycleOwner) { active ->
                        if (active == true) {
                            showFAQs()
                            ivbMultipleScan.setImageDrawable(requireContext().getDrawable(R.drawable.multiple_scan_active))
                        } else {
                            hideMultipleScan()
                            ivbMultipleScan.setImageDrawable(requireContext().getDrawable(R.drawable.multiple_scan))
                        }
                    }

                }
            }
        }
    }

    private fun isActiveBarCode() {
        mBinding?.apply {
            layoutCameraScan.apply {
                layoutScanOption.apply {
                    scanVM.barCodeActive.observe(viewLifecycleOwner) { active ->
                        if (active == true) {
                            ivScanView.visible()
                            lottieAnim.gone()
                            barCodeOption.setImageDrawable(requireContext().getDrawable(R.drawable.barcode_option_active))
                            showDialogSearchBarcode()
                        } else {
                            ivScanView.gone()
                            lottieAnim.visible()
                            barCodeOption.setImageDrawable(requireContext().getDrawable(R.drawable.barcode_option))
                            bottomSheetDialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun cameraListener() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            try {
                val imageUri: Uri? = data?.data
                val imageStream: InputStream = imageUri?.let {
                    requireActivity().contentResolver.openInputStream(
                        it
                    )
                }!!
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                try {
                    val contents: String?
                    val intArray = IntArray(selectedImage.width * selectedImage.height)
                    selectedImage.getPixels(
                        intArray,
                        0,
                        selectedImage.width,
                        0,
                        0,
                        selectedImage.width,
                        selectedImage.height
                    )
                    val source: LuminanceSource = RGBLuminanceSource(
                        selectedImage.width, selectedImage.height, intArray
                    )
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    val reader: Reader = MultiFormatReader()
                    val result: Result = reader.decode(bitmap)
                    contents = result.text
                    val typeResult: String = GetTypeCodeScan.checkTypeResult(contents)
                    val currentDateTime = LocalDateTime.now()
                    val time = currentDateTime.format(formatter)
                    val intent = Intent(requireContext(), ResultActivity::class.java)

                    val model = contents?.let {
                        ResultScanModel(
                            result = it,
                            time = time,
                            typeCodeScan = if (result.barcodeFormat == BarcodeFormat.CODE_128) {
                                BARCODE
                            } else {
                                QRCODE
                            },
                            typeResult = typeResult,
                            scanned = true
                        )
                    }
                    model?.let { handleResultVM.insertResultScan(it) }

                    CoroutineScope(Dispatchers.Main).launch {
                        intent.putExtra(MODEL_SCAN, model)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "You haven't picked Image", Toast.LENGTH_LONG).show()
        }

    }

    private fun vibrateScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun playBeep() {
        m = MediaPlayer()
        try {
            if (m.isPlaying) {
                m.stop()
                m.release()
                m = MediaPlayer()
            }
            val descriptor =
                requireActivity().assets.openFd("sound_beep.mp3" as String? ?: "beep.mp3")
            m.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()
            m.prepare()
            m.setVolume(1f, 1f)
            m.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initObserver() {
        scanVM.permissionCamera.observe(viewLifecycleOwner) {
            if (it) {
                visibleScan()
            }
        }
        scanVM.permissionStorage.observe(viewLifecycleOwner) {
            if (it && isPickImage) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, IMAGE_PICKED)
            }
        }
    }

    private fun visibleScan() {
        isScanning = true
        mBinding?.apply {
            layoutNoAccessCamera.layout.gone()
            layoutCameraScan.apply {
                layout.visible()
                barcodeScanner.visible()
                overlay.post {
                    overlay.setViewFinder()
                }
            }
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        cameraProvider?.unbindAll()
        val preview: Preview = Preview.Builder().build()
        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val imageAnalysis = ImageAnalysis.Builder().setTargetResolution(
            Size(
                mBinding!!.layoutCameraScan.barcodeScanner.width,
                mBinding!!.layoutCameraScan.barcodeScanner.height
            )
        ).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        val orientationEventListener = object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
        class ScanningListener : ScannerListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScanned(result: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    val typeResult = GetTypeCodeScan.checkTypeResult(result)
                    if (isMultipleScanActive) {
                        val listMultipleScan = handleMultipleScanVM.listResult.value
                        if (listMultipleScan?.isNotEmpty() == true && result == listMultipleScan.first().value) {
                            Toast.makeText(
                                requireContext(),
                                requireContext().getText(R.string.you_scanned_this_QR_code_already),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            imageAnalysis.clearAnalyzer()
                            cameraProvider?.unbindAll()
                            val currentDateTime = LocalDateTime.now()
                            val time = currentDateTime.format(formatter)
                            idScan++
                            handleMultipleScanVM.insertItem(
                                MultipleScanModel(
                                    imageResult = R.drawable.scan,
                                    value = result,
                                    type = typeResult,
                                    typeCode = if (typeCode == BARCODE) {
                                        BARCODE
                                    } else {
                                        QRCODE
                                    },
                                    time = time
                                )
                            )
                            handleResultVM.insertResultScan(
                                ResultScanModel(
                                    result = result,
                                    time = time,
                                    typeCodeScan = if (typeCode == BARCODE) {
                                        BARCODE
                                    } else {
                                        QRCODE
                                    },
                                    typeResult = typeResult,
                                    scanned = true
                                )
                            )
                            showMultipleScan()
                            hideFAQs()
                            if (isVibrate) {
                                vibrateScan()
                            }
                            if (isSound) {
                                playBeep()
                            }
                            cameraExecutor.shutdown()
                            launch { delay(1000) }
                            cameraListener()
                        }
                    } else {
                        imageAnalysis.clearAnalyzer()
                        cameraProvider?.unbindAll()
                        if (isVibrate) {
                            vibrateScan()
                        }
                        if (isSound) {
                            playBeep()
                        }
                        val currentDateTime = LocalDateTime.now()
                        val time = currentDateTime.format(formatter)
                        val intent = Intent(requireContext(), ResultActivity::class.java)
                        val model = ResultScanModel(
                            result = result,
                            time = time,
                            typeCodeScan = if (typeCode == BARCODE) {
                                BARCODE
                            } else {
                                QRCODE
                            },
                            typeResult = typeResult,
                            scanned = true
                        )
                        handleResultVM.insertResultScan(model)
                        intent.putExtra(MODEL_SCAN, model)
                        startActivity(intent)
                    }
                }
            }

            override fun checkTypeCode(type: String) {
                typeCode = type
            }
        }

        val analyzer: ImageAnalysis.Analyzer = ImageCodeAnalysis(ScanningListener())
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
        preview.setSurfaceProvider(mBinding!!.layoutCameraScan.barcodeScanner.surfaceProvider)
        val camera = cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            mBinding!!.layoutCameraScan.layoutScanOption.ivFlashOption.visibility = View.VISIBLE

            mBinding!!.layoutCameraScan.layoutScanOption.ivFlashOption.setOnSingleClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }

            camera.cameraInfo.torchState.observe(viewLifecycleOwner) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        mBinding!!.layoutCameraScan.layoutScanOption.ivFlashOption.setImageResource(
                            R.drawable.flash_on
                        )
                    } else {
                        flashEnabled = false
                        mBinding!!.layoutCameraScan.layoutScanOption.ivFlashOption.setImageResource(
                            R.drawable.flash_off1
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanVM.clearVM()
    }

    override fun onStop() {
        super.onStop()
        cameraExecutor.shutdown()
    }
}