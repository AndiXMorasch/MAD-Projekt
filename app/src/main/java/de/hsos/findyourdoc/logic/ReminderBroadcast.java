package de.hsos.findyourdoc.logic;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.hsos.findyourdoc.R;
import de.hsos.findyourdoc.storage.DatabaseHelper;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        String username = intent.getStringExtra("username");
        String docName = intent.getStringExtra("docname");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        databaseHelper.updateNotificationStatus(docName, 1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyAboutAppointment")
                .setSmallIcon(R.drawable.ic_baseline_access_time_24)
                .setContentTitle("Hey " + username + "! You'll have an appointment soon!")
                .setContentText(docName + " is waiting for your appointment at " + date + " " + time + "! :-)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "notifyAboutAppointment";
            String description = "You'll have an appointment soon.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyAboutAppointment", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setUpAlarms(DatabaseHelper databaseHelper, Context context, String username) {
        Cursor cursor = databaseHelper.getDataCursorDocTable();

        int i = 0;
        while (cursor.moveToNext()) {
            int wasNotified = cursor.getInt(5);
            if (wasNotified == 1) {
                continue;
            }

            Intent intent = new Intent(context, ReminderBroadcast.class);

            String docname = cursor.getString(0);
            String date = cursor.getString(2);
            String time = cursor.getString(3);
            int remindTime = cursor.getInt(4);

            intent.putExtra("username", username);
            intent.putExtra("docname", docname);
            intent.putExtra("date", date);
            intent.putExtra("time", time);

            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            String dateAndTime = date + " " + time;
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }

            long millis = 0;
            if (localDateTime != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                }
            } else {
                return;
            }

            if (millis - System.currentTimeMillis() > 0) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, millis - remindTime - 60000 * 60, pendingIntent);
            }
            i++;
        }
    }
}
