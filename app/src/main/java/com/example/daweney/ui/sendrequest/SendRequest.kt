package com.example.daweney.ui.sendrequest

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.intent_extra_key.IntentExtraKey
import com.example.daweney.pojo.send_request.SendRequestBody
import com.example.daweney.pojo.services.ServicesResponse
import com.example.daweney.pojo.services.ServicesResponseItem
import com.example.daweney.repo.SendRequestRepository
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.myrequests.MyRequests
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_send_request.*
import java.text.SimpleDateFormat
import java.util.*

class SendRequest : LocalizationActivity(), AskPermissionInterface {
    private val sendRequestResponse = SendRequestRepository(this)
    private val MY_PERMISSIONS_REQUEST_LOCATION = 123
    private lateinit var dateEditText: EditText
    private lateinit var genderEditView: AutoCompleteTextView
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var selectionServicesRecyclerView: RecyclerView
    private lateinit var patientNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sendRequestCardView: CardView
    private lateinit var sendRequestAnimation: LottieAnimationView
    private var servicesList: ArrayList<ServicesResponseItem>? = null
    private lateinit var sendRequestViewModel: SendRequestViewModel
    private lateinit var date: String
    private lateinit var serviceType: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_request)
        viewModelInit()
        initialization()
        initGenderMenu()
        sendRequestSuccessObserver()
        sendRequestFailureObserver()
        sendRequestNoProviderObserver()
        sendRequestErrorObserver()
        progressBarObserver()
        getServicesFormIntent()
        patientNameTextWatcher()
        phoneTextWatcher()
        dateTextWatcher()
        addressTextWatcher()
        locationTextWatcher()
        dateEditText.setOnClickListener { onClick(it) }
        locationEditText.setOnClickListener { onClick(it) }
        genderEditView.setOnClickListener { onClick(it) }
        sendButton.setOnClickListener { onClick(it) }
        goBack.setOnClickListener { onClick(it) }
        setLightStatusBar()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    private fun viewModelInit() {
        val sendRequestViewModelFactory = SendRequestViewModelFactory(this)
        sendRequestViewModel =
            ViewModelProvider(this, sendRequestViewModelFactory)[SendRequestViewModel::class.java]
    }

    private fun progressBarObserver() {
        sendRequestViewModel.progressBar.observe(this) {
            if (it) {
                progressBar.visibility = ViewGroup.VISIBLE
            } else {
                progressBar.visibility = ViewGroup.GONE
            }
        }
    }

    private fun sendRequestErrorObserver() {
        sendRequestViewModel.sendRequestInvalidError.observe(this) {
            if (it) {
                val dialogFragment = CustomDialogFragment(
                    getString(R.string.invalid_data_entered),
                    applicationContext
                )
                dialogFragment.show(supportFragmentManager, "send_request_error_dialog")
            }
        }
    }

    private fun sendRequestNoProviderObserver() {
        sendRequestViewModel.sendRequestNoProvider.observe(this) {
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(getString(R.string.no_provider), applicationContext)
                dialogFragment.show(supportFragmentManager, "send_request_No_provider_error_dialog")
            }
        }
    }

    private fun sendRequestSuccessObserver() {
        sendRequestViewModel.sendRequestSuccess.observe(this) {
            if (it) {
                sendRequestCardView.visibility = ViewGroup.GONE
                sendRequestAnimation.visibility = ViewGroup.VISIBLE
                sendRequestAnimation.setAnimation(R.raw.send_animation)
                sendRequestAnimation.playAnimation()
                sendRequestAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        val intent = Intent(this@SendRequest, MyRequests::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finishAffinity()
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })

            }
        }
    }

    private fun sendRequestFailureObserver() {
        sendRequestViewModel.sendRequestFailure.observe(this) {
            if (it) {
                val dialogFragment = CustomDialogFragment(
                    getString(R.string.error_with_connection),
                    applicationContext
                )
                dialogFragment.show(supportFragmentManager, "send_request_failure_dialog")

            }
        }
    }

    private fun patientNameTextWatcher() {
        patientNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {

                    patientNameEditText.error = getString(R.string.name_required_msg)
                } else {

                    if (isValidName(s.toString())) {
                        patientNameEditText.error = null
                    } else {
                        patientNameEditText.error = getString(R.string.name_not_valid)
                    }

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun isValidName(name: String): Boolean {
        val regex =
            Regex("^[\u0621-\u064Aa-zA-Z]+(([',. -][\u0621-\u064Aa-zA-Z ])?[\u0621-\u064Aa-zA-Z]*)*[\u0621-\u064Aa-zA-Z ]?\$")
        return regex.matches(name)
    }

    private fun phoneTextWatcher() {
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {

                    phoneEditText.error = getString(R.string.phone_required_msg)
                } else {
                    if ((s.length == 11 && s[0] == '0') || (s.length == 10 && s[0] != '0')) {
                        phoneEditText.error = null
                    } else {
                        phoneEditText.error = getString(R.string.phone_not_valid)
                    }

                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun dateTextWatcher() {
        dateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    dateEditText.error = getString(R.string.date_required_msg)
                } else {
                    dateEditText.error = null
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun addressTextWatcher() {
        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    addressEditText.error = getString(R.string.address_required_msg)
                } else {
                    addressEditText.error = null
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun locationTextWatcher() {
        locationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    locationEditText.error = getString(R.string.address_required_msg)
                } else {
                    locationEditText.error = null
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun getServicesFormIntent() {
        servicesList =
            intent.getSerializableExtra(IntentExtraKey.SERVICES) as? ArrayList<ServicesResponseItem>

        if (servicesList != null) {
            val selectionServices = servicesList!!.toCollection(ServicesResponse())
            servicesAdapter = ServicesAdapter(selectionServices)
            selectionServicesRecyclerView.layoutManager = LinearLayoutManager(this)
            selectionServicesRecyclerView.adapter = servicesAdapter
        }
        serviceType = intent.getStringExtra(IntentExtraKey.SERVICE_TYPE).toString()
    }

    private fun initGenderMenu() {

        val menuInflater = MenuInflater(this)
        val menu = PopupMenu(this, null).menu
        menuInflater.inflate(R.menu.gender_menu_spinner, menu)
        val itemList = mutableListOf<String>()
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            itemList.add(item.title.toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itemList)
        genderEditText.setAdapter(adapter)
        genderEditText.setText(itemList[0], false)
    }

    private fun initialization() {
        selectionServicesRecyclerView = findViewById(R.id.selectionServicesRecyclerView)
        dateEditText = findViewById(R.id.dateEditText)
        genderEditView = findViewById(R.id.genderEditText)
        patientNameEditText = findViewById(R.id.patientNameEditText)
        phoneEditText = findViewById(R.id.phoneNumberEditText)
        addressEditText = findViewById(R.id.requestAddressEditText)
        locationEditText = findViewById(R.id.requestLocationEditText)
        sendButton = findViewById(R.id.sendRequest)
        progressBar = findViewById(R.id.progressBar)
        sendRequestCardView = findViewById(R.id.sendRequestCardView)
        sendRequestAnimation = findViewById(R.id.sendRequestAnimation)
    }

    private fun onClick(view: View?) {
        when (view?.id) {
            R.id.dateEditText -> {
                showDatePickerDialog()
            }

            R.id.genderEditText -> {
                genderEditView.showDropDown()
            }

            R.id.sendRequest -> {
                if (allFieldValid()) sendRequestViewModel.sendRequest(getRequest())

            }

            R.id.goBack -> {
                finish()
            }

            R.id.requestLocationEditText -> {
                when {
                    !isGPSEnabled() -> {
                        snackBar(
                            getText(R.string.gps_not_enable).toString(),
                            ::requestGPSEnabling,
                            getText(R.string.enable_GPS).toString()
                        )

                    }

                    else -> {
                        checkPermission()
                    }
                }
            }
        }
    }


    private fun snackBar(msg: String, requestGPSEnabling: () -> Unit, btnText: String) {
        val rootView: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            msg,
            Snackbar.LENGTH_LONG
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundResource(R.drawable.custom_snackbar)
        snackbar.setAction(btnText) {
            if (it != null) requestGPSEnabling()

        }
        snackbar.show()
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        progressBar.visibility = ViewGroup.VISIBLE
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    locationEditText.setText("long:$longitude  lat:$latitude")
                    progressBar.visibility = ViewGroup.INVISIBLE
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestGPSEnabling() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    dialogPermission(getText(R.string.location_required).toString())
                    //showToast("Location permission denied. Please grant the permission to use this feature.")
                }
            }
        }
    }


    private fun dialogPermission(msg: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            checkPermission()
            return
        }
        val permissionDialog = PermissionDialogFragment(msg, this)
        permissionDialog.show(supportFragmentManager, "permission dialog")

    }


    override fun askLocationPermissionInterface() {

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = packageName

        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri

        startActivity(intent)
    }

    private fun setLightStatusBar() {
        val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme
            window.decorView.systemUiVisibility = 0

        } else {
            // Light theme
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun allFieldValid(): Boolean {
        if (allFieldNotEmpty()) {

            if (patientNameEditText.error != null) {
                showToast(getString(R.string.name_not_valid))
            } else if (phoneEditText.error != null) {
                showToast(getString(R.string.phone_not_valid))
            } else if (dateEditText.error != null) {
                showToast(getString(R.string.date_not_valid))
            } else if (addressEditText.error != null) {
                showToast(getString(R.string.address_required_msg))
            } else if (locationEditText.error != null) {
                showToast(getString(R.string.address_required_msg))
            } else {
                return true
            }
        } else {
            return false
        }
        return false
    }

    private fun allFieldNotEmpty(): Boolean {

        if (patientNameEditText.text.isNullOrEmpty()) {
            patientNameEditText.error = getString(R.string.name_required_msg)
        } else if (phoneEditText.text.isNullOrEmpty()) {
            phoneEditText.error = getString(R.string.phone_required_msg)
        } else if (dateEditText.text.isNullOrEmpty()) {
            dateEditText.error = getString(R.string.date_required_msg)
        } else if (addressEditText.text.isNullOrEmpty()) {
            addressEditText.error = getString(R.string.address_required_msg)
        } else if (locationEditText.text.isNullOrEmpty()) {
            locationEditText.error = getString(R.string.address_required_msg)
        } else {
            return true
        }
        return false
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun getRequest(): SendRequestBody {
        return SendRequestBody(
            addressEditText.text.toString(), getCustomerId(),
            getProviderGender(), latitude, longitude, patientNameEditText.text.toString(),
            phoneEditText.text.toString(), getServicesList(), date, serviceType
        )
    }

    private fun getProviderGender(): String {
        return when (genderEditView.text.toString()) {
            getString(R.string.female) -> {
                "female"
            }

            getString(R.string.male) -> {
                "male"
            }

            else -> {
                "anyone"
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DateAndTimeDialog,
            { _, year, month, dayOfMonth ->
                // Check if the selected date is in the past
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val currentDate = Calendar.getInstance()
                if (selectedDate.before(currentDate)) {
                    // Show an error message or prevent further action
                    Toast.makeText(this, getString(R.string.select_future_date), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    showTimePickerDialog(year, month, dayOfMonth)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            R.style.DateAndTimeDialog,
            { _, hourOfDay, minute ->
                // Check if the selected time is in the past
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth, hourOfDay, minute)
                val currentDate = Calendar.getInstance()
                val timeDifferenceMinutes =
                    (selectedDate.timeInMillis - currentDate.timeInMillis) / (1000 * 60)

                if (selectedDate.before(currentDate)) {
                    // Show an error message or prevent further action
                    Toast.makeText(this, getString(R.string.select_future_time), Toast.LENGTH_SHORT)
                        .show()
                } else if (timeDifferenceMinutes < 30) {
                    // Show a different error message if time is less than 30 minutes from current time
                    Toast.makeText(
                        this,
                        getString(R.string.select_time_more_than_30m),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault())
                    val requestDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("en"))
                    date = requestDateFormat.format(selectedDate.time)
                    val formattedDate = dateFormat.format(selectedDate.time)
                    dateEditText.setText(formattedDate)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun getCustomerId(): String {
        val customerId = sendRequestResponse.getCustomerId()
        if (customerId.isEmpty()) {
            logout()
        }
        return customerId
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        sendRequestResponse.deleteCustomerId()
        startActivity(intent)
    }

    private fun getServicesList(): List<String> {
        val servicesIds = ArrayList<String>()
        for (service in servicesList!!) {
            servicesIds.add(service._id)
        }
        return servicesIds
    }


}