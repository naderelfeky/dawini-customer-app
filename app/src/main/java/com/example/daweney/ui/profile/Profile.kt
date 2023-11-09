package com.example.daweney.ui.profile

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.profile.ProfileBody
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.pojo.update_profile.LocalData
import com.example.daweney.repo.ProfileRepository
import com.example.daweney.ui.myrequests.MyRequests
import com.example.daweney.ui.updateprofile.UpdateProfile
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_profile.accountDetails
import kotlinx.android.synthetic.main.activity_profile.editButton
import kotlinx.android.synthetic.main.activity_profile.goBack
import kotlinx.android.synthetic.main.activity_profile.profileAnimation
import kotlinx.android.synthetic.main.activity_profile.profileDataLayout
import kotlinx.android.synthetic.main.activity_profile.profileNoConnection
import kotlinx.android.synthetic.main.activity_profile.progressBar
import kotlin.properties.Delegates

class Profile : LocalizationActivity() {
    private val profileImageAnimationTime: Long = 500
    private val imageFileName = "profileImg.jpg"
    private val profileRepository = ProfileRepository(this)
    private lateinit var localData: LocalData
    private lateinit var remoteData: ProfileResponse
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var profileImg: ImageView
    private lateinit var toolbarProfileImg: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var userName: TextView
    private var animationTranslationX by Delegates.notNull<Int>()
    private var animationTranslationY by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        viewModelInit()
        getProfile()
        initialization()
        progressBarObserve()
        collapsingChangeListener()
        getProfileSuccessObserve()
        getProfileFailureObserve()
        editButtonClicked()
        goBackButtonOnclickListener()
        setLightStatusBar()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MyRequests::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun viewModelInit() {
        profileViewModel =
            ViewModelProvider(this, ProfileViewModelFactory(this))[ProfileViewModel::class.java]
    }

    private fun getProfile() {
        profileViewModel.getProfile(ProfileBody(profileRepository.getCustomerId()))
    }

    private fun initialization() {
        appBarLayout = findViewById(R.id.appBarLayout)
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        userName = findViewById(R.id.userName)
        userNameEditText = findViewById(R.id.nameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        profileImg = findViewById<ImageView>(R.id.profileImg)
        toolbarProfileImg = findViewById<ImageView>(R.id.toolbarProfileImg)
    }

    private fun progressBarObserve() {
        profileViewModel.progressBar.observe(this) {
            progressBarStatus(it!!)
        }
        profileViewModel.progressBar.observe(this) {
            progressBarStatus(it!!)
        }
    }

    private fun collapsingChangeListener() {
        appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            when {
                (verticalOffset == totalScrollRange * -1) -> {  //all closed

                    getTranslationDistance()
                    setProfileImageAnimation(
                        .5f,
                        .5f,
                        animationTranslationX.toFloat(),
                        animationTranslationY.toFloat(),
                        profileImageAnimationTime,
                        true
                    )

                }

                (verticalOffset == 0) -> {    //full expanding
                    setProfileImageAnimation(1f, 1f, 0f, 0f, profileImageAnimationTime, false)
                }

                ((totalScrollRange / 3) <= verticalOffset * -1) -> {  //try to expanding
                    setProfileImageAnimation(.75f, .75f, 0f, 0f, profileImageAnimationTime, false)
                }
            }

        }
    }


    private fun getProfileFailureObserve() {
        profileViewModel.failure.observe(this) {

            val isConnected = isInternetAvailable(applicationContext)
            if (isConnected) {
                profileDataLayout.visibility = ViewGroup.VISIBLE
                profileNoConnection.visibility = ViewGroup.GONE
                Toast.makeText(this, R.string.request_failure, Toast.LENGTH_SHORT).show()
                getProfile()
            } else {
                noConnectionAnimation()
            }


        }
    }

    private fun noConnectionAnimation() {
        profileAnimation.setAnimation(R.raw.no_connection)
        profileNoConnection.visibility = ViewGroup.VISIBLE
        profileDataLayout.visibility = ViewGroup.GONE
        profileAnimation.playAnimation()
        profileAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                getProfile()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
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


    private fun getProfileSuccessObserve() {

        profileViewModel.profileResponse.observe(this) {
            profileDataLayout.visibility = ViewGroup.VISIBLE
            profileNoConnection.visibility = ViewGroup.GONE

            it?.let {
                localData = profileRepository.getLocalData()
                remoteData = it
                showData()
                profileRepository.setUserName(remoteData.username)
            }

        }
    }

    private fun showData() {
        userName.text = remoteData.username
        userNameEditText.setText(remoteData.username)
        emailEditText.setText(remoteData.email)
        phoneEditText.setText(localData.phone)
        addressEditText.setText(localData.address)

        localData.profileImg?.let {
            profileImg.setImageBitmap(localData.profileImg)
            toolbarProfileImg.setImageBitmap(localData.profileImg)
        }
    }


    private fun getTranslationDistance() {
        val profileImgLocation = IntArray(2)
        profileImg.getLocationOnScreen(profileImgLocation)

        val toolbarProfileImgLocation = IntArray(2)
        toolbarProfileImg.getLocationOnScreen(toolbarProfileImgLocation)
        val density = resources.displayMetrics.density
        val profileImgWidth=resources.getDimension(R.dimen.profile_img_width).toInt()/density
        val accountImgProfileWidth=resources.getDimension(R.dimen.account_img_profile_Width_size).toInt()/density
        val imageWidthDifference=(profileImgWidth-accountImgProfileWidth).toInt()
        animationTranslationX =
            toolbarProfileImgLocation[0] - profileImgLocation[0] - imageWidthDifference  // Horizontal distance
        animationTranslationY =
            toolbarProfileImgLocation[1] - profileImgLocation[1] - imageWidthDifference // Vertical distance
    }

    private fun setProfileImageAnimation(
        scaleX: Float,
        scaleY: Float,
        translationX: Float,
        translationZ: Float,
        duration: Long,
        showAccountDetails: Boolean
    ) {
        profileImg.animate()
            .scaleX(scaleX)
            .scaleY(scaleY)
            .translationX(translationX)
            .translationY(translationZ)
            .setDuration(duration) // Animation duration in milliseconds
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                    if (!showAccountDetails) {
                        accountDetails.visibility = ViewGroup.INVISIBLE
                    }
                }

                override fun onAnimationEnd(p0: Animator) {
                    if (showAccountDetails) {
                        profileImg.visibility = ViewGroup.INVISIBLE
                        accountDetails.visibility = ViewGroup.VISIBLE
                    } else {
                        profileImg.visibility = ViewGroup.VISIBLE
                        accountDetails.visibility = ViewGroup.INVISIBLE
                    }
                }

                override fun onAnimationCancel(p0: Animator) {}

                override fun onAnimationRepeat(p0: Animator) {}
            })
            .start()

    }


    private fun progressBarStatus(status: Boolean) {
        if (status) {
            progressBar.visibility = View.VISIBLE
            editButton.isEnabled = false
        } else {
            progressBar.visibility = View.INVISIBLE
            editButton.isEnabled = true
        }
    }

    private fun editButtonClicked() {
        editButton.setOnClickListener {
            val intent = Intent(this, UpdateProfile::class.java)
            startActivity(intent)
        }
    }
   private fun goBackButtonOnclickListener(){
       goBack.setOnClickListener {
           val intent = Intent(this, MyRequests::class.java)
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           startActivity(intent)
           finish()
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


}