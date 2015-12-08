package com.anlab.taku.teachertimer2;

/**
 * Created by taku on 2015/07/10.
 */
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import static android.view.View.*;
import static com.google.android.gms.wearable.MessageApi.*;


public class TimerActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = TimerActivity.class.getSimpleName();
    ArrayList<Integer> zikan_array;
    ArrayList<String> katei_array;
    ArrayList<String> goodTimeArray;

    CountDownTimer countDownTimer;

    TextView time_left;
    Button cdt_st;
    Button cdt_fi;
    Button good;
    Button plus1m;
    ScrollView scrollView;
    LinearLayout kateiLinear;

    int notification_time;
    int notification_time2;
    int notificationReduce;
    int notification_time_number = 0;
    int settingTime;
    int jugyou = 50 * 60;
    //    int jugyou = 120 * 60;
    int send_time;
    int zikan_old;
    int zikan_new;
    int tmp_reduceTime;
    int plusTime;
    int allPlusTime;
    int reduceTime;
    int allReduceTime;
    int kateiSize;
    int plusCount;
    int allPlusCount;
    int test_notification_time;
    int goodTime;

    long utc;

    Context mcontext;
    Intent viewIntent;
    PendingIntent pendingIntent;
    NotificationManagerCompat notificationManagerCompat;
    GoogleApiClient mGoogleApiClient;
    public static final String SETTING_TIME_PATH = "/setting/time";
    public static final String NOTICE_SETTING_TIME_PATH = "/notice/setting/time";
    public static final String CLASS_SETTING_TIME_PATH = "/class/setting/time";
    public static final String GOOD_BUTTON_PUSH = "/good/button/push";
    public static final String FINISH_BUTTON_PUSH = "/finish/button/push";
    public static final String PLUS_TIME_PATH = "/plus/time";
    String path;
    String handheldmessage;
    String notice_text;
    String kaisi;
    String settingtime_text;
    String good_text;
    String notification_title;
    String good_time;
    String goodText;
    String nowKateiText1;
    String nowKateiText2;
    String fewText1;
    String fewText2;
    String finishText;
    String zikan_old_text;
    String zikan_new_text;

    DataMap dataMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /**
         *
         * time_left         時間の残りを表示するテキスト
         * cdt_st            カウントダウン開始のボタン
         * cdt_fi            カウントダウン終了のボタン
         * katei_array       設定した過程名が入る配列
         * zikan_array       設定した時間が入る配列
         * countDownTimer    カウントダウンタイマーを生成
         *
         */

        time_left = (TextView) findViewById(R.id.timer_text);
        cdt_st = (Button) findViewById(R.id.cdt_start);
        cdt_fi = (Button) findViewById(R.id.cdt_finish);
        good = (Button) findViewById(R.id.iine_button);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        plus1m = (Button) findViewById(R.id.button4);

        cdt_st.setOnClickListener(cdt_stOnclickListener);
        cdt_fi.setOnClickListener(cdt_fiOnclickListener);
        good.setOnClickListener(goodOnClickListener);
//        plus1m.setOnClickListener(plusOnClickListener);

        katei_array = new ArrayList<>();
        zikan_array = new ArrayList<>();
        goodTimeArray = new ArrayList<>();

        utc = System.currentTimeMillis();

        Intent intent = getIntent();
        katei_array = intent.getStringArrayListExtra("katei");
        zikan_array = intent.getIntegerArrayListExtra("time");
        settingTime = intent.getIntExtra("alltime", 0);

        kateiSize = katei_array.size();
        Log.d(TAG, ""+kateiSize);

        //過程名の表示
        kateiLinear = new LinearLayout(this);
        kateiLinear.setOrientation(LinearLayout.VERTICAL);
        zikan_old = settingTime;
        for (int i = 0; i < kateiSize; i++) {
            TextView textView = new TextView(this);
            zikan_new = zikan_old - zikan_array.get(i);
            if (i == 0) {
                if (zikan_old / 60 >= 10) {
                    if (zikan_old % 60 >= 10) {
                        zikan_old_text = (zikan_old / 60) + ":" + (zikan_old % 60);
                    } else {
                        zikan_old_text = (zikan_old / 60) + ":0" + (zikan_old % 60);
                    }
                } else {
                    if (zikan_old % 60 >= 10) {
                        zikan_old_text = "0" + (zikan_old / 60) + ":" + (zikan_old % 60);
                    } else {
                        zikan_old_text = "0" + (zikan_old / 60) + ":0" + (zikan_old % 60);
                    }
                }
            }

            if (zikan_new / 60 >= 10){
                if ((zikan_new % 60) >= 10) {
                    zikan_new_text = (zikan_new / 60) + ":" + (zikan_new % 60);
                } else if ((zikan_new % 60) < 10) {
                    zikan_new_text = (zikan_new / 60) + ":0" + (zikan_new % 60);
                }
            } else {
                if ((zikan_new % 60) >= 10) {
                    zikan_new_text = "0" +  (zikan_new / 60) + ":" + (zikan_new % 60);
                } else if ((zikan_array.get(i) % 60) < 10) {
                    zikan_new_text = "0" + (zikan_new / 60) + ":0" + (zikan_new % 60);
                }
            }
            textView.setText(zikan_old_text + "-" + zikan_new_text + " : " + katei_array.get(i));
            zikan_old_text = zikan_new_text;
            kateiLinear.addView(textView);
        }
        scrollView.addView(kateiLinear);

        //count down timer 初期化
        countDownTimer = new MyCountDownTimer(settingTime * 1000, 1000);

        mcontext = this;

        goodText = getString(R.string.good);
        nowKateiText1 = getString(R.string.now);
        nowKateiText2 = getString(R.string._time);
        fewText1 = getString(R.string.few);
        fewText2 = getString(R.string.few_finish);
        finishText = getString(R.string.timeFinish);

        viewIntent = new Intent(this, TimerActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
        notificationManagerCompat = NotificationManagerCompat.from(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        plusTime = 0;
        reduceTime = 0;
        tmp_reduceTime = 0;
        allReduceTime = 0;
        plusCount = 0;
        allPlusTime = 0;
        allPlusCount = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        SendDataThread.interrupted();
        countDownTimer.cancel();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_stting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    OnClickListener cdt_stOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            countDownTimer.start();
            kaisi();
        }
    };

    OnClickListener cdt_fiOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dataMap = new DataMap();
            countDownTimer.onFinish();
            dataMap.putString("title", good_time);
            new SendDataThread(FINISH_BUTTON_PUSH, dataMap).start();
            timerFinish();
        }
    };

    OnClickListener goodOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dataMap = new DataMap();
            good_text = goodText + good_time;
            dataMap.putString("title", good_text);
            dataMap.putInt("goodTime", goodTime);
            new SendDataThread(GOOD_BUTTON_PUSH, dataMap).start();
            goodTimeArray.add(good_time);
        }
    };

    OnClickListener plusOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            plusCount = plusCount + 1;
            allPlusCount = allPlusCount + 1;
//            plusTime = tmp_zikan + (60 / (kateiSize - notification_time_number));
            plusTime = 60 * plusCount;
            if ((kateiSize - (notification_time_number + 1)) == 0) {
                reduceTime = 0;
            } else {
                reduceTime = tmp_reduceTime +  (plusTime / (kateiSize - (notification_time_number + 1)));
            }
            tmp_reduceTime = tmp_reduceTime + reduceTime;
            Log.d(TAG, "reduceTime" + reduceTime);
            dataMap = new DataMap();
            dataMap.putInt("plusCount", plusCount);
            dataMap.putLong("test", notification_time2);
            new SendDataThread(PLUS_TIME_PATH, dataMap).start();
//            Log.d(TAG, plusTime + "plus");
        }
    };

    private void timerFinish() {
        InputStream input;
        OutputStream output;
        String lineBuffer;
        String fileName;

        String folderPath = Environment.getExternalStorageDirectory() + "/Teachertimer/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        fileName = folderPath + utc + "log.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), "SJIS"));
            for (int i = 0; goodTimeArray.size() > i; i++) {
                bw.write(i);
                bw.write(goodTimeArray.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();
//            output = openFileOutput(fileName,MODE_WORLD_READABLE|MODE_APPEND);
//            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));
//
//            for (int i = 0; goodTimeArray.size() > i; i++) {
//                printWriter.append(goodTimeArray.get(i));
//            }
//
//            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //インターバル(1秒)毎に呼ばれる
            notification_time2 = (int) millisUntilFinished;
//            if (notification_time_number == 0) {
//                test_notification_time = zikan_array.get(notification_time_number);
//            } else {
//                test_notification_time = zikan_array.get(notification_time_number) - zikan_array.get(notification_time_number-1) - reduceTime;
//            }
//            Log.d(TAG, "test_notification_time" + test_notification_time);
            if (notification_time_number < kateiSize) {
                if (plusCount != 0) {
                    if (notification_time_number == 0) {
                        notification_time = zikan_array.get(notification_time_number) + plusTime;
                        Log.d(TAG, "notification 1 :" + notification_time);
                    } else {
                        notification_time = zikan_array.get(notification_time_number) + plusTime + allPlusTime - allReduceTime;
                        Log.d(TAG, "notification 2 :" + notification_time);
                    }
                } else {
                    if (zikan_array.get(notification_time_number) - allReduceTime <= 0) {
                        notification_time_number = notification_time_number + 1;
                        settingtime();
                        notificationReduce = notificationReduce + reduceTime;
                        Log.d(TAG, "notification 5 :");
                    } else {
                        notification_time = zikan_array.get(notification_time_number) + allPlusTime - allReduceTime;
                        Log.d(TAG, "notification 3 :" + notification_time);
                        Log.d(TAG, "notification 3 :zikan_array" + zikan_array.get(notification_time_number));
                        Log.d(TAG, "notification 3 :allPlusTime" + allPlusTime);
                        Log.d(TAG, "notification 3 :allReduceTime" + allReduceTime);
                    }
                }
            }
//            else if (notification_time_number + 1 == kateiSize) {
              else {
                notification_time = settingTime;
                Log.d(TAG, "notification 4 :" + notification_time);
            }
            if ((notification_time + plusTime - notificationReduce) >= settingTime) {
                plusTime = plusTime - 60;
                Toast.makeText(mcontext, "授業時間を超えてしまいます", Toast.LENGTH_LONG).show();
            }
//            if (zikan_array.size() > notification_time_number) {
//                notification_time = zikan_array.get(notification_time_number);
//                        Log.d(TAG, "notification :" + notification_time);
//            } else if (zikan_array.size() == notification_time_number) {
//                notification_time = settingTime;
//            }
            keikazikan(millisUntilFinished);

//            if (notification_time2 != 0) {
//                if ((settingTime - (millisUntilFinished / 1000)) == notification_time2) {
//                    if (notification_time_number < kateiSize) {
//                        notification_time_number = notification_time_number + 1;
//                        //設定した時間のmessage
//                        settingtime();
//                    } else if (notification_time_number == kateiSize) {
//                        settingtime();
//                    }
//                } else if ((settingTime - (millisUntilFinished / 1000)) == (notification_time2  - 60)) {
//                    //設定した1分前
//                    notice_settingtime();
//                }
//            } else {
                if ((settingTime - (millisUntilFinished / 1000)) == notification_time) {
                    if (notification_time_number < kateiSize) {
                        notification_time_number = notification_time_number + 1;
//                        notificationReduce = reduceTime * notification_time_number;
                        Log.d(TAG, "notificationReduce" + notificationReduce);
                        allPlusTime = 60 * allPlusCount;
                        allReduceTime = allReduceTime + reduceTime;
                        Log.d(TAG, "allPlusTime" + allPlusTime);
                        Log.d(TAG, "allPlusCount" + allPlusCount);
                        plusCount = 0;
                        //設定した時間のmessage
                        settingtime();
                    } else if (notification_time_number == kateiSize) {
                        plusCount = 0;
                        settingtime();
                    }
                } else if ((settingTime - (millisUntilFinished / 1000)) == (notification_time  - 60)) {
                    //設定した1分前
                    notice_settingtime();
                }
//            }
            //残り時間表示
            if ((millisUntilFinished / 1000 / 60) >= 10) {
                if ((millisUntilFinished / 1000 % 60) >= 10) {
                    time_left.setText(Long.toString(millisUntilFinished / 1000 / 60) + ":" + Long.toString(millisUntilFinished / 1000 % 60));
                } else if ((millisUntilFinished / 1000 % 60) < 10) {
                    time_left.setText(Long.toString(millisUntilFinished / 1000 / 60) + ":0" + Long.toString(millisUntilFinished / 1000 % 60));
                }
            } else if ((millisUntilFinished / 1000 / 60) < 10) {
                if ((millisUntilFinished / 1000 % 60) >= 10) {
                    time_left.setText("0" + Long.toString(millisUntilFinished / 1000 / 60) + ":" + Long.toString(millisUntilFinished / 1000 % 60));
                } else if ((millisUntilFinished / 1000 % 60) < 10) {
                    time_left.setText("0" + Long.toString(millisUntilFinished / 1000 / 60) + ":0" + Long.toString(millisUntilFinished / 1000 % 60));
                }
            }
        }

        @Override
        public void onFinish() {
            countDownTimer.cancel();
            time_left.setText("00:00");
        }
    }

    private void keikazikan(long millisUntilFinished) {
        long tmp_keikazikan;
        tmp_keikazikan = (settingTime - (millisUntilFinished/1000));
        goodTime = (int) tmp_keikazikan;
        Log.d(TAG, ""+tmp_keikazikan);
        if (tmp_keikazikan / 60 >= 10){
            if ((tmp_keikazikan % 60) >= 10) {
                good_time = (tmp_keikazikan / 60) + ":" + (tmp_keikazikan % 60);
            } else if ((tmp_keikazikan % 60) < 10) {
                good_time = (tmp_keikazikan / 60) + ":0" + (tmp_keikazikan % 60);
            }
        } else if (tmp_keikazikan / 60 < 9) {
            if ((tmp_keikazikan % 60) >= 10) {
                good_time = "0" +  (tmp_keikazikan / 60) + ":" + (tmp_keikazikan % 60);
            } else if ((tmp_keikazikan % 60) < 10) {
                good_time = "0" + (tmp_keikazikan / 60) + ":0" + (tmp_keikazikan % 60);
            }
        }
    }

    private void kaisi() {
        dataMap = new DataMap();
        notification_title = katei_array.get(notification_time_number);
        kaisi = nowKateiText1 + notification_title + nowKateiText2;
        dataMap.putString("title", kaisi);
        dataMap.putInt("time", zikan_array.get(notification_time_number));
        dataMap.putInt("classTime", settingTime);
        new SendDataThread(CLASS_SETTING_TIME_PATH, dataMap).start();
    }

    private void notice_settingtime() {
        dataMap = new DataMap();
        if (kateiSize > notification_time_number) {
            notification_title = katei_array.get(notification_time_number);
            notice_text = fewText1 + notification_title + fewText2;
            dataMap.putString("title", notice_text);
            new SendDataThread(NOTICE_SETTING_TIME_PATH, dataMap).start();
        }
    }

    private void settingtime() {
        dataMap = new DataMap();
        Log.d(TAG, "" + notification_time_number);
        if (kateiSize > notification_time_number) {
            send_time = zikan_array.get(notification_time_number) - zikan_array.get(notification_time_number-1) - reduceTime;
            notification_title = katei_array.get(notification_time_number);
            settingtime_text = nowKateiText1 + notification_title + nowKateiText2;
            dataMap.putString("title", settingtime_text);
            dataMap.putInt("time", send_time);
        } else if (kateiSize == notification_time_number) {
            settingtime_text = finishText;
            dataMap.putString("title", settingtime_text);
            dataMap.putInt("time", (settingTime - zikan_array.get(notification_time_number-1)));
        }
        new SendDataThread(SETTING_TIME_PATH, dataMap).start();
    }


    class SendDataThread extends Thread {
        DataMap tmp_datamap;
        public SendDataThread(String pth, DataMap message) {
            path = pth;
            tmp_datamap = message;
        }

        public void run() {
            Log.d(TAG, "senddata thread start");
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
                putDataMapRequest.getDataMap().putAll(dataMap);
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient,request).await();

                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    Log.d(TAG, "ERROR");
                }
            }
        }
    }

}