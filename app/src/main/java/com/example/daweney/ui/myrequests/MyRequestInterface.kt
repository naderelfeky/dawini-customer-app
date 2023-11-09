package com.example.daweney.ui.myrequests

import com.example.daweney.pojo.myrequests.RequestResponseItem

interface MyRequestInterface {
    fun onItemClick(request: RequestResponseItem)
    fun onItemLongClick(request: RequestResponseItem)

}