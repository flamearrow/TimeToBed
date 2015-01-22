package gb.ml.com.timetobed.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Calendar;

import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.activities.MainActivity;
import gb.ml.com.timetobed.activities.TimePickerActivity;
import gb.ml.com.timetobed.fragments.TimePickerFragment;

/**
 * Created by ccen on 1/19/15.
 */
public class PoppingService extends IntentService {

    public static final String COUNT = "count";

    public static final String POPPING_DONE = "popping_done";

    public PoppingService() {
        super("PoppingService");

    }

    private int mStartHour, mStartMin, mLastHour, mLastMin, mCount;

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(android.content.Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        mStartHour = intent.getIntExtra(TimePickerActivity.STARTTIME + TimePickerFragment.HOUR, 0);
        mStartMin = intent.getIntExtra(TimePickerActivity.STARTTIME + TimePickerFragment.MIN, 0);
        mLastHour = intent.getIntExtra(TimePickerActivity.LASTTIME + TimePickerFragment.HOUR, 0);
        mLastMin = intent.getIntExtra(TimePickerActivity.LASTTIME + TimePickerFragment.MIN, 0);
        mCount = intent.getIntExtra(COUNT, 0);

        Log.d("popping", "startHour: " + mStartHour + ", startMin: " + mStartMin);
        Log.d("popping", "lastHour: " + mLastHour + ", lastMin: " + mLastMin);
        Log.d("popping", "mCount: " + mCount);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, mStartHour);
        start.set(Calendar.MINUTE, mStartMin);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.HOUR, mLastHour);
        end.add(Calendar.MINUTE, mLastMin);

        Calendar now = Calendar.getInstance();

        if (now.before(start) || now.after(end)) {
            Log.d("time", "now" + now + " is out of range");
        } else {
            Log.d("time", "now" + now + " is within range");
        }

        while(!(now.before(start) || now.after(end))) {
            shout();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            now = Calendar.getInstance();
        }

        endPopping();
    }

    private void shout() {
        Log.d("shout", "begin to shout");
        final Intent i = new Intent(getApplication(), MainActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(getApplication(), 0, i, 0);
        Notification n = new Notification.Builder(getApplication())
                .setContentTitle("Go To Bed: " + mCount++)
                .setContentText("Otherwise tomorrow will suck!")
                .setContentIntent(pi).setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher).setSound(
                        RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .getNotification();

        NotificationManager notificationManager = (NotificationManager) getApplication()
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
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
