package com.example.daweney.pojo.update_profile

import android.graphics.Bitmap

data class LocalData(
    val userName: String,
    val phone: String,
    val address: String,
    val profileImg:Bitmap?
)