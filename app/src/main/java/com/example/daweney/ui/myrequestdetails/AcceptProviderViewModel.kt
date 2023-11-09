package com.example.daweney.ui.myrequestdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.my_request_details.AcceptAppBody
import com.example.daweney.pojo.my_request_details.AcceptAppResponse
import com.example.daweney.repo.MyRequestProviderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceptProviderViewModel:ViewModel() {
    private val  myRequestProviderRepository=MyRequestProviderRepository()
    private val _providerAccept= MutableLiveData<Boolean>()
    private val _error= MutableLiveData<Boolean>()
    private val _failure= MutableLiveData<Boolean>()
    private val _progressBar= MutableLiveData<Boolean>()
    val providerAccept: LiveData<Boolean>
        get() = _providerAccept
    val error: LiveData<Boolean>
        get() = _error
    val failure: LiveData<Boolean>
        get() = _failure
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    fun acceptApp(acceptAppBody: AcceptAppBody){
        _progressBar.postValue(true)
        myRequestProviderRepository.acceptRequest(acceptAppBody).enqueue(object:Callback<AcceptAppResponse>{
            override fun onResponse(
                call: Call<AcceptAppResponse>,
                response: Response<AcceptAppResponse>
            ) {
                when(response.code()){
                    200->{
                        _providerAccept.postValue(true)
                    }
                    else->{
                        _error.postValue(true)
                    }
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<AcceptAppResponse>, t: Throwable) {
                _progressBar.postValue(false)
                _failure.postValue(true)
            }
        })
    }
}