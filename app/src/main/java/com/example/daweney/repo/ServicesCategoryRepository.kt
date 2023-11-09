package com.example.daweney.repo

import com.example.daweney.data.UserClient
import com.example.daweney.pojo.services_category.ServicesCategoryResponse
import retrofit2.Call

class ServicesCategoryRepository {
    fun getServicesCategory(): Call<ServicesCategoryResponse>{
        return UserClient.create().getServicesCategory()
    }
}