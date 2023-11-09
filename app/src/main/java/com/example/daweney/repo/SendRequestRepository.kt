package com.example.daweney.repo

import android.content.Context
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.send_request.SendRequestBody
import com.example.daweney.pojo.send_request.SendRequestResponse
import retrofit2.Call

class SendRequestRepository(context: Context) {
    private val sharedPrefRepo=SharedPrefRepo(context)
    fun sendRequest(sendRequestBody: SendRequestBody):Call<SendRequestResponse>{
        return UserClient.create().sendRequest(sendRequestBody)
    }

    fun deleteCustomerId(){
        sharedPrefRepo.deleteData(SharedPrefRepo.PROFILE,SharedPrefRepo.CUSTOMER_ID)
    }

    fun getCustomerId():String {
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE, SharedPrefRepo.CUSTOMER_ID, SharedPrefRepo.EMPTY).toString()
    }

}