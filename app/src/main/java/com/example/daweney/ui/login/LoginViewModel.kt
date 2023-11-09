package com.example.daweney.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.R
import com.example.daweney.pojo.login.LoginResponse
import com.example.daweney.pojo.login.LoginUser
import com.example.daweney.repo.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _loginMutableLiveData = MutableLiveData<LoginResponse>()
    private val _progressBar = MutableLiveData<Boolean>()
    private val _dialogMessage = MutableLiveData("")
    private val _emailIsVerified = MutableLiveData<Boolean>()
    private val userRepository = UserRepository(context)

    // livedata which use to observe change
    val loginMutableLiveData: LiveData<LoginResponse>
        get() = _loginMutableLiveData
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    val dialogMessage: LiveData<String>
        get() = _dialogMessage
    val emailIsVerified: LiveData<Boolean>
        get() = _emailIsVerified

    fun login(user: LoginUser) {
        _progressBar.postValue(true)
        userRepository.login(user).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                when (response.code()) {
                    200 -> {
                        _loginMutableLiveData.postValue(response.body())
                    }
                    400 -> {
                        _dialogMessage.postValue(context.getText(R.string.username_or_pass_wrong).toString())
                    }
                    401 -> {
                        _emailIsVerified.postValue(false)
                    }
                    else -> {
                        _dialogMessage.postValue(context.getText(R.string.have_error_try_again).toString())
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                _dialogMessage.postValue(context.getText(R.string.cant_login_try_again).toString())
                _progressBar.postValue(false)
            }

        })
    }


}