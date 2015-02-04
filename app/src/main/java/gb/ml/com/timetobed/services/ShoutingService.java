package gb.ml.com.timetobed.services;

import android.app.AlarmManager;
import android.app.KeyguardManager;
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

import java.util.Calendar;
import java.util.Random;

import gb.ml.com.timetobed.activities.TimePickerActivity;
import gb.ml.com.timetobed.fragments.TimePickerFragment;

/**
 * Created by ccen on 1/24/15.
 */
public class ShoutingService extends Service {

    public static String SHOUTING = "SHOUTING";

    public boolean isShouting() {
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);
        return sp.getBoolean("SHOUTING", false);
    }

    public void setShouting(boolean val) {
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean(SHOUTING, val);
        spEditor.commit();
    }

    private int mStartHour, mStartMin, mLastHour, mLastMin;


    public static final String POPPING_DONE = "popping_done";

    @Override
    public void onDestroy() {
        Log.d("service", "ShoutingService is destroyed");
        Log.d("service", "flipping SHOUTING from " + isShouting() + " to false");
        setShouting(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "flipping SHOUTING from " + isShouting() + " to true");
        setShouting(true);
        new Thread() {
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                            MODE_MULTI_PROCESS);
//                    Log.d("sp", "start getting sp from service");
//                    Log.d("sp",
//                            "startHr" + sp
//                                    .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0));
//                    Log.d("sp",
//                            "startMin" + sp
//                                    .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0));
//                    Log.d("sp",
//                            "LastHr" + sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0));
//                    Log.d("sp",
//                            "LastMin" + sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0));
//                    Log.d("sp", "end getting sp");

                    mStartHour = sp
                            .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
                    mStartMin = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
                    mLastHour = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
                    mLastMin = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);

                    Log.d("popping", "startHour: " + mStartHour + ", startMin: " + mStartMin);
                    Log.d("popping", "lastHour: " + mLastHour + ", lastMin: " + mLastMin);

                    Calendar start = Calendar.getInstance();
                    start.set(Calendar.HOUR_OF_DAY, mStartHour);
                    start.set(Calendar.MINUTE, mStartMin);

                    Calendar end = (Calendar) start.clone();
                    end.add(Calendar.HOUR, mLastHour);
                    end.add(Calendar.MINUTE, mLastMin);

                    Calendar now = Calendar.getInstance();

                    while (!(now.before(start) || now.after(end))) {
                        KeyguardManager kgMgr =
                                (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                        if (kgMgr.inKeyguardRestrictedInputMode()) {
                            continue;
                        }

                        shout();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        now = Calendar.getInstance();
                    }

                    endPopping();
                }
            }
        }.start();
        return START_NOT_STICKY;
    }

    private void shout() {
        final String msg = "Go To the Fucking Bed!!! Otherwise tomorrow will suck!";
        Log.d("shout", "begin to shout: " + msg);
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
        Log.d("shout", "shouting end");
        // should clear sharedPf here
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        spEditor.commit();
        Log.d("sp", "sharedPref cleared");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
                new Intent(POPPING_DONE));
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//
//        restartServiceIntent
//                .putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, mStartHour);
//        restartServiceIntent
//                .putExtra(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, mStartMin);
//        restartServiceIntent
//                .putExtra(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, mLastHour);
//        restartServiceIntent
//                .putExtra(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, mLastMin);
//
//        restartServiceIntent.setPackage(getPackageName());
//
//        PendingIntent restartServicePendingIntent = PendingIntent
//                .getService(getApplicationContext(), 1, restartServiceIntent,
//                        PendingIntent.FLAG_ONE_SHOT);
//        // alarm manager allows your application to be executed in the future
//        AlarmManager alarmService = (AlarmManager) getApplicationContext()
//                .getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
//    }
}
