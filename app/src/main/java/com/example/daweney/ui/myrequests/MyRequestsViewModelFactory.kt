package com.example.daweney.ui.myrequests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyRequestsViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRequestsViewModel::class.java)) {
            return MyRequestsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}