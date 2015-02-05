package gb.ml.com.timetobed.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import gb.ml.com.timetobed.R;
import gb.ml.com.timetobed.fragments.TimePickerFragment;
import gb.ml.com.timetobed.services.ShoutingService;

/**
 * Created by ccen on 2/4/15.
 */
public class IdleActivity extends Activity {


    private BroadcastReceiver mShoutingEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast", "popping done");
            Intent startIntent = new Intent(context, TimePickerActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mShoutingEndReceiver, new IntentFilter(
                ShoutingService.POPPING_DONE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mShoutingEndReceiver);
    }
}
