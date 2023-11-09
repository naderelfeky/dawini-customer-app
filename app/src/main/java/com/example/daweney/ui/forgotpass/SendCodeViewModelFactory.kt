package com.example.daweney.ui.forgotpass

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SendCodeViewModelFactory (private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendCodeViewModel::class.java)) {
            return SendCodeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}