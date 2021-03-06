package gb.ml.com.timetobed.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ml.gb.aidlserver.IShoutingService;

import java.util.Calendar;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.fragments.TimePickerFragment;
import gb.ml.com.timetobed.services.LocalShoutingService;

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
                LocalShoutingService.POPPING_DONE));
        registerReceiver(mShoutingStartReceiver, new IntentFilter(
                LocalShoutingService.POPPING_START));
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
        spEditor.apply();
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
        spEditor.apply();
        Log.d("sp", "sharedPref updated");
    }

    public void clearSP(View v) {
        clearSharedPref();
    }


    // local messenger and connection
    private Messenger messenger;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
        }
    };


    // bind to a local service
    public void startSvc(View v) {
        // not bind
        // startService(new Intent(this, ShoutingService.class));

        // bind, need to pass a ServiceConnection object that accespts the result
        bindService(new Intent(this, LocalShoutingService.class), connection,
                Context.BIND_AUTO_CREATE);
    }

    public void callService(View v) {
        if (messenger != null) {
            try {
                messenger.send(Message.obtain(null, LocalShoutingService.WHAT_SHOUT));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("BGLM", "no messenger");
        }
    }


    // Remove service and connection
    private IShoutingService iShoutingService;
    private final ServiceConnection aidlConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // similar to downcast, but the generated code has a dedicated method
            iShoutingService = IShoutingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.d("BGLM", "iShouting service disconnected");
            iShoutingService = null;
        }
    };

    // bind to a remote aidl service
    public void startAidlSvc(View v) {
        Log.d("BGLM", "start remote service");
        Intent intent = new Intent(IShoutingService.ACTION);
        intent.setPackage("com.ml.gb.aidlserver");
        bindService(intent, aidlConnection, Context.BIND_AUTO_CREATE);

    }

    public void callAidlService(View v) {
        if (iShoutingService != null) {
            try {
                Log.d("BGLM", "iShouting at process" + iShoutingService.getPid());
                Toast.makeText(this,
                        iShoutingService.shout(IShoutingService.SHOUT_0),
                        Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("BGLM", "no iShoutingService");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        unbindService(aidlConnection);
    }
}
