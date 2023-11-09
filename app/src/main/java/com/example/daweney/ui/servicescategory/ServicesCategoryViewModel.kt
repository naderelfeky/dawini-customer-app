package com.example.daweney.ui.servicescategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.services_category.ServicesCategoryResponse
import com.example.daweney.repo.ServicesCategoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicesCategoryViewModel() : ViewModel() {
    private val servicesCategoryResponse = ServicesCategoryRepository()
    private val _errorMessage = MutableLiveData<Boolean>()
    private val _servicesCategory = MutableLiveData<ServicesCategoryResponse>()
    private val _progressBar = MutableLiveData<Boolean>()
    private val _failWithConnection = MutableLiveData<Boolean>()
    val errorMessage: LiveData<Boolean>
        get() = _errorMessage
    val servicesCategory: LiveData<ServicesCategoryResponse>
        get() = _servicesCategory
    val progressBar
        get() = _progressBar
    val failWithConnection: LiveData<Boolean>
        get() = _failWithConnection

    fun getServicesCategory() {
        _progressBar.postValue(true)
        servicesCategoryResponse.getServicesCategory()
            .enqueue(object : Callback<ServicesCategoryResponse> {

                override fun onResponse(
                    call: Call<ServicesCategoryResponse>,
                    response: Response<ServicesCategoryResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            _servicesCategory.postValue(response.body())

                        }
                        else -> {
                            _errorMessage.postValue(true)
                        }
                    }
                    _progressBar.postValue(false)
                    _failWithConnection.postValue(false)
                }

                override fun onFailure(call: Call<ServicesCategoryResponse>, t: Throwable) {
                    _failWithConnection.postValue(true)
                    _progressBar.postValue(false)

                }
            })
    }
}