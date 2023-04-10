package net.rhubarbdev.android.timetablegenerator.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.LocalDateTime

@Parcelize
data class ItemParcel(
    val day : DayOfWeek,
    val content : String = "",
    val start : LocalDateTime,
    val end : LocalDateTime,
    val colour : Int
) : Parcelable