package com.example.daweney.pojo.localization

import android.content.Context
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import java.util.*

class Localization: LocalizationApplication() {
    override fun getDefaultLanguage(context: Context) = Locale.ENGLISH!!
}