package com.mwi.oledsaver.config

class SummerLayoutConfig : LayoutConfig {
    override fun getClockMaskerConfig(): LayoutConfig.ClockMaskerLayoutSetup {
        return LayoutConfig.ClockMaskerLayoutSetup(200, 0, false)
    }

    override fun getLogoMaskerLayoutSetup(): LayoutConfig.LogoMaskerLayoutSetup {
        return LayoutConfig.LogoMaskerLayoutSetup(160, 170)
    }
}