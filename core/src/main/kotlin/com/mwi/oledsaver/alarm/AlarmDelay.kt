package com.mwi.oledsaver.alarm

/** How long masking stays dismissed. [minutes] is null for the "next day" option. */
enum class AlarmDelay(val minutes: Long?, val message: String) {
    DELAY_1_MIN(1, "1 minutę"),
    DELAY_5_MIN(5, "5 minut"),
    DELAY_10_MIN(10, "10 minut"),
    DELAY_1_H(60, "1 godzinę"),
    DELAY_NEXT_DAY(null, "do następnego dnia")
}
