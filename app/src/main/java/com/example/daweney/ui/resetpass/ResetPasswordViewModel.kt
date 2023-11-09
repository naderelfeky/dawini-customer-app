package com.example.daweney.ui.resetpass

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.R
import com.example.daweney.pojo.resetpass.MsgResponse
import com.example.daweney.pojo.resetpass.ResetPassword
import com.example.daweney.repo.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordViewModel(val context: Context): ViewModel() {
    private val _resetPasswordMutableLiveData= MutableLiveData<MsgResponse>()
    private val _progressBar= MutableLiveData<Boolean>()
    private val _dialogMessage= MutableLiveData("")
    private val  userRepository= UserRepository(context)
    // livedata which use to observe change
    val loginMutableLiveData: LiveData<MsgResponse>
        get() =_resetPasswordMutableLiveData
    val progressBar: LiveData<Boolean>
        get()=_progressBar
    val dialogMessage: LiveData<String>
        get() = _dialogMessage


    fun resetPassword(resetPassword: ResetPassword){
        _progressBar.postValue(true)
        userRepository.resetPassword(resetPassword).enqueue(object : Callback<MsgResponse>{
            override fun onResponse(call: Call<MsgResponse>, response: Response<MsgResponse>) {
                when(response.code()){
                    200->{
                        _resetPasswordMutableLiveData.postValue(response.body())
                    }
                    400->{
                        _dialogMessage.postValue(context.getText(R.string.pass_length_error).toString())
                    }
                    else->{
                        _dialogMessage.postValue(context.getText(R.string.error_try_again).toString())
                    }

                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<MsgResponse>, t: Throwable) {
                _dialogMessage.postValue(context.getText(R.string.error_try_again).toString())
                _progressBar.postValue(false)
            }
        })
    }

}