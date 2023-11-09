package com.example.daweney.ui.login

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.R.layout
import com.example.daweney.pojo.login.LoginUser
import com.example.daweney.repo.UserRepository
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.forgotpass.ForgotPassword
import com.example.daweney.ui.myrequests.MyRequests
import com.example.daweney.ui.register.Register
import com.example.daweney.ui.verifiedaccount.VerifiedActivity
import com.google.android.material.elevation.SurfaceColors
import kotlinx.android.synthetic.main.activity_login.emailTextView
import kotlinx.android.synthetic.main.activity_login.forget_pass
import kotlinx.android.synthetic.main.activity_login.loginButton
import kotlinx.android.synthetic.main.activity_login.passwordTextView
import kotlinx.android.synthetic.main.activity_login.progressBar
import kotlinx.android.synthetic.main.activity_login.signup

class Login : LocalizationActivity(), TextWatcher {

    private lateinit var login: LoginViewModel
    private lateinit var sendCode: SendCodeViewModel
    private lateinit var email: String
    private lateinit var password: String
    private val userRepository=UserRepository(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)


        //watcher
        emailTextView.addTextChangedListener(this)
        passwordTextView.addTextChangedListener(this)

        viewModelInit()
        loginButton.setOnClickListener { click(it) }
        signup.setOnClickListener { click(it) }
        forget_pass.setOnClickListener { click(it) }
        getIntentExtrasData()
        loginSuccessful()
        progressBarObserve()
        dialogMessageObserve()
        emailVerifiedObserve()
        setLightStatusBar()
    }

    private fun viewModelInit() {
        val loginViewModelFactory=LoginViewModelFactory(this)
        login = ViewModelProvider(this,loginViewModelFactory)[LoginViewModel::class.java]
        val sendCodeViewModelFactory=SendCodeViewModelFactory(this)
        sendCode=ViewModelProvider(this,sendCodeViewModelFactory)[SendCodeViewModel::class.java]
    }


    private fun getIntentExtrasData() {
        val mail: String = intent.getStringExtra("email").toString()
        val pass: String = intent.getStringExtra("pass").toString()

        if (mail != "null") emailTextView.setText(mail)
        if (pass != "null") passwordTextView.setText(pass)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        loginButton.isEnabled = (emailTextView.text?.trim()?.isNotEmpty() == true
                && passwordTextView.text?.trim()?.isNotEmpty() == true)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun startMainActivity() {
        val intent = Intent(this, MyRequests::class.java)
        startActivity(intent)
        finish()
    }

    private fun startVerifiedCodeActivity() {
        sendCode.sendCode(email)
        val intent = Intent(this, VerifiedActivity::class.java)
        intent.putExtra("callingActivity", Login::class.java.toString())
        intent.putExtra("email",email)
        intent.putExtra("pass",password)
        startActivity(intent)
        finish()
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        finish()
    }

    private fun click(view: View?) {
        when (view?.id) {
            R.id.loginButton -> {

                email = emailTextView.text?.trim().toString()
                password = passwordTextView.text?.trim().toString()

                login.login(LoginUser(email, password))

                progressBar.visibility = View.VISIBLE
                emailTextView.isEnabled = false
                passwordTextView.isEnabled = false

            }
            R.id.signup -> {
                startRegisterActivity()
            }
            R.id.forget_pass -> {
                val intent = Intent(applicationContext, ForgotPassword::class.java)
                if(::email.isInitialized) intent.putExtra("email",email)
                startActivity(intent)
            }
        }


    }

    private fun emailVerifiedObserve() {
        login.emailIsVerified.observe(this) {

            startVerifiedCodeActivity()

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

    private fun dialogMessageObserve() {
        login.dialogMessage.observe(this) {

            if (it.toString().isNotEmpty()) {
                val dialogFragment = CustomDialogFragment(it, applicationContext)
                dialogFragment.show(supportFragmentManager, "login_error_dialog")
            }

        }

    }

    private fun progressBarObserve() {
        login.progressBar.observe(this) {
            if (it) {
                progressBar.visibility = View.VISIBLE
                loginButton.isEnabled = false
            } else {
                progressBar.visibility = View.INVISIBLE
                emailTextView.isEnabled = true
                passwordTextView.isEnabled = true
                loginButton.isEnabled = true
            }
        }
    }

    private fun loginSuccessful() {
        login.loginMutableLiveData.observe(this) {
          userRepository.setCustomerId(it.customerID)
            startMainActivity()
        }

    }
}