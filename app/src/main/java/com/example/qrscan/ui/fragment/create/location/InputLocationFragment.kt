package com.example.qrscan.ui.fragment.create.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.qrscan.R
import com.example.qrscan.base.BaseFragment
import com.example.qrscan.databinding.FragmentInputLocationBinding
import com.example.qrscan.extension.gone
import com.example.qrscan.extension.invisible
import com.example.qrscan.extension.setOnSingleClickListener
import com.example.qrscan.extension.visible
import com.example.qrscan.ui.activity.create.CreateQrActivity
import com.example.qrscan.ui.activity.create.QrResultActivity
import com.example.qrscan.util.PermissionsHelper
import com.example.qrscan.util.TypeResult
import com.example.qrscan.viewmodel.ShareDataVM
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern


class InputLocationFragment : BaseFragment<FragmentInputLocationBinding>(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private lateinit var mLastLocation: Location
    private var mCurrentMarker: Marker? = null
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mLocationRequest: com.google.android.gms.location.LocationRequest
    private val scanVM: ShareDataVM by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val activity by lazy {
        requireActivity() as CreateQrActivity
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm", Locale.US)

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun getLayout(): Int = R.layout.fragment_input_location

    override fun setInsets(left: Int, top: Int, right: Int, bottom: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (PermissionsHelper.areAllPermissionsGranted(requireActivity(), locationPermissions)) {
            initMap()
        }
        observeMap()
        mBinding?.apply {
            ivBack.setOnSingleClickListener { requireActivity().finish() }
            btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
            btnCreated.isEnabled = false
            edtLocationTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtLongtitude.text.toString()
                            .trim() == "" || edtLatitude.text.toString()
                            .trim() == "" || edtLocationTitle.text.toString().trim() == ""
                    ) {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        btnCreated.isEnabled = false
                    } else {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        btnCreated.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            edtLongtitude.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtLongtitude.text.toString()
                            .trim() == "" || edtLatitude.text.toString()
                            .trim() == "" || edtLocationTitle.text.toString()
                            .trim() == "" || !validateLatitude(edtLatitude.text.toString()) || !validateLongitude(
                            edtLongtitude.text.toString()
                        )
                    ) {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        btnCreated.isEnabled = false
                    } else {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        btnCreated.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })


            edtLatitude.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (edtLongtitude.text.toString()
                            .trim() == "" || edtLatitude.text.toString()
                            .trim() == "" || edtLocationTitle.text.toString()
                            .trim() == "" || !validateLatitude(edtLatitude.text.toString()) || !validateLongitude(
                            edtLongtitude.text.toString()
                        )
                    ) {

                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                        btnCreated.isEnabled = false
                    } else {
                        btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                        btnCreated.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            btnSetLocation.setOnSingleClickListener {
                if (!PermissionsHelper.areAllPermissionsGranted(
                        requireActivity(),
                        locationPermissions
                    )
                ) {
                    activity.requestPermissionLocation()
                    return@setOnSingleClickListener
                } else {
                    initMap()
                    val result = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token
                    )

                    result.addOnCompleteListener { location ->
                        edtLatitude.setText(location.result.latitude.toString().trim())
                        edtLongtitude.setText(location.result.longitude.toString().trim())
                    }
                }
            }

            btnCreated.setOnSingleClickListener {
                handleOnClickBtnCreate()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateLatitude(latitude: String): Boolean {
        mBinding?.apply {
            val latitudePattern = Pattern.compile("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$")
            return if (latitudePattern.matcher(latitude).matches()) {
                edtLatitude.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinateLat.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edtLatitude.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinateLat.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun validateLongitude(longitude: String): Boolean {
        mBinding?.apply {
            val longitudePattern =
                Pattern.compile("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$")
            return if (longitudePattern.matcher(longitude).matches()) {
                edtLongtitude.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result))
                tvInvalidCoordinateLong.invisible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_blue))
                btnCreated.isEnabled = true
                true
            } else {
                edtLongtitude.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_bg_scan_result_valid))
                tvInvalidCoordinateLong.visible()
                btnCreated.setBackgroundDrawable(requireContext().getDrawable(R.drawable.custom_button_gray))
                btnCreated.isEnabled = false
                false
            }
        }
        return false
    }

    private fun initMap() {
        mBinding?.apply {
            mapView.visible()
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
            mapFragment.getMapAsync(this@InputLocationFragment)
        }
    }

    private fun observeMap() {
        scanVM.permissionLocation.observe(viewLifecycleOwner) {
            if (it == true) {
                initMap()
            } else {
                mBinding!!.mapView.gone()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnClickBtnCreate() {
        val currentDateTime = LocalDateTime.now()
        val time = currentDateTime.format(formatter)
        val intent = Intent(activity, QrResultActivity::class.java)
        val bundle = Bundle()
        mBinding?.apply {
            bundle.putString("TYPE", TypeResult.LOCATION)
            bundle.putString("nameLocation", edtLocationTitle.text.toString().trim())
            bundle.putString("latitude", edtLatitude.text.toString().trim())
            bundle.putString("longtitude", edtLongtitude.text.toString().trim())
            bundle.putString("time", time)
        }
        intent.putExtras(bundle)
        startActivity(intent)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        val latLng = LatLng(21.0, 105.0)
        mGoogleMap?.apply {
            addMarker(MarkerOptions().position(latLng))
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            moveCamera(CameraUpdateFactory.zoomTo(11f))
        }
    }

}