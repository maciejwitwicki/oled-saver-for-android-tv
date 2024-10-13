package com.mwi.oledsaver.config

import android.util.Log
import android.util.Range
import com.mwi.oledsaver.MainActivity
import java.time.ZoneId
import java.time.ZonedDateTime

class ConfigProvider : LayoutConfig, ApplicationConfig {

    private val SummerConfigDateRange = createSummerRange()
    private val OperatingHoursRange = createOperatingHoursRange()

    override fun isEnabled(): Boolean {
        val now = getNow()
        val start = OperatingHoursRange.lower
        val end = OperatingHoursRange.upper
        Log.i(MainActivity.TAG, "Time is %s, operating hours are %s - %s".format(now, start, end))
        return now.isAfter(start) && now.isBefore(end)
    }

    override fun getClockMaskerConfig(): LayoutConfig.ClockMaskerLayoutSetup {
        return getConfig().getClockMaskerConfig()
    }

    override fun getLogoMaskerLayoutSetup(): LayoutConfig.LogoMaskerLayoutSetup {
        return getConfig().getLogoMaskerLayoutSetup()
    }

    private fun getConfig(): LayoutConfig {
        val now = getNow()
        return if (now.isAfter(SummerConfigDateRange.lower) && now.isBefore(SummerConfigDateRange.upper)
        ) {
            SummerLayoutConfig()
        } else {
            DefaultLayoutConfig()
        }
    }

    private fun getNow(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
    }

    private fun createSummerRange(): Range<ZonedDateTime> {
        val start = getNow().withMonth(6).withDayOfMonth(29)
            .withHour(0).withMinute(0).withSecond(0)

        val end = getNow().withMonth(9)
            .withHour(0).withMinute(0).withSecond(0)

        return Range(start, end)
    }

    private fun createOperatingHoursRange(): Range<ZonedDateTime> {
        val start = getNow().withHour(7)
            .withMinute(45).withSecond(0)
        val end = getNow().withHour(11)
            .withMinute(30).withSecond(0)

        return Range(start, end)
    }

}
