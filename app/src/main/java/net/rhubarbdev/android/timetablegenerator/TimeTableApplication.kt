package net.rhubarbdev.android.timetablegenerator

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.rhubarbdev.android.timetablegenerator.data.ItemRepository
import net.rhubarbdev.android.timetablegenerator.data.ItemRoomDatabase

class TimeTableApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { ItemRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ItemRepository(database.itemDao()) }
}