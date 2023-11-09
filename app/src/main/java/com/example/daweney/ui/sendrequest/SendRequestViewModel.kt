package com.example.daweney.ui.sendrequest


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.send_request.SendRequestBody
import com.example.daweney.pojo.send_request.SendRequestResponse
import com.example.daweney.repo.SendRequestRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendRequestViewModel(context: Context) : ViewModel() {
    private val sendRequestResponse = SendRequestRepository(context)
    private val _sendRequestSuccess = MutableLiveData<Boolean>()
    private val _sendRequestInvalidError = MutableLiveData<Boolean>()
    private val _sendRequestNoProviderError = MutableLiveData<Boolean>()
    private val _sendRequestFailure = MutableLiveData<Boolean>()
    private val _progressBar = MutableLiveData<Boolean>()
    val sendRequestSuccess: LiveData<Boolean>
        get() = _sendRequestSuccess
    val sendRequestInvalidError: LiveData<Boolean>
        get() = _sendRequestInvalidError
    val sendRequestNoProvider: LiveData<Boolean>
        get() = _sendRequestNoProviderError
    val sendRequestFailure: LiveData<Boolean>
        get() = _sendRequestFailure
    val progressBar: LiveData<Boolean>
        get() = _progressBar


    fun sendRequest(sendRequestBody: SendRequestBody) {
        _progressBar.postValue(true)
        sendRequestResponse.sendRequest(sendRequestBody)
            .enqueue(object : Callback<SendRequestResponse> {
                override fun onResponse(
                    call: Call<SendRequestResponse>,
                    response: Response<SendRequestResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            _sendRequestSuccess.postValue(true)
                        }
                        405 -> {
                            _sendRequestNoProviderError.postValue(true)
                        }
                        else -> {
                            _sendRequestInvalidError.postValue(true)
                        }
                    }
                    _progressBar.postValue(false)
                }

                override fun onFailure(call: Call<SendRequestResponse>, t: Throwable) {
                    _sendRequestFailure.postValue(true)
                    _progressBar.postValue(false)
                }
            })
    }
}