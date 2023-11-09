package com.example.daweney.ui.sevices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.services.ServicesBody
import com.example.daweney.pojo.services.ServicesResponse
import com.example.daweney.repo.ServicesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicesViewModel : ViewModel() {
    private val servicesRepository = ServicesRepository()
    private val _services = MutableLiveData<ServicesResponse>()
    private val _errorMessage = MutableLiveData<Boolean>()
    private val _progressBar = MutableLiveData<Boolean>()
    private val _failWithConnection = MutableLiveData<Boolean>()

    val errorMessage: LiveData<Boolean>
        get() = _errorMessage
    val progressBar
        get() = _progressBar
    val services: LiveData<ServicesResponse>
        get() = _services
    val failWithConnection:LiveData<Boolean>
        get() = _failWithConnection

    fun getServices(servicesBody: ServicesBody) {
        _progressBar.postValue(true)
        servicesRepository.getServices(servicesBody).enqueue(object : Callback<ServicesResponse> {
            override fun onResponse(
                call: Call<ServicesResponse>,
                response: Response<ServicesResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        _services.postValue(response.body())
                    }
                    else -> {
                        _errorMessage.postValue(true)
                    }
                }
                _progressBar.postValue(false)
                _failWithConnection.postValue(false)
            }

            override fun onFailure(call: Call<ServicesResponse>, t: Throwable) {
                _failWithConnection.postValue(true)
                _progressBar.postValue(false)
            }
        })
    }


}