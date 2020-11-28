package gb.ml.com.timetobed.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

import androidx.annotation.NonNull;
import gb.ml.com.timetobed.activities.TimePickerActivity;
import gb.ml.com.timetobed.fragments.TimePickerFragment;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by ccen on 1/24/15.
 */
public class ShoutingService extends Service {

    public static final String POPPING_START = "popping_start";

    public static final String POPPING_DONE = "popping_done";

    private static final int WHAT_SHOUT = 1;
    private static final int WHAT_LOG_START_ID = 1 << 1;

    private ServiceHandler serviceHandler;

    private final IBinder binder = new ShoutingBinder();

    public class ShoutingBinder extends Binder {
        public ShoutingService getService() {
            return ShoutingService.this;
        }
    }

    // Some sample public methods for client to call
    public void shout() {
        serviceHandler.post(() -> {
            // off of main
            Toast t = Toast.makeText(getApplicationContext(), "Go To the Fucking Bed!!! " +
                    "Otherwise tomorrow will suck!", Toast.LENGTH_SHORT);
            Display display = ((WindowManager) getApplicationContext().getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay();
            Random r = new Random();
            int yOffset = (int) (r.nextFloat() * display.getHeight());
            t.setGravity(Gravity.TOP | Gravity.CENTER, 0, yOffset);
            t.show();
        });
    }

    public int getRandomNumber() {
        return new Random().nextInt();
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_SHOUT: {
                    shout();
                    break;
                }
                case WHAT_LOG_START_ID: {
                    logStartId(msg.arg1);
                    break;
                }
            }

        }

        private void logStartId(int startId) {
            Log.d("BGLM", "handling new message with startId: " + startId);
        }


    }


    @Override
    public void onDestroy() {
        Log.d("service", "ShoutingService is destroyed");
//        Log.d("service", "Restart shouting service.");
//        startService(new Intent(this, ShoutingService.class));
    }


    @Override
    public void onCreate() {
        HandlerThread thread =
                new HandlerThread("ServiceArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create and send a new message encapsulating 'startId'

        Message msg;
        if (startId % 2 == 0) {
            msg = serviceHandler.obtainMessage(WHAT_SHOUT);
        } else {
            msg = serviceHandler.obtainMessage(WHAT_LOG_START_ID);
            msg.arg1 = startId;
        }
        serviceHandler.sendMessage(msg);
        // Note this is on a separate thread, meaning even ShoutingService is destroyed
        // this thread will still keep running

//        new Thread() {
//            public void run() {
//
//                while (true) {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    while (shouldShout()) {
//                        startPopping();
//                        Log.d("popping", "now shouting....");
//                        KeyguardManager kgMgr =
//                                (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                        if (kgMgr.inKeyguardRestrictedInputMode()) {
//                            continue;
//                        }
//
//                        shout();
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        Log.d("popping", "now shouting....");
//                    }
//
//                    endPopping();
//                }
//            }
//        }.start();
        return START_NOT_STICKY;
    }

    private boolean shouldShout() {
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);
        int mStartYear = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.YEAR, 0);
        int mStartMonth = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MONTH, 0);
        int mStartDay = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.DAY, 0);
        int mStartHour = sp
                .getInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        int mStartMin = sp.getInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        int mLastHour = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        int mLastMin = sp.getInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        Log.d("popping",
                "mStartYear: " + mStartYear + ", mStartMonth: " + mStartMonth + ", mStartDay: "
                        + mStartDay);
        Log.d("popping", "startHour: " + mStartHour + ", startMin: " + mStartMin);
        Log.d("popping", "lastHour: " + mLastHour + ", lastMin: " + mLastMin);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, mStartYear);
        start.set(Calendar.MONTH, mStartMonth);
        start.set(Calendar.DAY_OF_MONTH, mStartDay);
        start.set(Calendar.HOUR_OF_DAY, mStartHour);
        start.set(Calendar.MINUTE, mStartMin);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR, mLastHour);
        end.add(Calendar.MINUTE, mLastMin);

        Calendar now = Calendar.getInstance();
        return !(now.before(start) || now.after(end));
    }


    private void startPopping() {
        Log.d("shout", "shouting started");
        sendBroadcast(new Intent(POPPING_START));
    }

    private void endPopping() {
        Log.d("shout", "shouting end");
        sendBroadcast(new Intent(POPPING_DONE));
    }

    private void clearSP() {
        // should clear sharedPf here
        SharedPreferences sp = getSharedPreferences(TimePickerActivity.SHAREDPREFNAME,
                MODE_MULTI_PROCESS);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.YEAR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MONTH, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.DAY, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        spEditor.putInt(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        spEditor.commit();
        Log.d("sp", "sharedPref cleared");
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
