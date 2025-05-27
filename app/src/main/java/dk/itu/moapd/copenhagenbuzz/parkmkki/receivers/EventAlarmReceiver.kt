package dk.itu.moapd.copenhagenbuzz.parkmkki.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dk.itu.moapd.copenhagenbuzz.parkmkki.R

class EventAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("EVENT_TITLE") ?: "Event Reminder"

        val channelId = "event_channel"
        val channelName = "Event Notifications"
        val channelDescription = "Notifications for upcoming events"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.w("EventAlarmReceiver", "Missing POST_NOTIFICATIONS permission. Skipping notification.")
                return
            }
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val builder = NotificationCompat.Builder(context, "event_channel")
            .setSmallIcon(R.drawable.baseline_add_alert_24)
            .setContentTitle("Event Reminder")
            .setContentText("Your event \"$title\" is starting soon!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(title.hashCode(), builder.build())
        }
    }
}
