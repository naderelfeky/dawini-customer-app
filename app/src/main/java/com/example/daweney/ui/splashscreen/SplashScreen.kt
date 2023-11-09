package com.example.daweney.ui.splashscreen

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.repo.UserRepository
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.myrequests.MyRequests
import kotlinx.android.synthetic.main.activity_splash_screen.*



class SplashScreen : LocalizationActivity() {

    private val userRepository=UserRepository(this)
    private val SPLASH_DISPLAY_LENGTH = 3000
    private val DARK="dark"
    private val LIGHT="light"
    private val ARABIC="arabic"
    private val ARABIC_CODE="ar"
    private val ENGLISH="english"
    private val ENGLISH_CODE="en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        //to set animation
        logo_animation.setAnimation(R.raw.daweney_logo)
        loadTheme()
        loadLanguage()
        hideStatusBar()

        Handler().postDelayed({
           userSaveLogin()
        },SPLASH_DISPLAY_LENGTH.toLong())
        setLightStatusBar()
    }

    private fun loadLanguage() {

        when(userRepository.getLanguage())
       {
           ARABIC->{
               setLanguage(ARABIC_CODE)
           }
           ENGLISH->{
               setLanguage(ENGLISH_CODE)
           }
       }
    }

    private fun loadTheme() {
        val theme = userRepository.getTheme()

        if (theme == DARK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


    private fun userSaveLogin() {

        if (userRepository.getCustomerId().isNotEmpty()) {
            startMainActivity()
        }else{
            startLoginActivity()
        }
    }
    private fun startMainActivity() {
        val intent = Intent(this, MyRequests::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun startLoginActivity() {
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
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