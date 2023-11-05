package com.mwi.oledsaver.config

class ConfigProvider : LayoutConfig {

    private var config = DefaultLayoutConfig();
//    private var config = SummerLayoutConfig();

    override fun getClockMaskerConfig(): LayoutConfig.ClockMaskerLayoutSetup {
        return config.getClockMaskerConfig();
    }

    override fun getLogoMaskerLayoutSetup(): LayoutConfig.LogoMaskerLayoutSetup {
        return config.getLogoMaskerLayoutSetup();
    }

}