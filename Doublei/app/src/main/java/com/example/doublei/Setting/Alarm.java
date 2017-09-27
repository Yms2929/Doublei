package com.example.doublei.Setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doublei.R;

import java.util.ArrayList;
import java.util.Calendar;

public class Alarm extends AppCompatActivity {
    private int hour;
    private int minute;
    private Spinner hourSpinner;
    private Spinner minSpinner;
    private Button btnAlarm;
    public ArrayList<String> hourList;
    public ArrayList<String> minList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        hourList = new ArrayList<>();
        minList = new ArrayList<>();

        for (int i = 1; i <= 24; i++) {
            hourList.add(Integer.toString(i));
        }
        for (int j = 0; j < 60; j++) {
            minList.add(Integer.toString(j));
        }

        hourSpinner = (Spinner) findViewById(R.id.hourspinner);
        minSpinner = (Spinner) findViewById(R.id.minspinner);

        initSpinner(hourSpinner, hourList);
        initSpinner(minSpinner, minList);

        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // 시
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                hour = Integer.parseInt(hourList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        minSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // 분
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                minute = Integer.parseInt(minList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnAlarm.setOnClickListener(new View.OnClickListener() { // 설정 완료 버튼
            @Override
            public void onClick(View view) {
                new AlarmSetting(getApplicationContext()).Alarm();
                Toast.makeText(getApplicationContext(), "알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class AlarmSetting {
        Context context;

        public AlarmSetting(Context context) {
            this.context = context;
        }

        public void Alarm() { // 알람 시작 함수
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // 미래에 대한 알림 행위 등록
            Intent intent = new Intent(Alarm.this, BroadcastD.class); // 알림이 발생했을 경우 방송을 해줌

            PendingIntent sender = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, minute, 0); // 알림시간 캘린더에 set하기

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender); // 알람 예약 실제 시간 기준 / 대기 상태일 경우 활성 상태로 전환한 후 작업 수행
        }
    }

    public void initSpinner(Spinner spinner, ArrayList<String> arrayList) { // 스피너 초기화
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}