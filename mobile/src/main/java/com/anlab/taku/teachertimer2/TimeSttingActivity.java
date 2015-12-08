package com.anlab.taku.teachertimer2;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class TimeSttingActivity extends ActionBarActivity {

    View content;
    LayoutInflater inflater;

    AlertDialog.Builder builder;

    NumberPicker numberPicker1,numberPicker2,numberPicker3,numberPicker4;
    Button button1,button2,button3;

    String[] minutes = new String[] {"0","3"};

    int settingAllClassTime;

    Context mcontext;

    String dialogTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_stting);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        button1 = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        mcontext = this;

        dialogTimeText = getString(R.string.classtimesetting);

        button1.setOnClickListener(shortOnClickListener);
        button2.setOnClickListener(longOnClickListener);
        button3.setOnClickListener(freeOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    OnClickListener shortOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            settingAllClassTime = 45 * 60;
            Intent intent = new Intent(TimeSttingActivity.this, MainActivity.class);
            intent.putExtra("settingtime", settingAllClassTime);
            startActivity(intent);
        }
    };

    OnClickListener longOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            settingAllClassTime = 50 * 60;
            Intent intent = new Intent(TimeSttingActivity.this, MainActivity.class);
            intent.putExtra("settingtime", settingAllClassTime);
            startActivity(intent);
        }
    };

    OnClickListener freeOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialog(0);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {

        /**
         * ダイアログの準備
         * numberPickerは左から1,2,3,4
         * 秒数を30区切りにするためにnumberPicker3は1と0のみにして，配列を対応
         */

        //カスタムビューを設定
        content = inflater.inflate(R.layout.time_setting_dialog, (ViewGroup)findViewById(R.id.dialog));
        //アラートダイアログを生成
        builder = new AlertDialog.Builder(this);
        numberPicker1 = (NumberPicker) content.findViewById(R.id.numberPicker);
        numberPicker2 = (NumberPicker) content.findViewById(R.id.numberPicker2);
        numberPicker3 = (NumberPicker) content.findViewById(R.id.numberPicker3);
        numberPicker4 = (NumberPicker) content.findViewById(R.id.numberPicker4);
        numberPicker1.setMaxValue(9);
        numberPicker1.setMinValue(0);
        numberPicker1.setValue(5);
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
                        settingAllClassTime = (numberPicker1.getValue() * 600) + (numberPicker2.getValue() * 60) + (numberPicker3.getValue() * 30);
                        if (settingAllClassTime != 0) {
                            Intent intent = new Intent(TimeSttingActivity.this, MainActivity.class);
                            intent.putExtra("settingtime", settingAllClassTime);
                            startActivity(intent);
                        } else {
                            Toast.makeText(mcontext, dialogTimeText, Toast.LENGTH_LONG).show();
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
}
