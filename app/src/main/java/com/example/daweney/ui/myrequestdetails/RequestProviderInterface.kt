package com.example.daweney.ui.myrequestdetails

import com.example.daweney.pojo.my_request_details.RequestApplicationResponseItem

interface RequestProviderInterface {

    fun onAcceptApplication(provider:RequestApplicationResponseItem)

    fun onRejectApplication(provider:RequestApplicationResponseItem)

}