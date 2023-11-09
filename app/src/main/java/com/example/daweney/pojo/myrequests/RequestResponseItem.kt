package com.example.daweney.pojo.myrequests

import android.os.Parcel
import android.os.Parcelable

data class RequestResponseItem(
    val __v: Int,
    val _id: String,
    val address: String,
    val customerId: String,
    val date: String,
    val priceOfService: Int,
    val service: List<Service>,
    var status: String,
    val typeofservice: String
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.createTypedArrayList(Service)!!,
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(__v)
        dest.writeString(_id)
        dest.writeString(address)
        dest.writeString(customerId)
        dest.writeString(date)
        dest.writeInt(priceOfService)
        dest.writeTypedList(service)
        dest.writeString(status)
        dest.writeString(typeofservice)
    }

    companion object CREATOR : Parcelable.Creator<RequestResponseItem> {
        override fun createFromParcel(parcel: Parcel): RequestResponseItem {
            return RequestResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<RequestResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}