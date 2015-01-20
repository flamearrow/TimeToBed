package gb.ml.com.timetobed.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.fragments.GoToBedAlertFragment;
import gb.ml.com.timetobed.fragments.TimePickerFragment;
import gb.ml.com.timetobed.services.PoppingService;

/**
 * Created by ccen on 1/19/15.
 */
public class TimePickerActivity extends FragmentActivity {

    public static final String STARTTIME = "startTime";

    public static final String ENDTIME = "endTime";

    private TextView startTimeTV, endTimeTV;

    private int mStartHour, mStartMin, mEndHour, mEndMin;

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
            mEndHour = hour;
            mEndMin = min;
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
                .registerReceiver(mEndReceiver, new IntentFilter(ENDTIME));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mPoppingServiceReceiver, new IntentFilter(
                        PoppingService.POPPING_DONE));

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

    public void pickStartTime(View v) {
        TimePickerFragment startTPF = new TimePickerFragment();
        startTPF.setStartTimer(true);
        startTPF.show(getFragmentManager(), "startTimePicker");
    }

    public void pickEndTime(View v) {
        TimePickerFragment endTPF = new TimePickerFragment();
        endTPF.setStartTimer(false);
        endTPF.show(getFragmentManager(), "endTimePicker");
    }

    public void startShout(View v) {
        final Intent i = new Intent(this, PoppingService.class);
        i.putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, mStartHour);
        i.putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, mStartMin);
        i.putExtra(TimePickerActivity.ENDTIME + TimePickerFragment.HOUR, mEndHour);
        i.putExtra(TimePickerActivity.ENDTIME + TimePickerFragment.MIN, mEndMin);
        startService(i);
        Log.d("popping", "start popping");
    }
}
