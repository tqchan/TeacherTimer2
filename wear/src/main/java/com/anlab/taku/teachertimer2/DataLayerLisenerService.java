package com.anlab.taku.teachertimer2;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by taku on 2015/04/22.
 */
public class DataLayerLisenerService extends WearableListenerService {
    private static final String TAG = DataLayerLisenerService.class.getSimpleName();
    public static final String SETTING_TIME_PATH = "/setting/time";
    public static final String NOTICE_SETTING_TIME_PATH = "/notice/setting/time";
    public static final String CLASS_SETTING_TIME_PATH = "/class/setting/time";
    public static final String GOOD_BUTTON_PUSH = "/good/button/push";
    public static final String FINISH_BUTTON_PUSH = "/finish/button/push";
    public static final String START_WEAR_ACTIVITY = "/start/wear/activity";
    public static final String PLUS_TIME_PATH = "/plus/time";
    String wear_message;
    String good_text;

    int katei_time;
    int vibrate_time;
    int class_time;
    int plusTime;
    int reduceTime;
    int plusCount;
    int goodTime;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (START_WEAR_ACTIVITY.equals(messageEvent.getPath())) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "data received");
        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                switch (path) {
                    case SETTING_TIME_PATH:
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        wear_message = dataMap.getString("title");
                        katei_time = dataMap.getInt("time");
                        vibrate_time = 500;
                        Log.d(TAG, "kateitime" + katei_time);
                        intent.putExtra("message", wear_message);
                        intent.putExtra("vibrate_time", vibrate_time);
                        intent.putExtra("time", katei_time);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    case CLASS_SETTING_TIME_PATH:
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        wear_message = dataMap.getString("title");
                        katei_time = dataMap.getInt("time");
                        class_time = dataMap.getInt("classTime", 0);
                        Log.d(TAG, "kateitime" + katei_time);
                        intent.putExtra("message", wear_message);
                        intent.putExtra("timer_start", class_time);
                        intent.putExtra("time", katei_time);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    case NOTICE_SETTING_TIME_PATH:
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        wear_message = dataMap.getString("title");
                        vibrate_time = 100;
                        intent.putExtra("message", wear_message);
                        intent.putExtra("vibrate_time", vibrate_time);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    case GOOD_BUTTON_PUSH:
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        good_text = dataMap.getString("title");
                        goodTime = dataMap.getInt("goodTime", 0);
//                        vibrate_time = 200;
                        Log.d(TAG, "received" + good_text);
                        intent.putExtra("good", good_text);
                        intent.putExtra("goodTime", goodTime);
//                        intent.putExtra("vibrate_time", vibrate_time);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        break;
                    case PLUS_TIME_PATH:
                        dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        plusCount = dataMap.getInt("plusCount", 0);
                        plusTime = 60 * plusCount;
                        intent.putExtra("plusTime", plusTime);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        Log.d(TAG, "plus");
                        break;
                    case FINISH_BUTTON_PUSH:
                        intent.putExtra("finish", 0);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                        Log.d(TAG, "finish");
                        wear_message = null;
                        katei_time = 0;
                        class_time = 0;
                        break;
                }
//                if (path.equals(SETTING_TIME_PATH)) {
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    wear_message = dataMap.getString("title");
//                    katei_time = dataMap.getInt("time");
//                    vibrate_time = 500;
//                    Log.d(TAG, "kateitime" + katei_time);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("message", wear_message);
//                    intent.putExtra("vibrate_time", vibrate_time);
//                    intent.putExtra("time", katei_time);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                } else if (path.equals(CLASS_SETTING_TIME_PATH)) {
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    wear_message = dataMap.getString("title");
//                    katei_time = dataMap.getInt("time");
//                    class_time = dataMap.getInt("classTime", 0);
//                    Log.d(TAG, "kateitime" + katei_time);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("message", wear_message);
//                    intent.putExtra("timer_start", class_time);
//                    intent.putExtra("time", katei_time);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                } else if (path.equals(NOTICE_SETTING_TIME_PATH)) {
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    wear_message = dataMap.getString("title");
//                    vibrate_time = 100;
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("message", wear_message);
//                    intent.putExtra("vibrate_time", vibrate_time);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                } else if (path.equals(GOOD_BUTTON_PUSH)) {
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    good_text = dataMap.getString("title");
//                    vibrate_time = 200;
//                    Log.d(TAG, "received" + good_text);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("good", good_text);
//                    intent.putExtra("vibrate_time", vibrate_time);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                } else if (path.equals(FINISH_BUTTON_PUSH)) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("finish", 0);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                    Log.d(TAG, "finish");
//                } else if (path.equals(PLUS_TIME_PATH)) {
//                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//                    plusCount = dataMap.getInt("plusCount", 0);
//                    plusTime = 60 * plusCount;
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.putExtra("plusTime", plusTime);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                    Log.d(TAG, "plus");
//                }
            }
        }
    }
}
