package com.limyusontudtud.souvseek.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopOwnerItemsViewModel : ViewModel() {

    private val _shopList = MutableLiveData<MutableList<String>>(mutableListOf())
    val shopList: LiveData<MutableList<String>> get() = _shopList

    fun addShop(shopName: String) {
        _shopList.value?.add(shopName)
        _shopList.value = _shopList.value // Notify observers
    }
}