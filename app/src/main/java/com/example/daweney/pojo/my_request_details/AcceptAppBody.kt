package com.example.daweney.pojo.my_request_details

data class AcceptAppBody(
    val AppId: String,
    val customerId: String,
    val providerId: String,
    val reqId: String,
    val serviceIds: List<String>
)