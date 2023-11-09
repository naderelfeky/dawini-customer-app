package com.example.daweney.ui.myrequestdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.my_request_details.CancelRequestBody
import com.example.daweney.pojo.my_request_details.CancelRequestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelRequestViewModel:ViewModel() {
    private val _requestCancel= MutableLiveData<Boolean>()
    private val _error= MutableLiveData<Boolean>()
    private val _failure= MutableLiveData<Boolean>()
    private val _progressBar= MutableLiveData<Boolean>()
    val requestCancel:LiveData<Boolean>
    get() = _requestCancel
    val error:LiveData<Boolean>
        get() = _error
    val failure:LiveData<Boolean>
        get() = _failure
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    fun cancelRequest(cancelRequestBody: CancelRequestBody){
        _progressBar.postValue(true)
        UserClient.create().cancelRequest(cancelRequestBody).enqueue(object :Callback<CancelRequestResponse>{
            override fun onResponse(
                call: Call<CancelRequestResponse>,
                response: Response<CancelRequestResponse>
            ) {
                when(response.code()){
                    200->{
                        _requestCancel.postValue(true)
                    }
                    else->{
                        _error.postValue(true)
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<CancelRequestResponse>, t: Throwable) {
               _progressBar.postValue(false)
                _failure.postValue(true)
            }
        })
    }
}