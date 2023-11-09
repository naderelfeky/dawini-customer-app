package com.example.daweney.ui.sendrequest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SendRequestViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendRequestViewModel::class.java)) {
            return SendRequestViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}