package com.example.daweney.ui.myrequestdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daweney.R
import com.example.daweney.pojo.my_request_details.ProviderRate
import com.example.daweney.pojo.my_request_details.RequestApplicationResponse
import com.example.daweney.ui.myrequests.RequestRecyclerViewAdapter
import kotlinx.android.synthetic.main.request_provider_row.view.*

class RequestProviderAdapter(private val context:Context,private val providerList: RequestApplicationResponse, private val requestProviderInterface: RequestProviderInterface):
    RecyclerView.Adapter<RequestProviderAdapter.ProviderViewHolder>(){
    class ProviderViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
      val providerRate: TextView =itemView.findViewById(R.id.provider_rate)
      val providerName: TextView =itemView.findViewById(R.id.provider_name)
      val providerGender: TextView =itemView.findViewById(R.id.provider_gender)
      val requestCost: TextView =itemView.findViewById(R.id.request_cost)
      val acceptProvider:ImageButton=itemView.findViewById(R.id.accept_provider)
      val rejectProvider:ImageButton=itemView.findViewById(R.id.reject_provider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.request_provider_row,parent,false)
        return ProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        val provider=providerList[position]
        holder.providerName.text=provider.providerName.toString()
        holder.providerRate.text=getRate(provider.providerRate)
        holder.providerGender.text=context.getText(R.string.male)
        holder.requestCost.text=provider.priceOfService.toString()
        holder.acceptProvider.setOnClickListener {
            requestProviderInterface.onAcceptApplication(provider)
        }
        holder.rejectProvider.setOnClickListener {
            requestProviderInterface.onRejectApplication(provider)
        }

    }

    private fun getRate(providerRate: ProviderRate): String {
     val numberOfRate=providerRate.ratedBy
     val valueOfRate:Float=providerRate.value.toFloat()
        if(numberOfRate!=0) {
            val rate: Float = valueOfRate / numberOfRate
            return "$rate  ($numberOfRate)"
        }
        return "0  (0)"
    }

    override fun getItemCount(): Int {
        return providerList.size
    }

}