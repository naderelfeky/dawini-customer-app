package com.example.daweney.ui.register

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.register.RegisterUser
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.verifiedaccount.VerifiedActivity
import kotlinx.android.synthetic.main.activity_register.*

class Register : LocalizationActivity() , TextWatcher {
    private lateinit var register: RegisterViewModel
    private lateinit var sendCode: SendCodeViewModel
    private lateinit var userName:String
    private lateinit var email:String
    private lateinit var password:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //watcher
        userNameEditText.addTextChangedListener(this)
        emailEditText.addTextChangedListener(this)
        passwordEditText.addTextChangedListener(this)

        viewModelInit()
        signUpButton.setOnClickListener { click(it) }
        login.setOnClickListener{click(it) }
        progressBarObserve()
        dialogMessageObserve()
        registerSuccessful()
        setLightStatusBar()
    }

    private fun viewModelInit() {
        val registerViewModelFactory=RegisterViewModelFactory(this)
        register= ViewModelProvider(this,registerViewModelFactory)[RegisterViewModel::class.java]
        val sendCodeViewModelFactory=SendCodeViewModelFactory(this)
        sendCode= ViewModelProvider(this,sendCodeViewModelFactory)[SendCodeViewModel::class.java]
    }


    private fun click(view: View?) {
        when(view?.id){
            R.id.signUpButton->{
                //get text and assign in variable
                userName=userNameEditText.text?.trim().toString()
                email=emailEditText.text?.trim().toString()
                password=passwordEditText.text?.trim().toString()

                register.register(RegisterUser(userName,email,password))

                progressBar.visibility= View.VISIBLE
                userNameEditText.isEnabled=false
                emailEditText.isEnabled=false
                passwordEditText.isEnabled=false

            }
           R.id.login->{
               startLoginActivity()
           }
        }


    }


    private fun dialogMessageObserve(){
        register.dialogMessage.observe(this) {
            if (it.toString().isNotEmpty()){
                val dialogFragment = CustomDialogFragment(it,applicationContext)
                dialogFragment.show(supportFragmentManager, "register_error_dialog")
            }
        }

    }
    private fun progressBarObserve(){
        register.progressBar.observe(this){
            if(it){
                progressBar.visibility= View.VISIBLE
                signUpButton.isEnabled=false
            }else{
                progressBar.visibility= View.INVISIBLE
                userNameEditText.isEnabled=true
                emailEditText.isEnabled=true
                passwordEditText.isEnabled=true
                signUpButton.isEnabled=true
            }
        }
    }


    private fun startLoginActivity(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerSuccessful() {

        register.registerMutableLiveData.observe(this){
            sendCode.sendCode(email)
            val intent = Intent(this, VerifiedActivity::class.java)
            intent.putExtra("callingActivity", Register::class.java.toString())
            intent.putExtra("email",email)
            intent.putExtra("pass",password)
            startActivity(intent)
            finish()
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        signUpButton.isEnabled = (userNameEditText.text.toString().trim().isNotEmpty() &&
                                    emailEditText.text.toString().trim().isNotEmpty()  &&
                                    passwordEditText.text.toString().trim().isNotEmpty())

    }

    override fun afterTextChanged(s: Editable?) {

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