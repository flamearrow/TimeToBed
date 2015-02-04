package gb.ml.com.timetobed.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.fragments.TimePickerFragment;
import gb.ml.com.timetobed.services.ShoutingService;

/**
 * Created by ccen on 1/19/15.
 */
public class TimePickerActivity extends FragmentActivity {

    public static final String STARTTIME = "startTime";

    public static final String LASTTIME = "last";

    private TextView startTimeTV, endTimeTV;

    private int mStartHour, mStartMin, mLastHour, mLastMin;

    public static String SHAREDPREFNAME = "TimeToBedSharedPref";

    private BroadcastReceiver mStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mlgb", "startTime received!");
            final int hour = intent.getIntExtra(TimePickerFragment.HOUR, 0);
            final int min = intent.getIntExtra(TimePickerFragment.MIN, 0);
            Log.d("mlgb", "hour: " + hour);
            Log.d("mlgb", "min: " + min);
            mStartHour = hour;
            mStartMin = min;
            startTimeTV.setText("" + hour + " : " + min);
        }
    };


    private BroadcastReceiver mEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mlgb", "endTime received!");
            final int hour = intent.getIntExtra(TimePickerFragment.HOUR, 0);
            final int min = intent.getIntExtra(TimePickerFragment.MIN, 0);
            Log.d("mlgb", "hour: " + hour);
            Log.d("mlgb", "min: " + min);
            mLastHour = hour;
            mLastMin = min;
            endTimeTV.setText("" + hour + " : " + min);
        }
    };

    private BroadcastReceiver mPoppingServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("popping", "popping done");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mStartReceiver, new IntentFilter(STARTTIME));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mEndReceiver, new IntentFilter(LASTTIME));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mPoppingServiceReceiver, new IntentFilter(
                        ShoutingService.POPPING_DONE));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStartReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mEndReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPoppingServiceReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker);
        startTimeTV = (TextView) findViewById(R.id.startTime);
        endTimeTV = (TextView) findViewById(R.id.endTime);
    }


    private void clearSharedPref() {
        SharedPreferences sp = getSharedPreferences(SHAREDPREFNAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        spEditor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: check if shouting service is up, if not start it
//        Log.d("service", "Restart shouting service.");
//        startService(new Intent(this, ShoutingService.class));
    }

    public void pickStartTime(View v) {
        TimePickerFragment startTPF = new TimePickerFragment();
        startTPF.setStartTimer(true);
        startTPF.setPreSetNow(true);
        startTPF.show(getFragmentManager(), "startTimePicker");
    }

    public void pickLastTime(View v) {
        TimePickerFragment endTPF = new TimePickerFragment();
        endTPF.setStartTimer(false);
        endTPF.show(getFragmentManager(), "endTimePicker");
    }

    public void updatePref(View v) {
        Log.d("sp", "mStartHour: " + mStartHour + ", mStartMin: " + mStartMin);
        Log.d("sp", "mLastHour: " + mLastHour + ", mLastMin: " + mLastMin);
        SharedPreferences sp = getSharedPreferences(SHAREDPREFNAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, mStartHour);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, mStartMin);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, mLastHour);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, mLastMin);
        spEditor.commit();
        Log.d("sp", "sharedPref updated");
    }

    public void clearSP(View v) {
        clearSharedPref();
    }
}
