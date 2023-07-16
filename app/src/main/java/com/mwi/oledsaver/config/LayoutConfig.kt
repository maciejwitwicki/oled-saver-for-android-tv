package com.mwi.oledsaver.config

interface LayoutConfig {

    fun getClockMaskerConfig(): ClockMaskerLayoutSetup
    fun getLogoMaskerLayoutSetup(): LogoMaskerLayoutSetup

    data class LogoMaskerLayoutSetup(val width: Int, val height: Int)
    data class ClockMaskerLayoutSetup(val width: Int, val right: Int, val rotation: Boolean)

}