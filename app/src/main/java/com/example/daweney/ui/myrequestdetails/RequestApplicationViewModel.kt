package com.example.daweney.ui.myrequestdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.my_request_details.RequestApplicationBody
import com.example.daweney.pojo.my_request_details.RequestApplicationResponse
import com.example.daweney.pojo.my_request_details.RequestApplicationResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestApplicationViewModel:ViewModel() {
private val _getRequestApplication=MutableLiveData<RequestApplicationResponse>()
private val _error= MutableLiveData<Boolean>()
private val _noProvider= MutableLiveData<Boolean>()
private val _failure= MutableLiveData<Boolean>()
private val _progressBar= MutableLiveData<Boolean>()

    val requestApplication: LiveData<RequestApplicationResponse>
        get() = _getRequestApplication
    val error: LiveData<Boolean>
        get() = _error
    val failure: LiveData<Boolean>
        get() = _failure
    val noProvider: LiveData<Boolean>
        get() = _noProvider
    val progressBar: LiveData<Boolean>
        get() = _progressBar

   fun getRequestApplication(requestApplicationBody: RequestApplicationBody){
       _progressBar.postValue(true)
       UserClient.create().getRequestApplication(requestApplicationBody).enqueue(object :Callback<RequestApplicationResponse>{
           override fun onResponse(
               call: Call<RequestApplicationResponse>,
               response: Response<RequestApplicationResponse>
           ) {
            when(response.code()){
                200->{
                    _getRequestApplication.postValue(response.body())
                }
                404->{
                    _noProvider.postValue(true)
                }
                else->{
                    _error.postValue(true)
                }
            }
               _progressBar.postValue(false)
           }

           override fun onFailure(call: Call<RequestApplicationResponse>, t: Throwable) {
             _progressBar.postValue(false)
             _failure.postValue(true)
           }
       })
   }
}