package com.example.daweney.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.R
import com.example.daweney.pojo.register.RegisterResponse
import com.example.daweney.pojo.register.RegisterUser
import com.example.daweney.repo.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(val context: Context) : ViewModel() {

    private val _registerMutableLiveData = MutableLiveData<RegisterResponse>()
    private val _progressBar = MutableLiveData<Boolean>()
    private val _dialogMessage = MutableLiveData("")
    private val userRepository = UserRepository(context)

    // live data which use to
    val registerMutableLiveData: LiveData<RegisterResponse>
        get() = _registerMutableLiveData
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    val dialogMessage: LiveData<String>
        get() = _dialogMessage


    fun register(user: RegisterUser) {
        _progressBar.postValue(true)
        userRepository.register(user).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                when (response.code()) {
                    201 -> {
                        _registerMutableLiveData.value = response.body()
                    }
                    400 -> {
                        _dialogMessage.postValue(context.getText(R.string.email_are_Wrong).toString())
                    }
                    409 -> {
                        _dialogMessage.postValue(context.getText(R.string.user_already_existy).toString())
                    }

                    else -> {
                        _dialogMessage.postValue(context.getText(R.string.have_error_try_again).toString())
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _dialogMessage.postValue( context.getText(R.string.opps_try_again).toString())
                _progressBar.postValue(false)

            }
        })
    }
}