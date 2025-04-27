package io.github.mamedovilkin.todoapp.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.ToDoAppActivity
import io.github.mamedovilkin.todoapp.util.CHANNEL_ID
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.REQUEST_CODE
import io.github.mamedovilkin.todoapp.util.WORK_MANAGER_INPUT_DATA_KEY

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val title = inputData.getString(WORK_MANAGER_INPUT_DATA_KEY)

        setForeground(createForegroundInfo(title.toString(), applicationContext))

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun createForegroundInfo(title: String, context: Context): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.resources.getString(R.string.notification_channel),
                importance
            )
            channel.description = context.resources.getString(R.string.notification_channel)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = createPendingIntent(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())

        return ForegroundInfo(NOTIFICATION_ID, builder.build())
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ToDoAppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        flags = flags or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            flags
        )
    }
}