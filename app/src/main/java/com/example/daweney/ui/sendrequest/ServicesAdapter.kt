package com.example.daweney.ui.sendrequest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daweney.R
import com.example.daweney.pojo.services.ServicesResponse
import com.example.daweney.pojo.services.ServicesResponseItem
import java.util.*

class ServicesAdapter(private val services: ServicesResponse):
    RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(var itemView: View): RecyclerView.ViewHolder(itemView){
        val servicesName:TextView=itemView.findViewById(R.id.requestServiceName)
        val servicesCost:TextView=itemView.findViewById(R.id.requestServiceCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.send_request_service_item,parent,false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service: ServicesResponseItem =services[position]
        val currentLocale: Locale = Locale.getDefault()
        val language: String = currentLocale.language
        if (language == "ar") {
            holder.servicesName.text = service.ArabicName
        }else{
            holder.servicesName.text = service.EnglishName
        }
        holder.servicesCost.text=service.price.toString()
    }

    override fun getItemCount(): Int {
       return services.size
    }
}