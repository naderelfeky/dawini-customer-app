package com.example.daweney.ui.verifiedaccount

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VerifyUserViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyUserViewModel::class.java)) {
            return VerifyUserViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}