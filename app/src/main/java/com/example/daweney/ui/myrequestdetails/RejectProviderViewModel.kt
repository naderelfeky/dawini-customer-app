package com.example.daweney.ui.myrequestdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.my_request_details.RejectAppBody
import com.example.daweney.pojo.my_request_details.RejectAppResonse
import com.example.daweney.repo.MyRequestProviderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RejectProviderViewModel:ViewModel() {
    private val  myRequestProviderRepository= MyRequestProviderRepository()
    private val _provideReject= MutableLiveData<Boolean>()
    private val _error= MutableLiveData<Boolean>()
    private val _failure= MutableLiveData<Boolean>()
    private val _progressBar= MutableLiveData<Boolean>()
    val providerReject: LiveData<Boolean>
        get() = _provideReject
    val error: LiveData<Boolean>
        get() = _error
    val failure: LiveData<Boolean>
        get() = _failure
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    fun rejectApp(rejectAppBody: RejectAppBody){
        _progressBar.postValue(true)
        myRequestProviderRepository.rejectProvider(rejectAppBody).enqueue(object :Callback<RejectAppResonse>{
            override fun onResponse(
                call: Call<RejectAppResonse>,
                response: Response<RejectAppResonse>
            ) {
                when(response.code()){
                    200->{
                        _provideReject.postValue(true)
                    }

                    else->{
                        _provideReject.postValue(true)
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<RejectAppResonse>, t: Throwable) {
                _failure.postValue(true)
                _progressBar.postValue(false)
            }
        })
    }
}