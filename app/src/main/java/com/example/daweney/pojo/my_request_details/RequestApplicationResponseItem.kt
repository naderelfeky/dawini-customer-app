package com.example.daweney.pojo.my_request_details

data class RequestApplicationResponseItem(
    val RequestId: String,
    val __v: Int,
    val _id: String,
    val customerId: String,
    val priceOfService: Int,
    val providerId: String,
    val providerName: String,
    val providerPhoto: String,
    val providerRate: ProviderRate,
    val typeofservice: String
)