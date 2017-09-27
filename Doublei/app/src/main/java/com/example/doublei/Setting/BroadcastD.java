package com.example.doublei.Setting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.doublei.R;

/**
 * Created by Yookmoonsu on 2017-09-27.
 */

public class BroadcastD extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { // 알람 시간이 되었을때 onReceive를 호출
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Alarm.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM); // 링톤매니저 타입 알람
//        Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
//        ringtone.play();

        builder.setSmallIcon(R.drawable.timeout).setTicker("Alarm").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("더블아이 메세지").setContentText("사용시간이 종료 되었어요")
                .setContentIntent(pendingIntent).setAutoCancel(true)
                .setSound(ringtoneUri);

        notificationManager.notify(1, builder.build());
    }
}