package com.example.daweney.ui.sevices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.daweney.R
import com.example.daweney.pojo.services.*
import java.util.*
import kotlin.collections.ArrayList


class ServicesAdapter(private val services: ServicesResponse,private val servicesInterface: ServicesInterface) :
    RecyclerView.Adapter<ServicesAdapter.RequestViewHolder>(), Filterable {
    private val fullServicesList=ArrayList<ServicesResponseItem>()
    private val selectedServices=ArrayList<ServicesResponseItem>()

    init{
        fullServicesList.addAll(services)
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =itemView.findViewById(R.id.subServices_img)
        val textView: TextView =itemView.findViewById(R.id.TV_SubServices_name)
        val background: LinearLayout =itemView.findViewById(R.id.background)

        fun bind(isSelected: Boolean) {
            if (isSelected) {
                background.setBackgroundResource(R.drawable.service_background_selected)
            } else {
                background.setBackgroundResource(R.drawable.service_background_unselect)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.services_list_row, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val service=services[position]
        val isSelected = selectedServices.contains(services[position])
        holder.bind(isSelected)
        holder.textView.text=getServicesName(service)
//        holder.imageView.setImageResource(R.drawable.doctor)

        holder.itemView.setOnClickListener {
            toggleSelection(position)
            updateButtonState()
        }

    }

    private fun getServicesName(service: ServicesResponseItem): String {
        val currentLocale: Locale = Locale.getDefault()
        return when(currentLocale.language){
            "ar"->{service.ArabicName}
            "en"->{service.EnglishName}
            else->{service.EnglishName}
        }

    }

    private fun updateButtonState() {
        servicesInterface.updateButtonState(selectedServices.isNotEmpty())
    }


    override fun getItemCount(): Int {
        return services.size
    }

    private fun toggleSelection(position: Int) {
        if (selectedServices.contains(services[position])) {
            selectedServices.remove(services[position])
        } else {
            selectedServices.add(services[position])
        }
        notifyItemChanged(position)
    }


    fun getSelectionList():List<ServicesResponseItem>{
        return selectedServices
    }

    override fun getFilter(): Filter {
        return servicesFiler
    }

    private val servicesFiler = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ServicesResponseItem>()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(fullServicesList)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.ROOT).trim()
                for (item in fullServicesList) {

                    if (item.EnglishName.lowercase(Locale.ROOT).contains(filterPattern)||
                        item.ArabicName.lowercase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            services.clear()
            if(results!=null){
                services.addAll(results.values as List<ServicesResponseItem>)
            }
            notifyDataSetChanged()
        }
    }

}