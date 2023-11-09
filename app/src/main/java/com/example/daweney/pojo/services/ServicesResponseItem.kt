package com.example.daweney.pojo.services

import java.io.Serializable

data class ServicesResponseItem(
    val ArabicName: String,
    val EnglishName: String,
    val __v: Int,
    val _id: String,
    val price: Int
): Serializable {

}