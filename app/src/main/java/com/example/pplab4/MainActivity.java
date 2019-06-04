package com.example.pplab4;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    CalendarView calendarView;
    Button button;
    static long widget_date = -1;
    public static String PREFERENCES = "PREFERENCES";
    public static String WIDGET_DATE = "widget_date";
    public static String ACTIVITY_DATE = "activity_date";
    int _year = -1;
    int _month = -1;
    int _dayOfmonth  = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calendarView);
        button = findViewById(R.id.button);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        widget_date = sharedPreferences.getLong(WIDGET_DATE, -1);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( CalendarView view, int year, int month, int dayOfMonth) {
                _year = year;
                _month = month;
                _dayOfmonth = dayOfMonth;
                Log.i("CalendarView Update", Integer.toString(dayOfMonth)+ "." + Integer.toString(month) + "." + Integer.toString(year));

            }
        });

        if(widget_date > 0){
            calendarView.setDate(widget_date);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long selected_date = calendarView.getDate();
                Intent intent = new Intent(getApplicationContext(), DayCounter.class);
                intent.setAction(getResources().getString(R.string.intent_action));
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);


                Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(_year, _month, _dayOfmonth, 9, 0);
                sharedPreferences.edit().putLong(WIDGET_DATE, calendar.getTimeInMillis()).apply();
                Log.i("MainActivity put", Long.toString(calendar.getTimeInMillis()));
                sendBroadcast(intent);
                finish();
            }
        });



    }
}
