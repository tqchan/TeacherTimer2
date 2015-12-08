package com.anlab.taku.teachertimer2;

/**
 * Created by taku on 2015/07/10.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    LinearLayout linearLayout;
    LinearLayout add_linearLayout;
    Button add;
    Button remove;
    Button add_finish;
    LayoutInflater inflater;
    View content;
    AlertDialog.Builder builder;
    NumberPicker numberPicker1,numberPicker2,numberPicker3,numberPicker4;
    EditText kateimei;
    TextView add_katei;
    TextView add_time;
    ViewGroup viewGroup;
    TableRow tableRow;
    TableLayout tableLayout;

    String[] minutes = new String[] {"0","3"};
    String katei;
    String zikan;
    String zikan_old;

    int time;
    int time_old = 0;
    int add_kazu = 0;

    ArrayList<Integer> zikan_array;
    ArrayList<String> katei_array;
    Context mcontext;
    GoogleApiClient mGoogleApiClient;

    public static final String START_WEAR_ACTIVITY = "/start/wear/activity";
    public static final String ALL_CLASS_TIME = "/all/class/time";

    int settingTime;

    String dialogTimeText;
    String dialogMissText;
    String wantTiem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *
         * linearLayout
         * inflater         レイアウトのinflater
         * add_linearLayout 追加するLinearLayout
         * add_katei        追加する過程名
         * add_time         追加する過程の時間
         * tableLayout      ダイアログの結果を表示するテーブル
         * add              追加ボタン
         * add_finish       完了ボタン
         * remove           削除ボタン
         * katei_array      過程名を格納する配列
         * zikan_array      時間を格納する配列
         * mcontext         this
         * viewGroup        tablelayoutのviewgroup
         * zikan_old        設定した前の時間
         *
         */

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_linearLayout = (LinearLayout)inflater.inflate(R.layout.add_layout, null);
        add_katei = (TextView)add_linearLayout.findViewById(R.id.text_katei);
        add_time = (TextView)add_linearLayout.findViewById(R.id.text_time);
        tableLayout = (TableLayout)findViewById(R.id.table);
        add = (Button)findViewById(R.id.add_button);
        add_finish = (Button)findViewById(R.id.end);
        remove = (Button)findViewById(R.id.remove_button);
        katei_array = new ArrayList<String>();
        zikan_array = new ArrayList<Integer>();
        mcontext = this;
        dialogTimeText = getString(R.string.classtimesetting);
        dialogMissText = getString(R.string.missdialog);
        wantTiem = getString(R.string.wanttime);

        //table layout のグループ取得
        viewGroup = (ViewGroup)findViewById(R.id.table);

        add.setOnClickListener(addOnClickListener);
        add_finish.setOnClickListener(add_finishOnClickListener);
        remove.setOnClickListener(removeOnClickListener);

        zikan_old = "00:00";

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Intent intent = getIntent();
        settingTime = intent.getIntExtra("settingtime", 0);
        if (settingTime == 0) {
            Toast.makeText(mcontext, dialogTimeText, Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(MainActivity.this, TimeSttingActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        /**
         * ダイアログの準備
         * numberPickerは左から1,2,3,4
         * 秒数を30区切りにするためにnumberPicker3は1と0のみにして，配列を対応
         */

        //カスタムビューを設定
        content = inflater.inflate(R.layout.dialog, (ViewGroup)findViewById(R.id.dialog));
        //アラートダイアログを生成
        builder = new AlertDialog.Builder(this);
        numberPicker1 = (NumberPicker) content.findViewById(R.id.numberPicker);
        numberPicker2 = (NumberPicker) content.findViewById(R.id.numberPicker2);
        numberPicker3 = (NumberPicker) content.findViewById(R.id.numberPicker3);
        numberPicker4 = (NumberPicker) content.findViewById(R.id.numberPicker4);
        numberPicker1.setMaxValue(5);
        numberPicker1.setMinValue(0);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(1);
        numberPicker3.setMinValue(0);
        numberPicker3.setDisplayedValues(minutes);

        //キーボードブロック
        numberPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        builder.setView(content)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //時間の取得
                        time = (numberPicker1.getValue() * 600) + (numberPicker2.getValue() * 60) + (numberPicker3.getValue() * 30);
                        if (time > time_old) {
                            //指導過程名取得&配列に
                            kateimei = (EditText)content.findViewById(R.id.edit_title);
                            katei = kateimei.getText().toString();
                            katei_array.add(katei);
                            //時間を配列に&判別のために時間を保存
                            time_old = time;
                            zikan_array.add(time);
                            Log.d(TAG, katei + time);
                            //取得した値をセット
                            add_katei.setText(katei);
                            if (numberPicker3.getValue() == 1) {
                                zikan = "" + numberPicker1.getValue() + numberPicker2.getValue() + ":" + (numberPicker3.getValue() * 30);
                                add_time.setText(zikan);
                            } else if (numberPicker3.getValue() == 0) {
                                zikan = "" + numberPicker1.getValue() + numberPicker2.getValue() + ":" + numberPicker3.getValue() + numberPicker4.getValue();
                                add_time.setText(zikan);
                            }
                            tableRow = new TableRow(mcontext);
                            tableRow.setId(add_kazu);
                            TextView textView = new TextView(mcontext);
                            textView.setText(katei);
                            TextView textView2 = new TextView(mcontext);
                            textView2.setText(zikan_old + "-" + zikan);
                            tableRow.addView(textView);
                            tableRow.addView(textView2);
                            tableLayout.addView(tableRow);
                            add_kazu ++;
                            zikan_old = zikan;
                        } else {
                            Toast.makeText(mcontext, dialogMissText, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
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
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    //clicklistener
    OnClickListener addOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            showDialog(0);
        }
    };

    OnClickListener add_finishOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            intent.putStringArrayListExtra("katei", katei_array);
            intent.putIntegerArrayListExtra("time", zikan_array);
            intent.putExtra("alltime", settingTime);
            if (katei_array.size() == 0 && zikan_array.size() == 0) {
                Toast.makeText(mcontext, wantTiem, Toast.LENGTH_LONG).show();
            } else if (katei_array.size() > 0 && zikan_array.size() > 0) {
                startActivity(intent);
                sendMessageToStartActivity();
            }
        }
    };

    OnClickListener removeOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            tableLayout.removeAllViews();
            katei_array.clear();
            zikan_array.clear();
            time_old = 0;
            zikan_old = "00:00";
        }
    };

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendMessageToStartActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collection<String> nodes = getNodes();
                for (String node : nodes) {
                    MessageApi.SendMessageResult result =
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node, START_WEAR_ACTIVITY, null).await();
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            }
        }).start();
    }

}