package com.example.daweney.ui.updateprofile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.pojo.update_profile.UpdateProfileBody
import com.example.daweney.pojo.update_profile.UpdateProfileResponse
import com.example.daweney.repo.ProfileRepository
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class UpdateProfileViewModel(context: Context) :ViewModel(){
    private val profileRepository = ProfileRepository(context)
    private val _progressBar = MutableLiveData<Boolean>()
    private val _failure = MutableLiveData<Boolean>()
    private val _updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    val progressBar: LiveData<Boolean>
        get() = _progressBar
    val updateProfileResponse: LiveData<UpdateProfileResponse>
        get() = _updateProfileResponse
    val failure: LiveData<Boolean>
        get() = _failure
    fun updateProfile(updateProfileBody: UpdateProfileBody){
        _progressBar.postValue(true)
        profileRepository.updateRemoteProfile(updateProfileBody).enqueue(object :
            retrofit2.Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                _progressBar.postValue(false)
                if(response.isSuccessful)
                _updateProfileResponse.postValue(response.body())
                else _failure.postValue(true)
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                _progressBar.postValue(false)
                _failure.postValue(true)
            }
        })
    }
}