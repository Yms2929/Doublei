package com.example.doublei.Etc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.doublei.MainFuction.BackgroundService;
import com.example.doublei.R;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    private static final String ACTION_WIDGET_RECEIVER = "ActionRecieverWidget";
    private static final String CLICK_ACTION = "com.example.doublei.CLICK";
    private RemoteViews views;
    private ComponentName updateAppWidget;
    public static boolean btnWidgetOn;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        views = new RemoteViews("com.example.doublei", R.layout.widget);
        //views = new RemoteViews("com.example.doublei", R.layout.widget);


        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, Widget.class);
            intent.setAction(ACTION_WIDGET_RECEIVER);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.imageBtn, pendingIntent);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean("widgetOn", false);

            if (value) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("widgetOn", false);
                editor.commit();
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);

        Log.e("intentValue",intent.getAction().toString());

        if(intent.getAction().equals(ACTION_WIDGET_RECEIVER)){
            // 위젯 버튼 클릭 시
            Log.e("press","onReceive 위젯 버튼");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean("widgetOn", true);
            Singleton.getInstance().setSwitchValue(value); // singleton 으로 value 변수 초기화
            Editor editor = prefs.edit();

            if (value) {
                btnWidgetOn = false;
            } else {
                btnWidgetOn = true;
            }

            if (btnWidgetOn) {
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                views.setImageViewResource(R.id.imageBtn, R.drawable.on);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);

                editor.putBoolean("widgetOn", true);
                editor.commit();

                Intent serviceIntent = new Intent(context, BackgroundService.class);
                context.startService(serviceIntent);

            } else {
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                views.setImageViewResource(R.id.imageBtn, R.drawable.off);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);

                editor.putBoolean("widgetOn", false);
                editor.commit();

                Intent serviceIntent = new Intent(context, BackgroundService.class);
                context.stopService(serviceIntent);
            }

            Singleton.getInstance().setSwitchValue(btnWidgetOn); // Singleton 변수 수정.
            updateAppWidget = new ComponentName( context, Widget.class );
            (AppWidgetManager.getInstance(context)).updateAppWidget( updateAppWidget, views );
        }
        else if(CLICK_ACTION.equals(intent.getAction())){
            // 앱 버튼 클릭 시
            Log.e("onReceive 앱 내 버튼","press");

            boolean value = Singleton.getInstance().getSwitchValue();

            if (value) {
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                views.setImageViewResource(R.id.imageBtn, R.drawable.on);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } else {
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                views.setImageViewResource(R.id.imageBtn, R.drawable.off);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            updateAppWidget = new ComponentName( context, Widget.class );
            (AppWidgetManager.getInstance(context)).updateAppWidget( updateAppWidget, views );
        }
        else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean value = prefs.getBoolean("widgetOn", false);

        if (value) {
            Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", false);
            editor.commit();
        }
        super.onDeleted(context, appWidgetIds);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
        Intent intent = new Intent();
        intent.setAction(CLICK_ACTION);
        context.sendBroadcast(intent);
    }
}

