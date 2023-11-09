package com.example.daweney.repo

import com.example.daweney.data.UserClient
import com.example.daweney.pojo.my_request_details.AcceptAppBody
import com.example.daweney.pojo.my_request_details.AcceptAppResponse
import com.example.daweney.pojo.my_request_details.RejectAppBody
import com.example.daweney.pojo.my_request_details.RejectAppResonse
import retrofit2.Call

class MyRequestProviderRepository {
    fun acceptRequest(acceptAppBody: AcceptAppBody):Call<AcceptAppResponse>{
        return UserClient.create().acceptApp(acceptAppBody)
    }
    fun rejectProvider(rejectAppBody: RejectAppBody):Call<RejectAppResonse>{
        return UserClient.create().rejectApp(rejectAppBody)
    }
}