package net.rhubarbdev.android.timetablegenerator.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationSchedule (var context: Context, var params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val data = params.inputData
        val title = "Title"
        val body = data.getString("body")

        TriggerNotification(context, title, body!!)

        return Result.success()
    }
}