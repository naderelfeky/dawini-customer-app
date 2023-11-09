package com.example.daweney.ui.myrequests

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.myrequests.RequestBody
import com.example.daweney.pojo.myrequests.RequestResponse
import com.example.daweney.repo.RequestRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyRequestsViewModel(val context: Context): ViewModel() {
    private val requestRepository = RequestRepository(context)
    private val _progressBar = MutableLiveData<Boolean>()
    private val _emptyRequest = MutableLiveData<Boolean>()
    private val _failure = MutableLiveData<Boolean>()
    private val _myRequestsResponse = MutableLiveData<RequestResponse>()
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    val myRequestsResponse: LiveData<RequestResponse>
        get() = _myRequestsResponse
    val emptyRequest: LiveData<Boolean>
        get() = _emptyRequest

    val failure: LiveData<Boolean>
        get() = _failure

    fun getMyRequests(requestBody: RequestBody) {

        _progressBar.postValue(true)
        requestRepository.getMyRequests(requestBody).enqueue(object : Callback<RequestResponse> {
            override fun onResponse(
                call: Call<RequestResponse>,
                response: Response<RequestResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        _myRequestsResponse.postValue(response.body())
                    }
                    else -> {
                        _emptyRequest.postValue(true)
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<RequestResponse>, t: Throwable) {
                _failure.postValue(true)
                _progressBar.postValue(false)
            }
        })

    }
}