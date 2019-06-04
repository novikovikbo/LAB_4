package com.example.pplab4;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Implementation of App Widget functionality.
 */
public class DayCounter extends AppWidgetProvider {
    static NotificationManager mManager;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.day_counter);

        long date = sharedPreferences.getLong(MainActivity.WIDGET_DATE, -1);
        if(date == -1){
            views.setTextViewText(R.id.appwidget_text, "-");
        }else{
            Calendar c1 = GregorianCalendar.getInstance();
            Calendar c2 = GregorianCalendar.getInstance();
            c2.setTimeInMillis(date);
            int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
            int days = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
            days += year * 365;
            if(days < 0){
                views.setTextViewText(R.id.appwidget_text, "-");
            }else{
                views.setTextViewText(R.id.appwidget_text, Integer.toString(days));
                if(days == 0){
                    showNotification(context);
                }else{
                    scheduleWakeUp(context);
                }
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);



        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);
        //String date = Long.toString(sharedPreferences.getLong(MainActivity.WIDGET_DATE, -1));

        onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, DayCounter.class)));

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    static void showNotification(Context context){
        String CHANNEL_ID = "com.example.pplab4.CHANNEL";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATIONS CHANNEL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.weekcalendar)
                .setContentTitle("Lab")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Count down reached zero"))
                .setAutoCancel(true);
        getManager(context).notify(12, builder.build());
    }

    static NotificationManager getManager(Context context) {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    static void scheduleWakeUp(Context context){
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DayCounter.class);
        PendingIntent pendingIntent =PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        String line = "Next wake up: " + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)) + ".";
        line += Integer.toString(calendar.get(Calendar.MONTH)) + ".";
        line += Integer.toString(calendar.get(Calendar.YEAR)) + " ";
        line += Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":00";
        Toast.makeText(context,line , Toast.LENGTH_SHORT).show();
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

