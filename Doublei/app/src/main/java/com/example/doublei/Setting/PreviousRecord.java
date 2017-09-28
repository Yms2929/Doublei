package com.example.doublei.Setting;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.Toast;

import com.example.doublei.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;

public class PreviousRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_record);

        MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        calendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator(), new OneDayDecorator());

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() { // 달력 클릭 이벤트
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Toast.makeText(getApplicationContext(), String.valueOf(date.getMonth() + 1) + "월" + String.valueOf(date.getDay()) + "일", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class SundayDecorator implements DayViewDecorator { // 일요일
        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {

        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    public class SaturdayDecorator implements DayViewDecorator { // 토요일
        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {

        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    public class OneDayDecorator implements DayViewDecorator { // 오늘 날짜
        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.GREEN));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }
}
