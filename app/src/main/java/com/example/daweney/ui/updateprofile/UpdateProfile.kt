package com.example.daweney.ui.updateprofile

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.profile.ProfileBody
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.pojo.update_profile.LocalData
import com.example.daweney.pojo.update_profile.UpdateProfileBody
import com.example.daweney.repo.ProfileRepository
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.profile.Profile
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_update_profile.goBack
import kotlinx.android.synthetic.main.activity_update_profile.progressBar
import kotlinx.android.synthetic.main.activity_update_profile.updateProfileAnimation
import kotlinx.android.synthetic.main.activity_update_profile.updateProfileDataLayout
import kotlinx.android.synthetic.main.activity_update_profile.updateProfileImgButton
import kotlinx.android.synthetic.main.activity_update_profile.updateProfileNoConnection

class UpdateProfile : LocalizationActivity() {
    private lateinit var getProfileViewModel: GetProfileViewModel
    private lateinit var updateProfileViewModel: UpdateProfileViewModel
    private val profileRepository = ProfileRepository(this)
    private val imagePickerRequestCode: Int = 123
    private val imageFileName = "profileImg.jpg"
    private lateinit var localData: LocalData
    private lateinit var remoteData: ProfileResponse
    private lateinit var profileImg: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        viewModelInit()
        initialization()
        checkCustomerIdExisting()
        updateProfileSuccessObserver()
        updateProfileFailureObserver()
        getProfileSuccessObserve()
        getProfileFailureObserve()
        progressBarObserve()
        updateProfileImgButton.setOnClickListener { openImagePicker() }
        goBack.setOnClickListener { goBack() }
        updateButtonOnClickListener()
        usernameTextWatcher()
        emailTextWatcher()
        addressTextWatcher()
        phoneTextWatcher()
        setLightStatusBar()
    }

    private fun viewModelInit() {
        val getProfileViewModelFactory = GetProfileViewModelFactory(this)
        getProfileViewModel =
            ViewModelProvider(this, getProfileViewModelFactory)[GetProfileViewModel::class.java]

        val updateProfileViewModelFactory = UpdateProfileViewModelFactory(this)
        updateProfileViewModel =
            ViewModelProvider(
                this,
                updateProfileViewModelFactory
            )[UpdateProfileViewModel::class.java]

    }

    private fun initialization() {
        userNameEditText = findViewById(R.id.nameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        updateButton = findViewById(R.id.updateButton)
        profileImg = findViewById<ImageView>(R.id.profileImg)
    }

    private fun checkCustomerIdExisting() {
        profileRepository.getCustomerId().let {
            if (it.isEmpty()) logout()
            else getProfileViewModel.getProfile(ProfileBody(it))
        }
    }

    private fun updateProfileSuccessObserver() {
        updateProfileViewModel.updateProfileResponse.observe(this) {
            val modifiedLocalData = LocalData(
                userNameEditText.text.toString(),
                phoneEditText.text.toString(),
                addressEditText.text.toString(),
                profileImg.drawable.toBitmap()
            )
            profileRepository.setLocalData(modifiedLocalData, imageFileName)
            Toast.makeText(this, getText(R.string.done), Toast.LENGTH_SHORT).show()
            goBack()
        }
    }

    private fun updateProfileFailureObserver() {
        updateProfileViewModel.failure.observe(this) {
            Toast.makeText(this, R.string.update_failure, Toast.LENGTH_SHORT).show()
        }
    }


    private fun getProfileFailureObserve() {
        getProfileViewModel.failure.observe(this) {

            val isConnected = isInternetAvailable(applicationContext)
            if (isConnected) {
                updateProfileDataLayout.visibility = ViewGroup.VISIBLE
                updateProfileNoConnection.visibility = ViewGroup.GONE
                Toast.makeText(this, R.string.request_failure, Toast.LENGTH_SHORT).show()
                getProfileViewModel.getProfile(ProfileBody(profileRepository.getCustomerId()))
            } else {
                noConnectionAnimation()
            }


        }
    }

    private fun getProfileSuccessObserve() {
        getProfileViewModel.profileResponse.observe(this) {

            updateProfileDataLayout.visibility = ViewGroup.VISIBLE
            updateProfileNoConnection.visibility = ViewGroup.GONE

            it?.let {
                localData = profileRepository.getLocalData()
                remoteData = it
                showData()
            }

        }
    }

    private fun progressBarObserve() {
        getProfileViewModel.progressBar.observe(this) {
            progressBarStatus(it!!)
        }
        updateProfileViewModel.progressBar.observe(this) {
            progressBarStatus(it!!)
        }
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .maxResultSize(
                1080,
                1080
            )
            .start(imagePickerRequestCode)
    }

    private fun goBack() {
        val intent = Intent(this, Profile::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateButtonOnClickListener() {
        updateButton.setOnClickListener {
            val profileRemoteData = UpdateProfileBody(
                emailEditText.text.toString(),
                profileRepository.getCustomerId(),
                userNameEditText.text.toString()
            )
            updateProfileViewModel.updateProfile(profileRemoteData)

        }
    }

    private fun usernameTextWatcher() {
        userNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userNameEditText.text.toString().let {
                    if (!it.isNullOrEmpty()) {
                        if (isValidName(it)) {
                            userNameEditText.error = null
                            if (!updateButton.isEnabled) updateButton.isEnabled =
                                it != remoteData.username
                        } else {
                            userNameEditText.error = getString(R.string.name_not_valid)
                        }
                    }

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    private fun emailTextWatcher() {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                emailEditText.text.toString().let {
                    if (!updateButton.isEnabled) updateButton.isEnabled = it != remoteData.email
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun addressTextWatcher() {
        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addressEditText.text.toString().let {
                    if (!updateButton.isEnabled) updateButton.isEnabled = it != localData.address
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun phoneTextWatcher() {
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phoneEditText.text.toString().let {
                    if ((it.length == 11 && it[0] == '0') || (it.length == 10 && it[0] != '0')) {
                        phoneEditText.error = null
                        if (!updateButton.isEnabled) updateButton.isEnabled = it != localData.phone
                    } else {
                        phoneEditText.error = getString(R.string.phone_not_valid)
                    }

                }


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun showData() {
        userNameEditText.setText(remoteData.username)
        emailEditText.setText(remoteData.email)
        phoneEditText.setText(localData.phone)
        addressEditText.setText(localData.address)
        localData.profileImg?.let {
            profileImg.setImageBitmap(localData.profileImg)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (imagePickerRequestCode == requestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val imageUri: Uri = data?.data!!
                    val inputStream = applicationContext.contentResolver.openInputStream(imageUri)
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    profileImg.setImageBitmap(imageBitmap)
                    updateButton.isEnabled = true

                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, R.string.task_cancelled, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun progressBarStatus(status: Boolean) {
        if (status) {
            progressBar.visibility = View.VISIBLE
            updateProfileImgButton.isEnabled = false
        } else {
            progressBar.visibility = View.INVISIBLE
            updateProfileImgButton.isEnabled = true
        }
    }

    private fun noConnectionAnimation() {
        updateProfileAnimation.setAnimation(R.raw.no_connection)
        updateProfileNoConnection.visibility = ViewGroup.VISIBLE
        updateProfileDataLayout.visibility = ViewGroup.GONE
        updateProfileAnimation.playAnimation()
        updateProfileAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                getProfileViewModel.getProfile(ProfileBody(profileRepository.getCustomerId()))
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun isValidName(name: String): Boolean {
        val regex =
            Regex("^[\u0621-\u064Aa-zA-Z]+(([',. -][\u0621-\u064Aa-zA-Z ])?[\u0621-\u064Aa-zA-Z]*)*[\u0621-\u064Aa-zA-Z ]?\$")
        return regex.matches(name)
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

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        profileRepository.deleteCustomerId()
        startActivity(intent)
    }


}

