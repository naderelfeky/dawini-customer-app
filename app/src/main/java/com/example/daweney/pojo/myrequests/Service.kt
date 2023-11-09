package com.example.daweney.pojo.myrequests

import android.os.Parcel
import android.os.Parcelable

data class Service(
    val ArabicName: String,
    val EnglishName: String,
    val __v: Int,
    val _id: String,
    val price: Int
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ArabicName)
        parcel.writeString(EnglishName)
        parcel.writeInt(__v)
        parcel.writeString(_id)
        parcel.writeInt(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}