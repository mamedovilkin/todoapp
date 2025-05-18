package io.github.mamedovilkin.todoapp.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.mamedovilkin.todoapp.R
import io.github.mamedovilkin.todoapp.ui.ToDoAppActivity
import io.github.mamedovilkin.todoapp.util.CHANNEL_ID
import io.github.mamedovilkin.todoapp.util.NOTIFICATION_ID
import io.github.mamedovilkin.todoapp.util.REQUEST_CODE
import io.github.mamedovilkin.todoapp.util.TITLE_KEY

class TaskReminderReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val title = intent.getStringExtra(TITLE_KEY).toString()

            createNotification(title, context)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun createNotification(title: String, context: Context) {
        createNotificationChannelIfNeeded(context)

        val pendingIntent: PendingIntent = createPendingIntent(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_task)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannelIfNeeded(context: Context) {
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