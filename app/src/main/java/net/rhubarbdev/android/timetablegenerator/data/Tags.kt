package net.rhubarbdev.android.timetablegenerator.data

import java.time.LocalDateTime

class Tags {
    data class ItemTag(
        val id : Int,
        val start : LocalDateTime,
        val end : LocalDateTime,
        val colour : Int
    )
}