package net.rhubarbdev.android.timetablegenerator.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDateTime

@Entity(tableName = "item_table")
class Item(
    @PrimaryKey(autoGenerate = true) val itemID: Int = 0,
    @ColumnInfo(name = "day") val day: DayOfWeek,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "start") val start: LocalDateTime,
    @ColumnInfo(name = "end") val end: LocalDateTime,
    @ColumnInfo(name = "colour") val colour: Int
)
