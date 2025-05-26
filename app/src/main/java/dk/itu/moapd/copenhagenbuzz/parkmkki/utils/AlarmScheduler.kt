package dk.itu.moapd.copenhagenbuzz.parkmkki.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import dk.itu.moapd.copenhagenbuzz.parkmkki.receivers.EventAlarmReceiver
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event

object AlarmScheduler {

    fun scheduleAlarm(context: Context, eventKey: String, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt user to allow "Schedule exact alarms" in system settings
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:${context.packageName}")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }
        }

        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra("EVENT_TITLE", event.eventName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventKey.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            event.eventDate,
            pendingIntent
        )
    }

    fun cancelAlarm(context: Context, eventKey: String) {
        val intent = Intent(context, EventAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventKey.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
