package com.example.daweney.repo

import com.example.daweney.data.UserClient
import com.example.daweney.pojo.services.ServicesBody
import com.example.daweney.pojo.services.ServicesResponse
import retrofit2.Call

class ServicesRepository {
    fun getServices(servicesBody: ServicesBody):Call<ServicesResponse>{
        return UserClient.create().getServices(servicesBody)
    }
}