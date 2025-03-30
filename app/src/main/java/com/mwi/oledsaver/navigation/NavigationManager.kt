package com.mwi.oledsaver.navigation


import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.exit.ExitFragment
import com.mwi.oledsaver.mask.MaskingFragmentPolsat
import com.mwi.oledsaver.mask.MaskingFragmentTvn
import kotlinx.serialization.Serializable

class NavigationManager(private val navigationController: NavController) {

    @Serializable
    data object MaskTvnRoute

    @Serializable
    data object MaskPolsatRoute

    @Serializable
    data object ExitFragmentRoute

    private val navigationItems = listOf(MaskTvnRoute, MaskPolsatRoute)
    private var currentItemIndex = 0

    init {

        navigationController.graph = navigationController.createGraph(
            startDestination = navigationItems[currentItemIndex]
        ) {
            fragment<MaskingFragmentTvn, MaskTvnRoute> {
                label = "TvnMask"
            }
            fragment<MaskingFragmentPolsat, MaskPolsatRoute> {
                label = "PolsatMask"
            }
            fragment<ExitFragment, ExitFragmentRoute> {
                label = "ExitFragment"
            }
        }

    }

    fun navigateLeft() {
        val newCurrentItemIndex = if (currentItemIndex > 0) currentItemIndex - 1 else 0
        navigateIfIndexChanged(newCurrentItemIndex)
    }

    fun navigateRight() {
        val maxPos = navigationItems.size - 1
        val newCurrentItemIndex = if (currentItemIndex < maxPos) currentItemIndex + 1 else maxPos
        navigateIfIndexChanged(newCurrentItemIndex)
    }

    fun navigateToExit() {
        navigationController.navigate(ExitFragmentRoute)
    }

    private fun navigateIfIndexChanged(newIndex: Int) {
        if (newIndex != currentItemIndex) {
            currentItemIndex = newIndex
            val route = navigationItems[currentItemIndex]
            Log.i(LOGGING_TAG, "${this.javaClass.simpleName} navigating to item $newIndex: $route")
            navigationController.navigate(route)
        }
    }

}
