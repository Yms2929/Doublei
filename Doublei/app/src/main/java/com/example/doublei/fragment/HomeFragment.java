package com.example.doublei.Fragment;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.doublei.BackgroundService;
import com.example.doublei.Etc.Singleton;
import com.example.doublei.Etc.Widget;
import com.example.doublei.R;

public class HomeFragment extends Fragment {
    public Button btnConnect;
    public boolean value;
    public Button btnEye;
    public int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

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

        btnEye = (Button) view.findViewById(R.id.eye);
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
            Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 종료됩니다.", Toast.LENGTH_LONG).show();

            value = false;
            Singleton.getInstance().setSwitchValue(value);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", false);
            editor.commit();

            Widget.updateWidget(context, widgetManager, appWidgetId);

            btnEye.setBackgroundResource(R.drawable.eyeoff);
            btnConnect.setBackgroundResource(R.drawable.disconnect);

            getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 실행됩니다.", Toast.LENGTH_LONG).show();
            value = true;
            Singleton.getInstance().setSwitchValue(value);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", true);
            editor.commit();

            Widget.updateWidget(context, widgetManager, appWidgetId);

            btnEye.setBackgroundResource(R.drawable.eyeon);
            btnConnect.setBackgroundResource(R.drawable.connect);

            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }
    }
}