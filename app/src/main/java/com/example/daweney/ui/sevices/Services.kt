package com.example.daweney.ui.sevices

import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.intent_extra_key.IntentExtraKey
import com.example.daweney.pojo.services.ServicesBody
import com.example.daweney.pojo.services.ServicesResponse
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.sendrequest.SendRequest
import kotlinx.android.synthetic.main.activity_services.btn_continue
import kotlinx.android.synthetic.main.activity_services.messageContainer
import kotlinx.android.synthetic.main.activity_services.progressBar
import kotlinx.android.synthetic.main.activity_services.refreshServices
import kotlinx.android.synthetic.main.activity_services.refreshServicesList
import kotlinx.android.synthetic.main.activity_services.searchContainer
import kotlinx.android.synthetic.main.activity_services.serviceNoConnectionAnimationView
import kotlinx.android.synthetic.main.activity_services.toolbarIconSearch
import kotlinx.android.synthetic.main.activity_services.toolbarSearch
import kotlinx.android.synthetic.main.activity_services.try_again_btn

class Services : LocalizationActivity(), ServicesInterface {
    private lateinit var servicesViewModel: ServicesViewModel
    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var serviceType: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        servicesViewModel = ViewModelProvider(this)[ServicesViewModel::class.java]

        getServices()
        recyclerViewInit()
        btn_continue.setOnClickListener { onClick(it) }
        getServicesObserver()
        progressBarObserver()
        onFailureObserver()
        dialogObserver()
        searcherChangeListener()
        toolbarIconSearch.setOnClickListener { onClick(it) }
        try_again_btn.setOnClickListener { onClick(it) }
        refreshServices.setOnRefreshListener { getServices() }
        refreshServicesList.setOnRefreshListener { getServices() }
        setLightStatusBar()
    }

    private fun searcherChangeListener() {
        toolbarSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::servicesAdapter.isInitialized) servicesAdapter.filter.filter(toolbarSearch.text)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun onFailureObserver() {
        servicesViewModel.failWithConnection.observe(this) {
            if (it) {
                servicesRecyclerView.visibility = ViewGroup.GONE
                serviceNoConnectionAnimationView.visibility = ViewGroup.VISIBLE
                messageContainer.visibility = ViewGroup.VISIBLE
                serviceNoConnectionAnimationView.setAnimation(R.raw.no_connection)
                serviceNoConnectionAnimationView.playAnimation()


            } else {

                servicesRecyclerView.visibility = ViewGroup.VISIBLE
                serviceNoConnectionAnimationView.visibility = ViewGroup.GONE
                messageContainer.visibility = ViewGroup.GONE

            }
        }
    }

    private fun onClick(view: View?) {
        when (view?.id) {
            R.id.toolbarIconSearch -> {
                if (searchContainer.width == resources.getDimensionPixelSize(R.dimen.toolBar_width)) {
                    openSearchBar()
                } else {
                    closeSearchBar()
                }
            }

            R.id.try_again_btn -> {
                getServices()
            }

            R.id.btn_continue -> {
                startSendRequestActivity()
            }

        }
    }

    private fun startSendRequestActivity() {
        val intent = Intent(this, SendRequest::class.java)
        val selectionServices = ServicesResponse()
        selectionServices.addAll(servicesAdapter.getSelectionList())
        intent.putExtra(IntentExtraKey.SERVICES, selectionServices)
        intent.putExtra(IntentExtraKey.SERVICE_TYPE, serviceType)
        startActivity(intent)
    }

    private fun closeSearchBar() {
        val targetWidth = resources.getDimensionPixelSize(R.dimen.toolBar_width)
        val animator = ValueAnimator.ofInt(searchContainer.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = searchContainer.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            searchContainer.requestLayout()
        }
        animator.duration = 400 // Set the duration of the animation (in milliseconds)
        animator.start()
        toolbarIconSearch.setImageResource(R.drawable.ic_search)
    }


    private fun openSearchBar() {
        val displayMetrics = resources.displayMetrics
        val deviceWidth = displayMetrics.widthPixels

                    //get margin of toolbar button (start+end)
        val dpToPx = resources.getDimension(R.dimen.toolBar_button_margin)*2

        val targetWidth = deviceWidth - dpToPx.toInt()
        val animator = ValueAnimator.ofInt(searchContainer.width, targetWidth)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = searchContainer.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            searchContainer.requestLayout()
        }
        animator.duration = 400 // Set the duration of the animation (in milliseconds)
        animator.start()
        toolbarIconSearch.setImageResource(R.drawable.ic_left)
    }


    private fun getServices() {
        servicesViewModel.getServices(gerServiceType())
        refreshServices.isRefreshing = false
        refreshServicesList.isRefreshing = false
        btn_continue.visibility = ViewGroup.GONE
    }

    private fun setLightStatusBar() {
        val currentTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (currentTheme == Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme
            window.decorView.systemUiVisibility = 0

        } else {
            // Light theme
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun recyclerViewInit() {
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView)
        servicesRecyclerView.setHasFixedSize(true)
        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun getServicesObserver() {
        servicesViewModel.services.observe(this) {
            if (it != null) {
                servicesAdapter = ServicesAdapter(it, this)
                servicesRecyclerView.adapter = servicesAdapter
            }
        }
    }

    private fun gerServiceType(): ServicesBody {
        serviceType = intent.getStringExtra(IntentExtraKey.SERVICE_TYPE).toString()
        return ServicesBody(serviceType)
    }


    private fun dialogObserver() {
        servicesViewModel.errorMessage.observe(this) {
            if (it) {
                val dialogFragment = CustomDialogFragment(R.string.this_category_not_found.toString(), applicationContext)
                dialogFragment.show(supportFragmentManager, "services_error_dialog")
            }
        }
    }

    private fun progressBarObserver() {
        servicesViewModel.progressBar.observe(this) {
            if (it) {
                progressBar.visibility = ViewGroup.VISIBLE
            } else {
                progressBar.visibility = ViewGroup.INVISIBLE
            }
        }
    }

    override fun updateButtonState(isEnable: Boolean) {
        if (isEnable) {
            btn_continue.visibility = ViewGroup.VISIBLE
        } else {
            btn_continue.visibility = ViewGroup.GONE
        }
    }


}