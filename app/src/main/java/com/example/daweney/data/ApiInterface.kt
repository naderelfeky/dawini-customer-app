package com.example.daweney.data

import com.example.daweney.pojo.*
import com.example.daweney.pojo.forgotpass.EmailUser
import com.example.daweney.pojo.forgotpass.MsgResponse
import com.example.daweney.pojo.login.LoginResponse
import com.example.daweney.pojo.login.LoginUser
import com.example.daweney.pojo.my_request_details.*
import com.example.daweney.pojo.myrequests.RequestBody
import com.example.daweney.pojo.myrequests.RequestResponse
import com.example.daweney.pojo.profile.ProfileBody
import com.example.daweney.pojo.profile.ProfileResponse
import com.example.daweney.pojo.register.RegisterResponse
import com.example.daweney.pojo.register.RegisterUser
import com.example.daweney.pojo.resetpass.ResetPassword
import com.example.daweney.pojo.send_request.SendRequestBody
import com.example.daweney.pojo.send_request.SendRequestResponse
import com.example.daweney.pojo.services.ServicesBody
import com.example.daweney.pojo.services.ServicesResponse
import com.example.daweney.pojo.services_category.ServicesCategoryResponse
import com.example.daweney.pojo.update_profile.UpdateProfileBody
import com.example.daweney.pojo.update_profile.UpdateProfileResponse
import com.example.daweney.pojo.verifyaccount.VerifyUser
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //   https://daweney.onrender.com/customers/signin

      @POST("signin")
      fun login(@Body user: LoginUser):Call<LoginResponse>

      @POST("signup")
      fun register(@Body user: RegisterUser):Call<RegisterResponse>

      @POST("verifyusr")
      fun verifyUser(@Body verifyUser: VerifyUser):Call<com.example.daweney.pojo.verifyaccount.MsgResponse>

      @POST("sendcode")
      fun sendCode(@Body emailUser: EmailUser):Call<MsgResponse>

      @POST("resetpass")
      fun resetPassword(@Body resetPassword: ResetPassword):Call<com.example.daweney.pojo.resetpass.MsgResponse>

      @POST("getreq")
       fun getMyRequests(@Body requestBody:RequestBody):Call<RequestResponse>

       @GET("get-srvc")
       fun getServicesCategory():Call<ServicesCategoryResponse>

       @POST("findservice")
       fun getServices(@Body servicesBody: ServicesBody):Call<ServicesResponse>

       @POST("sendreq")
       fun sendRequest(@Body sendRequestBody: SendRequestBody):Call<SendRequestResponse>

       @POST("cancelreq")
       fun cancelRequest(@Body cancelRequestBody: CancelRequestBody):Call<CancelRequestResponse>

       @POST("getapp")
       fun getRequestApplication(@Body requestApplicationBody: RequestApplicationBody):Call<RequestApplicationResponse>

       @POST("acceptapp")
       fun acceptApp(@Body acceptAppBody: AcceptAppBody):Call<AcceptAppResponse>

       @POST("rejectapp")
       fun rejectApp(@Body rejectAppBody: RejectAppBody):Call<RejectAppResonse>

       @POST("done")
       fun requestDone(@Body doneBody: DoneBody):Call<DoneResponse>

       @POST("get-profile")
       fun getProfileData(@Body profileBody:ProfileBody):Call<ProfileResponse>

       @POST("update-profile")
       fun updateProfileData(@Body updateProfileBody: UpdateProfileBody):Call<UpdateProfileResponse>
}