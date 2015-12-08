package com.anlab.taku.teachertimer2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements Runnable {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextView;
    FrameLayout frameLayout;
    ViewGroup.LayoutParams params;

    static int strokeWeight = 12;
    static int viewWidth;
    static int viewHeight;
    static int allClassTime = 0;
    static int eachClassTime = 0;
    static int nowAllClassTime = 0;
    static int nowEachClassTime = 0;
    static int plusTime = 0;
//    static int goodTime = 0;
    int class_time = 0;
    int vibrate_time = 0;
    int finish_time = 1;
    int now_katei_time = 1;
    int tmp_now_katei_time;
    int tmp_plusTime;
    int goodTime;
    static int goodTimesArrayListNo = 0;

    long finish_vibrate[] = {0, 800, 50, 800};
    long good_vibrate[] = {0, 200, 100, 50, 100, 50};
    long plus_vibrate[] = {0, 100, 50, 100};

    static ArrayList<Integer> goodTimesArrayList;

    String receive_message = null;
    String receive_message_good = null;
    String message = null;
    String message_good = null;

    static Thread mThread;

    static boolean isRunning;

    Handler mHandler;

    WindowManager wm;
    Display disp;

    MainActivity mainActivity;
    static ProgressView progressView;

    Vibrator mVibrator;

    IntentFilter mIntentFilter;

    static boolean drawRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                frameLayout = (FrameLayout) stub.findViewById(R.id.frameProgress);
            }
        });
        goodTimesArrayList = new ArrayList<>();
        mThread = new Thread(this);
        mHandler = new Handler();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        disp = wm.getDefaultDisplay();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        mIntentFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(messageReceiver, mIntentFilter);
        drawRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mThread = null;
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mThread.interrupt();
        this.finish();
        Log.d(TAG, "pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThread = null;
        this.finish();
        Log.d(TAG, "destroy");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        viewWidth = frameLayout.getWidth();
        viewHeight = frameLayout.getHeight();
        Log.d(TAG, "width:" + frameLayout.getWidth() + "height:" + frameLayout.getHeight());
        Log.d(TAG, "width:" + viewWidth + "height:" + viewHeight);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressView = new ProgressView(this);
        progressView.setLayoutParams(params);
        frameLayout.addView(progressView);
    }

    public void timeSet() {
        if (nowEachClassTime == 0) {
            nowEachClassTime = eachClassTime;
        }
        if (nowAllClassTime == 0) {
            nowAllClassTime = allClassTime;
        }
    }

    @Override
    public void run() {
        while (isRunning == true) {
            nowAllClassTime--;
            nowEachClassTime--;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressView.invalidate();
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nowAllClassTime <= 0) {
//                mVibrator.vibrate(finish_vibrate, -1);
                mVibrator.vibrate(vibrate_time);
                isRunning = false;
            } else {
                if (nowEachClassTime <= 0) {
                    nowEachClassTime = eachClassTime;
                }
            }
            Log.d(TAG, "thread all:" + nowAllClassTime + "::each:" + nowEachClassTime);
        }
        mThread.interrupt();
    }

    public static class ProgressView extends View {
        //        public class ProgressView extends View {
        int _size;
        int _disp;
        int blank_size;
        int textSize;

        public ProgressView(Context context) {
            super(context);
            setting();
        }

        public ProgressView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setting();
        }

        public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setting();
        }


        public void setting() {
            setSizeCalc();
            setMax();
            blankCalc();
        }


        private void setSizeCalc() {
//            Log.d("ProgressView", MainActivity.viewWidth + ":" + MainActivity.viewHeight);
            if (MainActivity.viewWidth > MainActivity.viewHeight) {
                _size = MainActivity.viewHeight;
                _disp = MainActivity.viewWidth - MainActivity.viewHeight;
            } else {
                _size = MainActivity.viewWidth;
                _disp = MainActivity.viewHeight - MainActivity.viewWidth;
            }
        }

        private void setMax() {

        }

        private void blankCalc() {
            blank_size = _disp / 2;
            textSize = _size / 4;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            allClass(canvas);
            eachClass(canvas);
            nowAllClass(canvas);
            nowEachClass(canvas);
            nowEachClassText(canvas);
            nowAllClassText(canvas);
            goodMark(canvas);
        }

        private void allClass(Canvas canvas) {
            RectF rect = new RectF(blank_size + MainActivity.strokeWeight, MainActivity.strokeWeight, blank_size + _size - MainActivity.strokeWeight, _size - MainActivity.strokeWeight);
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(MainActivity.strokeWeight);
            canvas.drawArc(rect, 270, 360, false, paint);
        }

        private void eachClass(Canvas canvas) {
            RectF rect = new RectF(blank_size + (MainActivity.strokeWeight * 2), MainActivity.strokeWeight * 2, blank_size + _size - (MainActivity.strokeWeight * 2), _size - (MainActivity.strokeWeight * 2));
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(MainActivity.strokeWeight);
            canvas.drawArc(rect, 270, 360, false, paint);
        }

        private void nowAllClass(Canvas canvas) {
            int calcNowAllClassTime;
            try {
                calcNowAllClassTime = -360 * MainActivity.nowAllClassTime / MainActivity.allClassTime;
            } catch (ArithmeticException e) {
                calcNowAllClassTime = 0;
            }
            RectF rect = new RectF(blank_size + MainActivity.strokeWeight, MainActivity.strokeWeight, blank_size + _size - MainActivity.strokeWeight, _size - MainActivity.strokeWeight);
            Paint paint = new Paint();
            paint.setColor(Color.MAGENTA);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(MainActivity.strokeWeight);
            canvas.drawArc(rect, 270, calcNowAllClassTime, false, paint);
        }

        private void nowEachClass(Canvas canvas) {
            int calcNowEachClassTime;
            try {
                calcNowEachClassTime = -360 * MainActivity.nowEachClassTime / MainActivity.eachClassTime;
            } catch (ArithmeticException e) {
                calcNowEachClassTime = 0;
            }
            RectF rect = new RectF(blank_size + (MainActivity.strokeWeight * 2), MainActivity.strokeWeight * 2, blank_size + _size - (MainActivity.strokeWeight * 2), _size - (MainActivity.strokeWeight * 2));
            Paint paint = new Paint();
            paint.setColor(Color.CYAN);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(MainActivity.strokeWeight);
            canvas.drawArc(rect, 270, calcNowEachClassTime, false, paint);
        }

        private void nowEachClassText(Canvas canvas) {
            String _text;
            if ((MainActivity.nowEachClassTime / 60) >= 10) {
                if (MainActivity.nowEachClassTime % 60 >= 10) {
                    _text = Long.toString(MainActivity.nowEachClassTime / 60) + ":" + Long.toString(MainActivity.nowEachClassTime % 60);
                } else {
                    _text = Long.toString(MainActivity.nowEachClassTime / 60) + ":0" + Long.toString(MainActivity.nowEachClassTime % 60);
                }
            } else {
                if (MainActivity.nowEachClassTime % 60 >= 10) {
                    _text = "0" +  Long.toString(MainActivity.nowEachClassTime / 60) + ":" + Long.toString(MainActivity.nowEachClassTime % 60);
                } else {
                    _text = "0" +  Long.toString(MainActivity.nowEachClassTime / 60) + ":0" + Long.toString(MainActivity.nowEachClassTime % 60);
                }
            }
            int _width = blank_size + (_size / 2);
            int _height = MainActivity.strokeWeight + (_size / 2);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(_text, _width, _height, paint);
        }

        private void nowAllClassText(Canvas canvas) {
            String _text;
            if ((MainActivity.nowAllClassTime / 60) >= 10) {
                if (MainActivity.nowAllClassTime % 60 >= 10) {
                    _text = Long.toString(MainActivity.nowAllClassTime / 60) + ":" + Long.toString(MainActivity.nowAllClassTime % 60);
                } else {
                    _text = Long.toString(MainActivity.nowAllClassTime / 60) + ":0" + Long.toString(MainActivity.nowAllClassTime % 60);
                }
            } else {
                if (MainActivity.nowAllClassTime % 60 >= 10) {
                    _text = "0" +  Long.toString(MainActivity.nowAllClassTime / 60) + ":" + Long.toString(MainActivity.nowAllClassTime % 60);
                } else {
                    _text = "0" +  Long.toString(MainActivity.nowAllClassTime / 60) + ":0" + Long.toString(MainActivity.nowAllClassTime % 60);
                }
            }
            int _width = blank_size + (_size / 2);
            int _height = MainActivity.strokeWeight + (_size / 2) + (textSize / 2);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize / 2);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(_text, _width, _height, paint);
        }

        private void goodMark(Canvas canvas) {
            int goodPoints;
            int goodPointsEnd;
            RectF rect = new RectF(blank_size + MainActivity.strokeWeight, MainActivity.strokeWeight, blank_size + _size - MainActivity.strokeWeight, _size - MainActivity.strokeWeight);
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(MainActivity.strokeWeight);

//            while (drawRunning == true) {
//                int i = 0;
//                goodPoints = 360 * goodTimesArrayList.get(i) / MainActivity.allClassTime;
//                goodPointsEnd = 360 * (goodTimesArrayList.get(i) + 1) / MainActivity.allClassTime;
//                Log.d(TAG, "goodPoints" + goodPoints);
//                Log.d(TAG, "goodPointsEnd" + goodPointsEnd);
//                if (i < goodTimesArrayListNo) {
//                    canvas.drawArc(rect, 270 + goodPoints, goodPointsEnd, false, paint);
//                    i++;
//                } else {
//                    drawRunning = false;
//                }
//            }
            for (int i = 0; i < goodTimesArrayList.size(); i++) {
                goodPoints = 360 * goodTimesArrayList.get(i) / MainActivity.allClassTime;
//                goodPointsEnd = 360 * (goodTimesArrayList.get(i) + 1) / MainActivity.allClassTime;
                canvas.drawArc(rect, 270 + goodPoints, 2, false, paint);
                Log.d(TAG, "goodTimesArrayList" + goodTimesArrayList.size());
                Log.d(TAG, "goodPoints" + goodPoints);
//                Log.d(TAG, "goodPointsEnd" + goodPointsEnd);
            }
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            receive_message = intent.getStringExtra("message");
            receive_message_good = intent.getStringExtra("good");
            class_time = intent.getIntExtra("timer_start", 0);
            vibrate_time = intent.getIntExtra("vibrate_time", 100);
            finish_time = intent.getIntExtra("finish", 0);
            tmp_now_katei_time = intent.getIntExtra("time", -1);
            plusTime = intent.getIntExtra("plusTime", 0);
            goodTime = intent.getIntExtra("goodTime", 0);
            if (tmp_now_katei_time != -1) {
                now_katei_time = intent.getIntExtra("time", 0);
            }
            Log.d("MessageReceive", "receivemessage" + receive_message);
            Log.d("MessageReceive", "katei_receivemessage" + now_katei_time);
            if (class_time != 0) {
                Log.d(TAG, "testtesttest");
                MainActivity.allClassTime = class_time;
                MainActivity.nowAllClassTime = MainActivity.allClassTime;
                if (MainActivity.mThread.getState() != Thread.State.RUNNABLE) {
                    isRunning = true;
                    MainActivity.mThread.start();
                }
                tmp_plusTime = 0;
//                class_time = 0;
            }
            if (receive_message != null) {
                message = receive_message;
                if (tmp_now_katei_time != -1) {
                    MainActivity.eachClassTime = now_katei_time;
                    MainActivity.nowEachClassTime = MainActivity.eachClassTime;
                }
                receive_message = null;
                Log.d("MessageReceive", "receivemessage" + receive_message);
                changeVibrate();
            } else if (receive_message_good != null) {
                message_good = receive_message_good;
                if (goodTime != 0) {
                    goodTimesArrayList.add(goodTime);
                }
                drawRunning = true;
                goodTimesArrayListNo++;
                receive_message_good = null;
                goodVibrate();
            } else if (plusTime != 0) {
                MainActivity.eachClassTime = MainActivity.eachClassTime - tmp_plusTime + plusTime;
                MainActivity.nowEachClassTime = MainActivity.nowEachClassTime - tmp_plusTime + plusTime;
                tmp_plusTime = plusTime;
                Log.d("plustime", "" + plusTime);
                plusVibrate();
            } else if (finish_time == 0) {
                Log.d("TAG", "finish_time");
                MainActivity.nowAllClassTime = 0;
                finish_time = 1;
                finishVibrate();
                finish();
                moveTaskToBack(true);
            }
            else {
                receive_message = "No Message";
            }
            setScreen();
        }
    }

    private void setScreen() {
        if (message != null) {
            mTextView.setText(message);
            message = null;
        } else if (message_good != null) {
//            good_TextView.setText(message_good);
            message_good = null;
            Log.d(TAG,"good");
        }
//        vibrate();
    }

    private void changeVibrate() {
//        mVibrator.vibrate(vibrate_time);
        mVibrator.vibrate(finish_vibrate, -1);
    }

    private void plusVibrate() {
        mVibrator.vibrate(plus_vibrate, -1);
    }

    private void goodVibrate() {
        mVibrator.vibrate(good_vibrate, -1);
    }

    private void finishVibrate() {
//        mVibrator.vibrate(finish_vibrate, -1);
        mVibrator.vibrate(vibrate_time);
    }

}
