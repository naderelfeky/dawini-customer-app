package com.example.daweney.ui.myrequests


import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.intent_extra_key.IntentExtraKey
import com.example.daweney.pojo.myrequests.RequestBody
import com.example.daweney.pojo.myrequests.RequestResponseItem
import com.example.daweney.pojo.update_profile.LocalData
import com.example.daweney.repo.RequestRepository
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.myrequestdetails.MyRequestDetails
import com.example.daweney.ui.profile.Profile
import com.example.daweney.ui.servicescategory.ServicesCategory
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_requests.addRequest
import kotlinx.android.synthetic.main.activity_my_requests.messageContainer
import kotlinx.android.synthetic.main.activity_my_requests.messageLabel
import kotlinx.android.synthetic.main.activity_my_requests.messageTV
import kotlinx.android.synthetic.main.activity_my_requests.myRequestLottieAnimationView
import kotlinx.android.synthetic.main.activity_my_requests.navigation_drawer
import kotlinx.android.synthetic.main.activity_my_requests.progressBar
import kotlinx.android.synthetic.main.activity_my_requests.refreshMyRequest
import kotlinx.android.synthetic.main.activity_my_requests.refreshMyRequestList
import kotlinx.android.synthetic.main.activity_my_requests.searchContainer
import kotlinx.android.synthetic.main.activity_my_requests.toolbarIconSearch
import kotlinx.android.synthetic.main.activity_my_requests.toolbarSearch
import kotlinx.android.synthetic.main.activity_my_requests.toolbar_navigation_drawer
import kotlinx.android.synthetic.main.activity_my_requests.try_again_btn

class MyRequests : LocalizationActivity(), MyRequestInterface, AskPermissionInterface {
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var requestAdapter: RequestRecyclerViewAdapter
    private lateinit var closeDrawerButton: ImageView
    private lateinit var switchTheme: SwitchCompat
    private lateinit var switchLanguage: SwitchCompat
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var localData: LocalData
    private lateinit var profileImg: CircleImageView
    private lateinit var userNameTextView: TextView
    private val requestRepository = RequestRepository(this)
    private var myRequestsViewModel = MyRequestsViewModel(this)
    private val DELAY_OF_SWITCH_ACTION: Long = 500
    private val REQUEST_CALL_PHONE_PERMISSION = 111
    private val DARK = "dark"
    private val LIGHT = "light"
    private val ARABIC = "arabic"
    private val ARABIC_CODE = "ar"
    private val ENGLISH = "english"
    private val ENGLISH_CODE = "en"
    private val PHONENUMBER = "+201201545101"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_requests)
        viewModelInit()
        initialization()
        getMyRequest()
        actionBarDrawer()
        onNavigationItemClick()
        myRequestObserver()
        onEmptyRequestListObserver()
        onFailureObserver()
        searcherChangeListener()
        progressBarObserve()
        toolbarIconSearch.setOnClickListener { onClick(it) }
        addRequest.setOnClickListener { onClick(it) }
        try_again_btn.setOnClickListener { onClick(it) }
        toolbar_navigation_drawer.setOnClickListener { onClick(it) }
        closeDrawerButton.setOnClickListener { onClick(it) }

        // to listen to switch (theme & language)
        setAppTheme(navigationView.menu.findItem(R.id.app_theme))
        setAppLanguage(navigationView.menu.findItem(R.id.app_language))

        getProfileData()
        setNavigationHeaderInfo()
        refreshMyRequest()
        setLightStatusBar()


    }



    private fun viewModelInit() {

        //view model
        val myRequestsViewModelFactory = MyRequestsViewModelFactory(this)
        myRequestsViewModel =
            ViewModelProvider(this, myRequestsViewModelFactory)[MyRequestsViewModel::class.java]
    }

    private fun initialization() {
        initializeRecyclerView()

        profileImg = navigation_drawer.getHeaderView(0).findViewById(R.id.profile_img)
        userNameTextView = navigation_drawer.getHeaderView(0).findViewById(R.id.username)
        //close button in header of navigation drawer
        closeDrawerButton = navigation_drawer.getHeaderView(0).findViewById(R.id.navigation_back)

        // Find the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        navigationView = findViewById<NavigationView>(R.id.navigation_drawer)

    }

    private fun initializeRecyclerView() {
        // recyclerView
        recyclerView = findViewById(R.id.requestRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getMyRequest() {
        //call get request
        myRequestsViewModel.getMyRequests(RequestBody(getCustomerId()))
    }

    private fun actionBarDrawer() {
        toggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()
    }

    private fun onNavigationItemClick() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    openProfile()
                }

                R.id.app_theme -> {
                    setAppTheme(menuItem)
                }

                R.id.app_language -> {
                    setAppLanguage(menuItem)
                }

                R.id.whatsApp -> {
                    openWhatsAppWithNumber(PHONENUMBER)
                }

                R.id.phone -> {
                    checkCallPhonePermission()
                }

                R.id.logout -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun myRequestObserver() {

        myRequestsViewModel.myRequestsResponse.observe(this) {

            recyclerView.visibility = View.VISIBLE
            myRequestLottieAnimationView.visibility = View.GONE
            try_again_btn.visibility = View.GONE
            messageContainer.visibility = View.GONE
            requestAdapter = RequestRecyclerViewAdapter(applicationContext,it, this)
            recyclerView.adapter = requestAdapter
        }
    }

    private fun onEmptyRequestListObserver() {
        myRequestsViewModel.emptyRequest.observe(this) {
            recyclerView.visibility = View.GONE
            myRequestLottieAnimationView.visibility = View.VISIBLE
            messageContainer.visibility = View.VISIBLE
            try_again_btn.visibility = View.GONE
            messageTV.text = getString(R.string.request_new_service)
            messageLabel.setText(getString(R.string.empty_request_list))
            myRequestLottieAnimationView.setAnimation(R.raw.empty_request)
            myRequestLottieAnimationView.playAnimation()
        }

    }

    private fun onFailureObserver() {
        myRequestsViewModel.failure.observe(this) {
            if (isInternetAvailable(applicationContext)) {
                getMyRequest()
            } else {

                recyclerView.visibility = View.GONE
                myRequestLottieAnimationView.visibility = View.VISIBLE
                messageContainer.visibility = View.VISIBLE
                try_again_btn.visibility = View.VISIBLE
                messageLabel.setText(getString(R.string.oops))
                messageTV.text = getString(R.string.error_with_connection)

                myRequestLottieAnimationView.setAnimation(R.raw.no_connection)
                myRequestLottieAnimationView.playAnimation()
            }
        }
    }

    private fun searcherChangeListener() {

        toolbarSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::requestAdapter.isInitialized)
                    requestAdapter.filter.filter(toolbarSearch.text)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun progressBarObserve() {
        myRequestsViewModel.progressBar.observe(this) {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.toolbarIconSearch -> {
                if (searchContainer.width == resources.getDimensionPixelSize(R.dimen.toolBar_width)) {
                    openSearchBar()
                } else {
                    closeSearchBar()
                }
            }

            R.id.toolbar_navigation_drawer -> {
                drawerLayout.openDrawer(GravityCompat.START)

            }

            R.id.navigation_back -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.addRequest -> {
                val intent = Intent(this, ServicesCategory::class.java)
                startActivity(intent)
            }

            R.id.try_again_btn -> {
                getMyRequest()
            }

        }
    }

    private fun setAppTheme(menuItem: MenuItem) {
        if (!::switchTheme.isInitialized) {
            val switchView: View = menuItem.actionView!!
            switchTheme = switchView.findViewById(R.id.theme_switch)
            switchTheme.isChecked = loadTheme() == DARK

        }
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            val theme = if (isChecked) DARK else LIGHT
            val handler = Handler()
            handler.postDelayed({
                if (isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }, DELAY_OF_SWITCH_ACTION)
            requestRepository.setTheme(theme)
        }

    }

    private fun setAppLanguage(menuItem: MenuItem) {

        if (!::switchLanguage.isInitialized) {
            val switchView = menuItem.actionView!!
            switchLanguage = switchView.findViewById(R.id.language_switch)
            switchLanguage.isChecked = loadLanguage() == ENGLISH
        }
        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val handler = Handler()
            handler.postDelayed({
                if (isChecked) {
                    applyLanguage(ENGLISH_CODE, ENGLISH)
                } else {
                    applyLanguage(ARABIC_CODE, ARABIC)
                }
            }, DELAY_OF_SWITCH_ACTION)

        }
    }

    private fun getProfileData() {
        localData = requestRepository.getLocalData()
    }

    private fun setNavigationHeaderInfo() {

        localData.profileImg?.let {
            profileImg.setImageBitmap(it)
        }
        userNameTextView.text = localData.userName
    }

    private fun refreshMyRequest() {
        refreshMyRequest.setOnRefreshListener {  //refresh when my list id empty or network error
            myRequestsViewModel.getMyRequests(RequestBody(getCustomerId()))
            refreshMyRequest.isRefreshing = false
        }
        refreshMyRequestList.setOnRefreshListener {  //refresh when my list no empty
            myRequestsViewModel.getMyRequests(RequestBody(getCustomerId()))
            refreshMyRequestList.isRefreshing = false
        }
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

    private fun openWhatsAppWithNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun callNumber(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        val numberUri = Uri.parse("tel:$phoneNumber")
        callIntent.data = numberUri
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(callIntent)
        }
    }

    private fun checkCallPhonePermission() {

        when {
            isPermissionGranted(Manifest.permission.CALL_PHONE) -> {
                callNumber(PHONENUMBER)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                dialogPermission(getText(R.string.need_call_permission).toString())
            }

            else -> {
                askPermission(Manifest.permission.CALL_PHONE)
            }

        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askPermission(permission: String) {
        ActivityCompat.requestPermissions(
            this@MyRequests,
            arrayOf(permission),
            REQUEST_CALL_PHONE_PERMISSION
        )
    }

    private fun openProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    private fun loadLanguage(): String {
        return requestRepository.getLanguage()
    }

    private fun applyLanguage(languageCode: String, language: String) {
        val layoutDirection = when (languageCode) {
            ARABIC_CODE -> {
                ViewCompat.LAYOUT_DIRECTION_RTL
            }

            ENGLISH_CODE -> {
                ViewCompat.LAYOUT_DIRECTION_LTR
            }

            else -> {
                ViewCompat.LAYOUT_DIRECTION_LTR
            }
        }
        setLanguage(languageCode)   // Language code for English

        ViewCompat.setLayoutDirection(
            findViewById(android.R.id.content),
            layoutDirection
        )
        requestRepository.setLanguage(language)
    }

    private fun loadTheme(): String {
        return requestRepository.getTheme()
    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        requestRepository.deleteCustomerId()
        startActivity(intent)
    }

    private fun getCustomerId(): String {
        val customerId =
            requestRepository.getCustomerId()
        if (customerId.isEmpty()) {
            logout()
        }
        return customerId
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            // For devices with API level lower than 23
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun closeSearchBar() {
        val targetWidth = resources.getDimensionPixelSize(R.dimen.toolBar_width)
        val animator = ValueAnimator.ofInt(searchContainer.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = searchContainer.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            searchContainer.requestLayout()
        }
        animator.duration = 400 // Set the duration of the animation (in milliseconds)
        animator.start()
        toolbarIconSearch.setImageResource(R.drawable.ic_search)
    }

    private fun openSearchBar() {
        val displayMetrics = resources.displayMetrics
        val deviceWidth = displayMetrics.widthPixels

        val dpToPx = // end drawable width                   //two margin in start and end                            // distant between search bar and end drawable
            (resources.getDimension(R.dimen.toolBar_width))+(resources.getDimension(R.dimen.toolBar_button_margin)*2)+resources.getDimension(R.dimen.toolBar_search_end_margin)

        val targetWidth = deviceWidth - dpToPx.toInt()
        val animator = ValueAnimator.ofInt(searchContainer.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = searchContainer.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            searchContainer.requestLayout()
        }
        animator.duration = 400 // Set the duration of the animation (in milliseconds)
        animator.start()
        toolbarIconSearch.setImageResource(R.drawable.ic_left)
    }

    override fun onItemClick(request: RequestResponseItem) {
        val intent = Intent(this, MyRequestDetails::class.java)
        intent.putExtra(IntentExtraKey.MY_REQUEST, request)
        startActivity(intent)
    }

    override fun onItemLongClick(request: RequestResponseItem) {
        val intent = Intent(this, MyRequestDetails::class.java)
        intent.putExtra(IntentExtraKey.MY_REQUEST, request)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callNumber(PHONENUMBER)
            } else {
                Toast.makeText(this, getText(R.string.need_call_permission), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun dialogPermission(msg: String) {
        val permissionDialog = PermissionDialogFragment(msg, this)
        permissionDialog.show(supportFragmentManager, "permission dialog")
    }


    override fun callPhoneAskPermissionInterface() {

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = packageName

        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri

        startActivity(intent)

    }


}


