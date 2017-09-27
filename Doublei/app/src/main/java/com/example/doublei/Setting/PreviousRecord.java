package com.example.doublei.Setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.doublei.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class PreviousRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_record);

        MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

    }
}