package gb.ml.com.timetobed.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.fragments.TimePickerFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplication();

        if(!isShouting()) {
            Intent startIntent = new Intent(context, TimePickerActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        } else {
            Intent startIntent = new Intent(context, IdleActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }

    private boolean isShouting() {
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);

        int mStartHour = sp
                .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        int mStartMin = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        int mLastHour = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        int mLastMin = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);

        Log.d("MainActivity", "startHour: " + mStartHour + ", startMin: " + mStartMin);
        Log.d("MainActivity", "lastHour: " + mLastHour + ", lastMin: " + mLastMin);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, mStartHour);
        start.set(Calendar.MINUTE, mStartMin);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR, mLastHour);
        end.add(Calendar.MINUTE, mLastMin);

        Calendar now = Calendar.getInstance();
        return !(now.before(start) || now.after(end));
    }
}
