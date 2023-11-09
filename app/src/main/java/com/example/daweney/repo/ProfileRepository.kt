package com.example.daweney.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.example.daweney.R
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.profile.ProfileBody
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.pojo.update_profile.LocalData
import com.example.daweney.pojo.update_profile.UpdateProfileBody
import com.example.daweney.pojo.update_profile.UpdateProfileResponse
import retrofit2.Call

class ProfileRepository(val context: Context) {
    private  var sharedPrefRepo: SharedPrefRepo = SharedPrefRepo(context)
    private val imageFileName = "profileImg.jpg"


    fun getRemoteProfile(profileBody: ProfileBody): Call<ProfileResponse> {
        return UserClient.create().getProfileData(profileBody)
    }
    fun updateRemoteProfile(updateProfileBody: UpdateProfileBody): Call<UpdateProfileResponse> {
        return UserClient.create().updateProfileData(updateProfileBody)
    }

    private fun getProfilePhoto(): Bitmap? {
        try {
            val fileInputStream = context.openFileInput(imageFileName)
            val imageBitmap = BitmapFactory.decodeStream(fileInputStream)
            fileInputStream.close()
            return imageBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun setProfilePhoto(imageBitmap: Bitmap?, imageFileName: String) {
        try {
            imageBitmap?.let { bitmap ->
                val fileOutputStream = context.openFileOutput(imageFileName, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    fun getLocalData(): LocalData {
        return LocalData(getUserName(),getPhoneNumber(), getAddress(), getProfilePhoto())
    }

    private fun getUserName(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.USERNAME,
            SharedPrefRepo.EMPTY
        ).toString()
    }

    fun setUserName(userName: String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.USERNAME, userName)
    }
    fun setLocalData(localData: LocalData, imageFileName: String) {
        setUserName(localData.userName)
        setProfilePhoto( localData.profileImg, imageFileName)
        setAddress(localData.address)
        setPhoneNumber(localData.phone)
    }

    private fun getPhoneNumber(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.PHONE,
            SharedPrefRepo.EMPTY
        ).toString()
    }

    private fun setPhoneNumber(phone: String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.PHONE, phone)
    }

    private fun getAddress(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.ADDRESS,
            SharedPrefRepo.EMPTY
        ).toString()
    }

    private fun setAddress(address: String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.ADDRESS, address)
    }
     fun getCustomerId():String {
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE, SharedPrefRepo.CUSTOMER_ID, SharedPrefRepo.EMPTY).toString()
    }

    fun deleteCustomerId(){
        sharedPrefRepo.deleteData(SharedPrefRepo.PROFILE,SharedPrefRepo.CUSTOMER_ID)
    }

}