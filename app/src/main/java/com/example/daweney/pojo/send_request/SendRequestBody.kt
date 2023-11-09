package com.example.daweney.pojo.send_request

data class SendRequestBody(
    val address: String,
    val customerId: String,
    val gender: String,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val number: String,
    val serviceIds: List<String>,
    val timeOfService: String,
    val typeofservice: String
)