package com.mwi.oledsaver.mask

import android.util.TypedValue
import android.view.View

object LayoutUtils {
    fun getDpValue(value: Float, view: View): Int {
        val resources = view.getResources()

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.getDisplayMetrics()
        ).toInt()
    }
}
