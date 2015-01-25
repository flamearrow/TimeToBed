package gb.ml.com.timetobed.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

import gb.ml.com.timetobed.activities.TimePickerActivity;
import gb.ml.com.timetobed.fragments.TimePickerFragment;

/**
 * Created by ccen on 1/24/15.
 */
public class ShoutingService extends Service {

    private int mStartHour, mStartMin, mLastHour, mLastMin, mCount;

    public static final String COUNT = "count";

    public static final String POPPING_DONE = "popping_done";

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d("autostart", "shouting service onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                    MODE_MULTI_PROCESS);
            Log.d("sp", "start getting sp from service");
            Log.d("sp",
                    "startHr" + sp
                            .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0));
            Log.d("sp",
                    "startMin" + sp
                            .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0));
            Log.d("sp",
                    "LastHr" + sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0));
            Log.d("sp",
                    "LastMin" + sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0));
            Log.d("sp", "end getting sp");

            mStartHour = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
            mStartMin = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
            mLastHour = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
            mLastMin = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
            mCount = intent.getIntExtra(COUNT, 0);

            Log.d("popping", "startHour: " + mStartHour + ", startMin: " + mStartMin);
            Log.d("popping", "lastHour: " + mLastHour + ", lastMin: " + mLastMin);
            Log.d("popping", "mCount: " + mCount);
        }
    }

    private void shout() {
        Log.d("shout", "begin to shout");
        final String msg = "Go To Bed: " + mCount++ + ", otherwise tomorrow will suck!";
        new Handler(getApplicationContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                Display display = ((WindowManager) getApplicationContext().getSystemService(
                        Context.WINDOW_SERVICE)).getDefaultDisplay();
                Random r = new Random();
                int yOffset = (int) (r.nextFloat() * display.getHeight());
                t.setGravity(Gravity.TOP | Gravity.CENTER, 0, yOffset);
                t.show();
            }
        });
    }

    private void endPopping() {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                new Intent(POPPING_DONE));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        restartServiceIntent
                .putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, mStartHour);
        restartServiceIntent
                .putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, mStartMin);
        restartServiceIntent
                .putExtra(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, mLastHour);
        restartServiceIntent
                .putExtra(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, mLastMin);
        restartServiceIntent.putExtra(COUNT, mCount);

        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent
                .getService(getApplicationContext(), 1, restartServiceIntent,
                        PendingIntent.FLAG_ONE_SHOT);
        // alarm manager allows your application to be executed in the future
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }
}