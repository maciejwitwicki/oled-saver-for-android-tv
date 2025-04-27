package com.mwi.oledsaver.alarm

enum class AlarmDelay(val message: String) {
    DELAY_1_MIN("1 minutę"),
    DELAY_5_MIN("5 minut"),
    DELAY_10_MIN("10 minut"),
    DELAY_1_H("1 godzinę"),
    DELAY_NEXT_DAY("do następnego dnia")
}
