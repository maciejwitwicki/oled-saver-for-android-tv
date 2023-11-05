package com.mwi.oledsaver

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.*
import java.net.URL
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class CatDisplayer : Activity() {

    private val IntervalInSeconds = 13 * 60
    private val DisplayLenghtInMillis = 1000L
    private val MinMarginDistance = 50
    private val CatServiceUrl = "https://cataas.com/cat"

    private lateinit var image: Bitmap

    init {
        val intervalMillis = (IntervalInSeconds * 1000).toLong();

        Timer().scheduleAtFixedRate(0, intervalMillis) {
            Thread {
                val url = URL(CatServiceUrl)
                image = BitmapFactory.decodeStream(url.openStream())
            }.start()
        }
    }

    fun mask(view: View) {
        val intervalMillis: Long = (IntervalInSeconds * 1000).toLong()
        Timer().scheduleAtFixedRate(intervalMillis, intervalMillis) {
            runOnUiThread{
                displayRandomCatAtRandomPosition(view)
            }
        }
    }


    private fun displayRandomCatAtRandomPosition(view: View) {
        val layout = view.findViewById<FrameLayout>(R.id.randomCat)
        setupLayoutLocation(layout, view)

        val imageView = view.findViewById<ImageView>(R.id.randomCatImage)
        imageView.setImageBitmap(this.image)
        layout.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            delay(DisplayLenghtInMillis)
            runOnUiThread {
                layout.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupLayoutLocation(layout: FrameLayout, view: View) {
        val container = view.findViewById<ConstraintLayout>(R.id.layoutContainer)
        val displayMetrics = container.resources.displayMetrics
        val containerWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        val containerHeight = (displayMetrics.heightPixels / displayMetrics.density).toInt()
        val randomX = Random.nextInt(MinMarginDistance, containerWidth - MinMarginDistance)
        val randomY = Random.nextInt(MinMarginDistance, containerHeight - MinMarginDistance)

        val params = layout.layoutParams
        when (params) {
            is ViewGroup.MarginLayoutParams -> {
                params.marginEnd = randomX
                params.bottomMargin = randomY
            }
        }
        layout.layoutParams = params
    }
}