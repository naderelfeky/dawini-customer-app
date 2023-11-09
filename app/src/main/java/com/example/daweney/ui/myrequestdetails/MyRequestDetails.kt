package com.example.daweney.ui.myrequestdetails

import android.animation.Animator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.intent_extra_key.IntentExtraKey
import com.example.daweney.pojo.my_request_details.*
import com.example.daweney.pojo.myrequests.RequestResponseItem
import com.example.daweney.pojo.myrequests.Service
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.login.Login
import com.example.daweney.ui.myrequests.MyRequests
import kotlinx.android.synthetic.main.activity_my_request_details.*
import java.text.SimpleDateFormat
import java.util.*


class MyRequestDetails : LocalizationActivity(), RequestProviderInterface {
    private lateinit var  requestResponseItem:RequestResponseItem
    private lateinit var serviceType: TextView
    private lateinit var date: TextView
    private lateinit var address: TextView
    private lateinit var cost: TextView
    private lateinit var providerGender: TextView
    private lateinit var providerName: TextView
    private lateinit var cancelButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var requestProviderApply: RecyclerView
    private lateinit var requestId: String
    private lateinit var requestStatus: String
    private var requestServicesList=mutableListOf<String>()
    private lateinit var lottieAnimationView: LottieAnimationView
    private var cancelRequestViewModel = CancelRequestViewModel()
    private var requestApplicationViewModel = RequestApplicationViewModel()
    private var acceptProviderViewModel=AcceptProviderViewModel()
    private var rejectProviderViewModel=RejectProviderViewModel()
    private var doneViewModel=DoneViewModel()
    private val requestApplicationResponse = RequestApplicationResponse()
    private lateinit var requestProviderAdapter: RequestProviderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_request_details)
        viewModelInit()
        initialization()
        progressBarObserve()
        requestCancelSuccessObserve()
        requestCancelErrorObserve()
        requestApplicationSuccessObserve()
        requestApplicationNoProviderObserve()
        requestApplicationErrorObserve()
        requestApplicationFailureObserve()
        requestCancelFailureObserve()
        acceptProviderSuccessObserve()
        acceptProviderErrorObserve()
        acceptProviderFailureObserve()
        rejectProviderSuccessObserve()
        rejectProviderErrorObserve()
        rejectProviderFailureObserve()
        getDataFormIntent()
        cancelButton.setOnClickListener { onClick(it) }
        goBack.setOnClickListener { onClick(it) }
        done_btn.setOnClickListener { onClick(it) }
        doneSuccessObserve()
        doneErrorObserve()
        doneFailureObserve()
        setLightStatusBar()

    }

    private fun viewModelInit() {

        cancelRequestViewModel      = ViewModelProvider(this)[CancelRequestViewModel::class.java]
        requestApplicationViewModel = ViewModelProvider(this)[RequestApplicationViewModel::class.java]
        acceptProviderViewModel     =ViewModelProvider(this)[AcceptProviderViewModel::class.java]
        rejectProviderViewModel     =ViewModelProvider(this)[RejectProviderViewModel::class.java]
        doneViewModel               =ViewModelProvider(this)[DoneViewModel::class.java]
    }

    private fun setLightStatusBar() {
        val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme
            window.decorView.systemUiVisibility = 0

        } else {
            // Light theme
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun doneFailureObserve() {
        doneViewModel.failure.observe(this){
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error_with_connection),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "failure_dialog")

            }
        }
    }

    private fun doneErrorObserve() {
        doneViewModel.error.observe(this){
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "error_dialog")

            }
        }

    }

    private fun doneSuccessObserve() {
        doneViewModel.requestDone.observe(this){
            val intent = Intent(this, MyRequests::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun requestApplicationNoProviderObserve() {
        requestApplicationViewModel.noProvider.observe(this){
            if(it){
                val intent=Intent(this,MyRequestDetails::class.java)
                requestResponseItem.status="pending"
                intent.putExtra(IntentExtraKey.MY_REQUEST, requestResponseItem)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun rejectProviderSuccessObserve() {
        rejectProviderViewModel.providerReject.observe(this){
            if(it){
                val intent=Intent(this,MyRequestDetails::class.java)
                intent.putExtra(IntentExtraKey.MY_REQUEST, requestResponseItem)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun rejectProviderFailureObserve() {
        rejectProviderViewModel.failure.observe(this){
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error_with_connection),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "reject_failure_dialog")

            }

        }
    }

    private fun rejectProviderErrorObserve() {
        rejectProviderViewModel.error.observe(this){
            if(it) {
                val dialogFragment=CustomDialogFragment(getString(R.string.error), applicationContext)
                dialogFragment.show(supportFragmentManager, "reject_error_dialog")
            }
        }
    }

    private fun acceptProviderFailureObserve() {
        acceptProviderViewModel.failure.observe(this){
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error_with_connection),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "request_application_failure_dialog")

            }

        }
    }

    private fun acceptProviderErrorObserve() {
        acceptProviderViewModel.error.observe(this){
            if(it) {
                val dialogFragment=CustomDialogFragment(getString(R.string.error), applicationContext)
                dialogFragment.show(supportFragmentManager, "request_application_failure_dialog")
            }
        }

    }

    private fun acceptProviderSuccessObserve() {
        acceptProviderViewModel.providerAccept.observe(this){
            val intent=Intent(this,MyRequestDetails::class.java)
            requestResponseItem.status="Accepted"
            intent.putExtra(IntentExtraKey.MY_REQUEST, requestResponseItem)
            startActivity(intent)
            finish()
        }
    }


    private fun requestApplicationFailureObserve() {
        requestApplicationViewModel.failure.observe(this) {
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error_with_connection),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "request_application_failure_dialog")

            }
        }
    }

    private fun requestApplicationErrorObserve() {
        requestApplicationViewModel.error.observe(this) {
            if (it) {
                val dialogFragment =
                CustomDialogFragment(getString(R.string.error), applicationContext)
                dialogFragment.show(supportFragmentManager, "request_application_error_dialog")
            }
        }
    }

    private fun requestApplicationSuccessObserve() {
        requestApplicationViewModel.requestApplication.observe(this) {
            requestApplicationResponse.addAll(it)


            when (requestStatus) {
                "applied" -> {
                    requestProviderAdapter =
                        RequestProviderAdapter(this,requestApplicationResponse, this)
                    requestProviderApply.adapter=requestProviderAdapter
                }
                "Accepted" -> {   //response is one provider
                    val providerDetails = requestApplicationResponse[0]
                    setProviderDetails(providerDetails.providerName)
                }

            }

        }
    }

    private fun requestCancelFailureObserve() {
        cancelRequestViewModel.failure.observe(this) {
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(
                        getString(R.string.error_with_connection),
                        applicationContext
                    )
                dialogFragment.show(supportFragmentManager, "cancel_request_error_dialog")
            }
        }
    }

    private fun requestCancelErrorObserve() {
        cancelRequestViewModel.error.observe(this) {
            if (it) {
                val dialogFragment =
                    CustomDialogFragment(getString(R.string.error), applicationContext)
                dialogFragment.show(supportFragmentManager, "cancel_request_error_dialog")
            }
        }
    }

    private fun requestCancelSuccessObserve() {
        cancelRequestViewModel.requestCancel.observe(this) {
            if (it) {
                requestDetails.visibility = ViewGroup.GONE
                lottieAnimationView.visibility = ViewGroup.VISIBLE
                lottieAnimationView.setAnimation(R.raw.request_cancel)
                lottieAnimationView.playAnimation()
                lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        val intent = Intent(this@MyRequestDetails, MyRequests::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finishAffinity()
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }
        }
    }

    private fun onClick(view: View?) {
        when (view?.id) {
            R.id.requestCancelButton -> {
                cancelRequestViewModel.cancelRequest(CancelRequestBody(requestId))
            }
            R.id.done_btn->{
                doneViewModel.done(DoneBody(requestResponseItem._id))
            }
            R.id.goBack->{
                finish()
            }
        }
    }

    private fun progressBarObserve() {
        cancelRequestViewModel.progressBar.observe(this) { setProgressBar(it) }
        requestApplicationViewModel.progressBar.observe(this) { setProgressBar(it) }
        acceptProviderViewModel.progressBar.observe(this){setProgressBar(it)}
        rejectProviderViewModel.progressBar.observe(this){setProgressBar(it)}
        doneViewModel.progressBar.observe(this){setProgressBar(it)}
    }

    private fun setProgressBar(it: Boolean) {
        if (it) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun initialization() {
        date = findViewById(R.id.requestDate)
        address = findViewById(R.id.requestAddress)
        cost = findViewById(R.id.requestCost)
        providerGender = findViewById(R.id.requestProviderGender)
        providerName = findViewById(R.id.requestProviderName)
        serviceType = findViewById(R.id.serviceType)
        cancelButton = findViewById(R.id.requestCancelButton)
        requestProviderApply = findViewById(R.id.request_provider_apply)
        requestProviderApply.setHasFixedSize(true)
        requestProviderApply.layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBar)
        lottieAnimationView = findViewById(R.id.myRequestDetailsLottieAnimationView)
        requestProviderApply = findViewById(R.id.request_provider_apply)
    }

    private fun getDataFormIntent() {
        requestResponseItem = intent.getParcelableExtra<RequestResponseItem>(IntentExtraKey.MY_REQUEST)!!
        serviceType.text =  requestResponseItem?.typeofservice
        date.text = getDate( requestResponseItem!!.date)
        address.text =  requestResponseItem.address
        cost.text =  requestResponseItem.priceOfService.toString()
        requestId =  requestResponseItem._id
        requestStatus =  requestResponseItem.status
        requestStatus( requestResponseItem.status)
        getServiceList( requestResponseItem.service)
    }

    private fun getServiceList(services: List<Service>) {
        for(service in services){
            requestServicesList.add(service._id)
        }
    }

    private fun requestStatus(status: String) {
        when (status) {
            "applied" -> { //response is one provider
                enableRecyclerView(true)
                getRequestApplication()
            }
            "pending" -> {
                enableRecyclerView(false)
                setProviderDetails(getString(R.string.no_providers_found_yet))
            }
            "Accepted" -> {
                enableRecyclerView(false)
                getRequestApplication()
            }
            else -> {
                enableRecyclerView(false)
                setProviderDetails(getString(R.string.no_providers_found_yet))
            }
        }
    }

    private fun setProviderDetails(name: String) {
        providerName.text = name
    }


    private fun getRequestApplication() {
        requestApplicationViewModel.getRequestApplication(RequestApplicationBody(requestId))
    }

    private fun getDate(dateString: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date: Date = format.parse(dateString) as Date
        val dateFormat = SimpleDateFormat("hh:mm a - yy/MMM/dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun enableRecyclerView(enable: Boolean) {
        if (enable) {

            provider_details.visibility = ViewGroup.GONE
            requestProviderApply.visibility = ViewGroup.VISIBLE
        } else {
            provider_details.visibility = ViewGroup.VISIBLE
            requestProviderApply.visibility = ViewGroup.GONE
        }
    }

    override fun onAcceptApplication(provider: RequestApplicationResponseItem) {
        val acceptAppBody = AcceptAppBody(provider._id,provider.customerId,provider.providerId,provider.RequestId,requestServicesList)

        acceptProviderViewModel.acceptApp(acceptAppBody)
    }



    override fun onRejectApplication(provider: RequestApplicationResponseItem) {
        val rejectAppBody = RejectAppBody(provider._id)
        rejectProviderViewModel.rejectApp(rejectAppBody)
    }
}