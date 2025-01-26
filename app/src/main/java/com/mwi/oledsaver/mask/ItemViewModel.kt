package com.mwi.oledsaver.mask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    private val mutableElementsState = MutableLiveData<MaskerVisibilityRequest>()
    val elementsState: LiveData<MaskerVisibilityRequest> get() = mutableElementsState

    fun changeMaskerVisibility(item: MaskerVisibilityRequest) {
        mutableElementsState.value = item
    }
}

data class MaskerVisibilityRequest(val boldStripe: Boolean)
