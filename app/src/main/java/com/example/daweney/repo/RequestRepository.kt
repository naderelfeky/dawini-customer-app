package com.example.daweney.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.myrequests.RequestBody
import com.example.daweney.pojo.myrequests.RequestResponse
import com.example.daweney.pojo.update_profile.LocalData
import retrofit2.Call

class RequestRepository(val context: Context) {
    private  var sharedPrefRepo: SharedPrefRepo = SharedPrefRepo(context)
    private val imageFileName = "profileImg.jpg"
    private val DARK = "dark"
    private val LIGHT = "light"
    private val ARABIC = "arabic"
    private val ARABIC_CODE = "ar"
    private val ENGLISH = "english"
    private val ENGLISH_CODE = "en"
    private val PHONENUMBER = "+201201545101"

    fun getMyRequests(requestBody: RequestBody): Call<RequestResponse> {
        return UserClient.create().getMyRequests(requestBody)
    }
    fun getLanguage():String{
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE,SharedPrefRepo.LANGUAGE,ARABIC).toString()
    }
    fun setLanguage(language:String){
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE,SharedPrefRepo.LANGUAGE,language)
    }
    fun getTheme():String{
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE,SharedPrefRepo.THEME,DARK).toString()
    }
    fun setTheme(theme:String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.THEME, theme)
    }
    fun getCustomerId():String {
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE, SharedPrefRepo.CUSTOMER_ID, SharedPrefRepo.EMPTY).toString()
    }
    fun deleteCustomerId(){
        sharedPrefRepo.deleteData(SharedPrefRepo.PROFILE,SharedPrefRepo.CUSTOMER_ID)
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
    private fun getPhoneNumber(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.PHONE,
            SharedPrefRepo.EMPTY
        ).toString()
    }
    private fun getAddress(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.ADDRESS,
            SharedPrefRepo.EMPTY
        ).toString()
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
}