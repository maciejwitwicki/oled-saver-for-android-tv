package com.mwi.oledsaver.config

import android.util.Range
import java.time.ZoneId
import java.time.ZonedDateTime

class ConfigProvider : LayoutConfig, ApplicationConfig {

    private val summerConfigDateRange = createSummerRange()
    private val operatingHoursRange: Range<ZonedDateTime> = createOperatingHoursRange()

    override fun isEnabled(): Boolean {
        val now = getNow()
        val start = operatingHoursRange.lower
        val end = operatingHoursRange.upper
        return now.isAfter(start) && now.isBefore(end)
    }

    override fun getOperatingRange(): Range<ZonedDateTime> {
        return operatingHoursRange
    }

    override fun getClockMaskerConfig(): LayoutConfig.ClockMaskerLayoutSetup {
        return getConfig().getClockMaskerConfig()
    }

    override fun getLogoMaskerLayoutSetup(): LayoutConfig.LogoMaskerLayoutSetup {
        return getConfig().getLogoMaskerLayoutSetup()
    }

    override fun getNow(): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
    }

    private fun getConfig(): LayoutConfig {
        val now = getNow()
        return if (now.isAfter(summerConfigDateRange.lower) && now.isBefore(summerConfigDateRange.upper)
        ) {
            SummerLayoutConfig()
        } else {
            DefaultLayoutConfig()
        }
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
        val end = getNow().withHour(12)
            .withMinute(0).withSecond(0)

        return Range(start, end)
    }

}
