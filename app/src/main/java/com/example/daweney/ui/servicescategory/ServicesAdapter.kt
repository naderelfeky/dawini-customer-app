package com.example.daweney.ui.servicescategory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.daweney.R
import com.example.daweney.pojo.services_category.ServicesCategoryResponse
import com.example.daweney.pojo.services_category.ServicesCategoryResponseItem
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.services_category_item.view.*
import java.util.*

class ServicesAdapter(
    private val context: Context,
    private val gridItems: ServicesCategoryResponse
) : BaseAdapter() {
    override fun getCount(): Int {
        return gridItems.size
    }

    override fun getItem(position: Int): Any {
        return gridItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.services_category_item, parent, false)
        }

        val item: ServicesCategoryResponseItem = getItem(position) as ServicesCategoryResponseItem
        val imageUrl = item.photo
        val currentLanguage = Locale.getDefault().language
        //set services type
        view?.TV_services?.text = when (currentLanguage) {
            "ar" -> {
                item.arabicName
            }
            "en" -> {
                item.EnglishName
            }
            else -> {
                item.EnglishName
            }
        }

        Picasso.get().load(imageUrl).into(view?.services_img)

        return view!!
    }

}