package com.example.daweney.repo


import android.content.Context
import com.example.daweney.data.UserClient
import com.example.daweney.pojo.forgotpass.EmailUser
import com.example.daweney.pojo.forgotpass.MsgResponse
import com.example.daweney.pojo.login.LoginResponse
import com.example.daweney.pojo.login.LoginUser
import com.example.daweney.pojo.register.RegisterResponse
import com.example.daweney.pojo.register.RegisterUser
import com.example.daweney.pojo.resetpass.ResetPassword
import com.example.daweney.pojo.verifyaccount.VerifyUser
import retrofit2.Call

class UserRepository(val context: Context) {
   private val sharedPrefRepo=SharedPrefRepo(context)
    private val DARK="dark"
    private val LIGHT="light"
    private val ARABIC="arabic"
    private val ARABIC_CODE="ar"
    private val ENGLISH="english"
    private val ENGLISH_CODE="en"
        fun login(user: LoginUser):Call<LoginResponse>{
            return UserClient.create().login(user)
        }

        fun register(user: RegisterUser):Call<RegisterResponse>{
            return UserClient.create().register(user)
        }

        fun sendCode(email: EmailUser):Call<MsgResponse>{
            return UserClient.create().sendCode(email)
        }

        fun verifyUser(verifyUser: VerifyUser):Call<com.example.daweney.pojo.verifyaccount.MsgResponse>{
            return UserClient.create().verifyUser(verifyUser)
        }

        fun resetPassword(resetPassword: ResetPassword):Call<com.example.daweney.pojo.resetpass.MsgResponse>{
            return UserClient.create().resetPassword(resetPassword)
        }
    fun setCustomerId(customerId:String){
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE,SharedPrefRepo.CUSTOMER_ID,customerId)
    }
    fun getCustomerId():String {
        return sharedPrefRepo.getData(SharedPrefRepo.PROFILE, SharedPrefRepo.CUSTOMER_ID, SharedPrefRepo.EMPTY).toString()
    }

    fun getLanguage(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.LANGUAGE,
            ARABIC
        ).toString()
    }

    fun setLanguage(language: String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.LANGUAGE, language)
    }

    fun getTheme(): String {
        return sharedPrefRepo.getData(
            SharedPrefRepo.PROFILE,
            SharedPrefRepo.THEME,
            DARK
        ).toString()
    }

    fun setTheme(theme: String) {
        sharedPrefRepo.saveData(SharedPrefRepo.PROFILE, SharedPrefRepo.THEME, theme)
    }

}