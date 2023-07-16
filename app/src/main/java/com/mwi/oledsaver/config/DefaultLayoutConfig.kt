package com.mwi.oledsaver.config

class DefaultLayoutConfig : LayoutConfig {
    override fun getClockMaskerConfig(): LayoutConfig.ClockMaskerLayoutSetup {
        return LayoutConfig.ClockMaskerLayoutSetup(140, 60, true)
    }

    override fun getLogoMaskerLayoutSetup(): LayoutConfig.LogoMaskerLayoutSetup {
        return LayoutConfig.LogoMaskerLayoutSetup(110, 110)
    }
}