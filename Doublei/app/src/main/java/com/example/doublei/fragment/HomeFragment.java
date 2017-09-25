package com.example.doublei.Fragment;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doublei.BackgroundService;
import com.example.doublei.R;
import com.example.doublei.Singleton;
import com.example.doublei.Widget;

public class HomeFragment extends Fragment {
    ImageView imageView;
    public boolean value;
    Button btnFace;
    Button ToastImage;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        imageView=(ImageView) view.findViewById(R.id.faceimage);
        btnFace = (Button) view.findViewById(R.id.eye);
//        ToastImage = (Button) view.findViewById(R.id.toast);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = getActivity().getApplicationContext();
                AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
                value = Singleton.getInstance().getSwitchValue();

                if(value){
                    Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 종료됩니다.",Toast.LENGTH_LONG).show();

                    value = false;
                    Singleton.getInstance().setSwitchValue(value);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("widgetOn", false);
                    editor.commit();

                    Widget.updateWidget(context,widgetManager,appWidgetId);

                    imageView.setImageResource(R.drawable.trans_girloff);
                    ((TransitionDrawable) imageView.getDrawable()).startTransition(1000);

                    getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "백그라운드 작업이 실행됩니다.",Toast.LENGTH_LONG).show();
                    value = true;
                    Singleton.getInstance().setSwitchValue(value);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("widgetOn", true);
                    editor.commit();

                    Widget.updateWidget(context,widgetManager,appWidgetId);

                    imageView.setImageResource(R.drawable.trans_girlon);
                    ((TransitionDrawable) imageView.getDrawable()).startTransition(1000);

                    getActivity().startService(new Intent(getActivity(), BackgroundService.class));
                }
            }
        });

//        ToastImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showToast();
//            }
//        });

        return view;
    }

    private void showToast() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_closeeye,null);
        Toast toast = new Toast(getActivity());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
}