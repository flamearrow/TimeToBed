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

import java.util.Calendar;

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
            final int hour = intent.getIntExtra(TimePickerFragment.HOUR, 0);
            final int min = intent.getIntExtra(TimePickerFragment.MIN, 0);
            mStartHour = hour;
            mStartMin = min;
            startTimeTV.setText("" + hour + " : " + min);
        }
    };


    private BroadcastReceiver mEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int hour = intent.getIntExtra(TimePickerFragment.HOUR, 0);
            final int min = intent.getIntExtra(TimePickerFragment.MIN, 0);
            mLastHour = hour;
            mLastMin = min;
            endTimeTV.setText("" + hour + " : " + min);
        }
    };

    private BroadcastReceiver mShoutingStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "popping started");
            Intent startIntent = new Intent(context, IdleActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    };

    private BroadcastReceiver mShoutingEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "popping done");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mStartReceiver, new IntentFilter(STARTTIME));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mEndReceiver, new IntentFilter(LASTTIME));
        registerReceiver(mShoutingEndReceiver, new IntentFilter(
                ShoutingService.POPPING_DONE));
        registerReceiver(mShoutingStartReceiver, new IntentFilter(
                ShoutingService.POPPING_START));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStartReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mEndReceiver);
        unregisterReceiver(mShoutingEndReceiver);
        unregisterReceiver(mShoutingStartReceiver);
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
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.YEAR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MONTH, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.DAY, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        spEditor.commit();
        Log.d("sp", "shared preference cleared!");
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
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        Log.d("sp", "mStartYear: " + mYear + ", mStartMonth: " + mMonth + ", mStartDay: " + mDay);
        Log.d("sp", "mStartHour: " + mStartHour + ", mStartMin: " + mStartMin);
        Log.d("sp", "mLastHour: " + mLastHour + ", mLastMin: " + mLastMin);
        SharedPreferences sp = getSharedPreferences(SHAREDPREFNAME, MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.YEAR, mYear);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MONTH, mMonth);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.DAY, mDay);
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

    public void startSvc(View v) {
        startService(new Intent(this, ShoutingService.class));
    }
}
