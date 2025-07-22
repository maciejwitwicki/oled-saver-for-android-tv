package com.mwi.oledsaver.mask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableElementsState = MutableLiveData<MaskerVisibilityRequest>()
    val elementsState: LiveData<MaskerVisibilityRequest> get() = mutableElementsState

    fun changeMaskerVisibility(item: MaskerVisibilityRequest) {
        mutableElementsState.value = item
    }

}


data class MaskerVisibilityRequest(val boldStripe: Boolean) {

    fun invertBoldStripe(): MaskerVisibilityRequest {
        return MaskerVisibilityRequest(!boldStripe)
    }

    companion object {
        val AllVisible = MaskerVisibilityRequest(true)
    }
}
