package gb.ml.com.timetobed.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TimePicker;

import java.util.Calendar;

import gb.ml.com.timetobed.activities.TimePickerActivity;

/**
 * Created by ccen on 1/19/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final String HOUR = "HOUR";

    public static final String MIN = "MIN";

    private boolean mStart = false;

    private boolean mPreSetNow = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = 0, minute = 0;
        if (mPreSetNow) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    /**
     * @param view      The view associated with this listener.
     * @param hourOfDay The hour that was set.
     * @param minute    The minute that was set.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Intent i = new Intent(mStart ? TimePickerActivity.STARTTIME : TimePickerActivity.LASTTIME);
        i.putExtra(HOUR, hourOfDay);
        i.putExtra(MIN, minute);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
    }

    public void setStartTimer(boolean isStart) {
        mStart = isStart;
    }

    public void setPreSetNow(boolean preSetNow) {
        mPreSetNow = preSetNow;
    }


}
