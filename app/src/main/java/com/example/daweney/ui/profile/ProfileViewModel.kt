package com.example.daweney.ui.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.profile.ProfileBody
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.repo.ProfileRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(context: Context):ViewModel() {
    private val profileRepository = ProfileRepository(context)
    private val _progressBar = MutableLiveData<Boolean>()
    private val _failure = MutableLiveData<Boolean>()
    private val _profileResponse = MutableLiveData<ProfileResponse>()
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    val profileResponse: LiveData<ProfileResponse>
        get() = _profileResponse
    val failure: LiveData<Boolean>
        get() = _failure

    fun getProfile(profileBody: ProfileBody) {
        _progressBar.postValue(true)
        profileRepository.getRemoteProfile(profileBody).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    _profileResponse.postValue(response.body())
                } else {
                    _failure.postValue(true)
                }
                _progressBar.postValue(false)
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                _progressBar.postValue(false)
                _failure.postValue(true)
            }
        })
    }

}