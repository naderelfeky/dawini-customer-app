package com.example.daweney.ui.servicescategory

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.daweney.R
import com.example.daweney.pojo.intent_extra_key.IntentExtraKey
import com.example.daweney.pojo.services_category.ServicesCategoryResponse
import com.example.daweney.ui.dialog.CustomDialogFragment
import com.example.daweney.ui.sevices.Services
import kotlinx.android.synthetic.main.activity_services_category.*

class ServicesCategory : LocalizationActivity() {
    private lateinit var servicesViewModel: ServicesCategoryViewModel
    private var services=ServicesCategoryResponse()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_category)

        servicesViewModel = ViewModelProvider(this)[ServicesCategoryViewModel::class.java]


        progressBarObserver()
        dialogObserver()
        servicesCategoryObserver()
        onFailureObserver()
        getServicesCategory()
        onItemOFCategoryClick()
        refreshMyCategory.setOnRefreshListener { getServicesCategory() }
        goBack.setOnClickListener { onClick(it) }
        try_again_btn.setOnClickListener { onClick(it) }
        setLightStatusBar()
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

    private fun dialogObserver() {
        servicesViewModel.errorMessage.observe(this) {
            if (it) {
                val dialogFragment = CustomDialogFragment(R.string.error_with_server_try_again.toString(), applicationContext)
                dialogFragment.show(supportFragmentManager, "services_category_error_dialog")
            }
        }
    }


    private fun servicesCategoryObserver() {
        servicesViewModel.servicesCategory.observe(this) {
            it?.let {
                services=it
                services_gridView.adapter = ServicesAdapter(this, sortServicesCategory(it))
            }

        }
    }

    private fun sortServicesCategory(it:ServicesCategoryResponse):ServicesCategoryResponse{

        val sortList=services.sortedBy {
            when (it.EnglishName) {
                "Nursing services" -> 0
                "medical services" -> 1
                "Radiology services" -> 2
                "Medical tests" -> 3
                "Medical components" -> 4
                "Medical supply" -> 5
                else -> 6
            }
        }

        val servicesCategoryResponse=ServicesCategoryResponse()
            servicesCategoryResponse.addAll(sortList)

        services=servicesCategoryResponse

        return services
    }


    private fun onFailureObserver() {
        servicesViewModel.failWithConnection.observe(this) {
            if (it) {
                noConnectionErrorContainer.visibility = ViewGroup.VISIBLE
                noConnectionAnimationView.visibility = ViewGroup.VISIBLE
                services_gridView.visibility = ViewGroup.GONE
                noConnectionAnimationView.setAnimation(R.raw.no_connection)
                noConnectionAnimationView.playAnimation()
            } else {
                noConnectionErrorContainer.visibility = ViewGroup.GONE
                noConnectionAnimationView.visibility = ViewGroup.GONE
                services_gridView.visibility = ViewGroup.VISIBLE

            }
        }
    }


    private fun getServicesCategory() {
        servicesViewModel.getServicesCategory()
        refreshMyCategory.isRefreshing = false
    }


    private fun onItemOFCategoryClick() {
        services_gridView.onItemClickListener = (
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val intent = Intent(this, Services::class.java)
                    intent.putExtra(IntentExtraKey.SERVICE_TYPE, services[position].EnglishName)
                    startActivity(intent)
                    finish()
                })
    }


    private fun onClick(view: View?) {
        when (view?.id) {
            goBack.id -> {
                finish()
            }

            try_again_btn.id -> {
                getServicesCategory()
            }

        }
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


}