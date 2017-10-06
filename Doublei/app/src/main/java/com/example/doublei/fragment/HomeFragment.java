package com.example.doublei.Fragment;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.doublei.MainFuction.BackgroundService;
import com.example.doublei.Etc.Singleton;
import com.example.doublei.Etc.Widget;
import com.example.doublei.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {
    public ImageButton btnEye;
    public Button btnConnect;
    public boolean value;
    public int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public long startTime, endTime, betweenTime;
    public int accumulateTime;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnConnect = (Button) view.findViewById(R.id.connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                buttonEvent();
            }
        });

        btnEye = (ImageButton) view.findViewById(R.id.eye);
        btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEvent();
            }
        });

        return view;
    }

    public void buttonEvent() {
        final Context context = getActivity().getApplicationContext();
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        value = Singleton.getInstance().getSwitchValue();

        if (value) {
            btnEye.setImageResource(R.drawable.trans_eyebuttonoff);
            ((TransitionDrawable) btnEye.getDrawable()).startTransition(500);
            btnConnect.setBackgroundResource(R.drawable.connect);

            Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 종료됩니다.", Toast.LENGTH_LONG).show();
            value = false;
            Singleton.getInstance().setSwitchValue(value);
            // 위젯을 위한 SharedPreference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", false);
            editor.commit();

            // 시간 계산을 위한 SharedPreference
            // 타임 종료

            endTime = System.currentTimeMillis();
            betweenTime = (endTime - startTime)/1000; // 초

            long date = System.currentTimeMillis(); // 시간받아오기
            Date nowDate = new Date(date); // Date 타입 변경
            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일"); // 월, 일 받아오기
            String dateTemp = sdf.format(nowDate);

            SharedPreferences timePref = this.getActivity().getSharedPreferences(dateTemp,Context.MODE_PRIVATE);
            SharedPreferences.Editor timeEditor = timePref.edit();
            String checkDate = timePref.getString(dateTemp,""); // key 값으로 dateTemp를 주고 그에 해당하는 Value를 가져옴. 없으면 공백("")

            accumulateTime = Integer.valueOf(checkDate);
            accumulateTime += Integer.valueOf(String.valueOf(betweenTime));

            timeEditor.putString(dateTemp, String.valueOf(accumulateTime));
            timeEditor.commit();
            String test = timePref.getString(dateTemp,"");
            Log.e("test",test);

            Widget.updateWidget(context, widgetManager, appWidgetId);

            getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
        } else {
            btnEye.setImageResource(R.drawable.trans_eyebuttonon);
            ((TransitionDrawable) btnEye.getDrawable()).startTransition(500);
            btnConnect.setBackgroundResource(R.drawable.disconnect);

            Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 실행됩니다.", Toast.LENGTH_LONG).show();
            value = true;
            Singleton.getInstance().setSwitchValue(value);

            // 위젯을 위한 SharedPreference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", true);
            editor.commit();

            // 시간 계산을 위한 SharedPreference
            long date = System.currentTimeMillis(); // 시간받아오기
            Date nowDate = new Date(date); // Date 타입 변경
            SimpleDateFormat sdf = new SimpleDateFormat("MM월 dd일"); // 월, 일 받아오기
            String dateTemp = sdf.format(nowDate);

            SharedPreferences timePref = this.getActivity().getSharedPreferences(dateTemp,Context.MODE_PRIVATE);
            SharedPreferences.Editor timeEditor = timePref.edit();
            String checkDate = timePref.getString(dateTemp,""); // key 값으로 dateTemp를 주고 그에 해당하는 Value를 가져옴. 없으면 공백("")

            // 오늘 첫 작동인 경우
            if(checkDate.equals("")){
                timeEditor.putString(dateTemp,"0"); // ID: 날짜 value = 0
                timeEditor.commit();
            }
            // 시간재기
            startTime = System.currentTimeMillis();
            /*
            else if(checkDate != null){
                // 오늘 날짜는 dateTemp
                // 현재시간 저장
                date = System.currentTimeMillis();
                nowDate = new Date(date);
                sdf = new SimpleDateFormat("HH:mm"); // HH 시 mm 분으로 나타냄
                String nowTime = sdf.format(nowDate); // 현재시간 스트링으로 변환

                timeEditor.putString("startTime",nowTime);
                timeEditor.commit();
            }
            */ // 삽질코드

            Widget.updateWidget(context, widgetManager, appWidgetId);

            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }
    }
}