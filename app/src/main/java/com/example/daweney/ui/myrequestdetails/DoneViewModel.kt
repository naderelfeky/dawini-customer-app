package com.example.daweney.ui.myrequestdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.my_request_details.DoneBody
import com.example.daweney.pojo.my_request_details.DoneResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoneViewModel:ViewModel() {
    private val _requestDone= MutableLiveData<Boolean>()
    private val _error= MutableLiveData<Boolean>()
    private val _failure= MutableLiveData<Boolean>()
    private val _progressBar= MutableLiveData<Boolean>()
    val requestDone: LiveData<Boolean>
        get() = _requestDone
    val error: LiveData<Boolean>
        get() = _error
    val failure: LiveData<Boolean>
        get() = _failure
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    fun done(doneBody: DoneBody){
        _progressBar.postValue(true)
        UserClient.create().requestDone(doneBody).enqueue(object : Callback<DoneResponse> {
            override fun onResponse(call: Call<DoneResponse>, response: Response<DoneResponse>) {
                when(response.code()){
                    200->{
                        _requestDone.postValue(true)
                    }
                    else->{
                        _error.postValue(true)
                    }
                }

                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<DoneResponse>, t: Throwable) {
                _progressBar.postValue(false)
                _failure.postValue(true)
            }
        })
    }
}