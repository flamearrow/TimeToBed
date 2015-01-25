package gb.ml.com.timetobed.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import gb.ml.com.timetobed.activities.MainActivity;
import gb.ml.com.timetobed.services.ShoutingService;

/**
 * Created by ccen on 1/24/15.
 */
public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ShoutingService.class);
        context.startService(i);
        Log.i("autostart", "broadcast received");
    }
}
