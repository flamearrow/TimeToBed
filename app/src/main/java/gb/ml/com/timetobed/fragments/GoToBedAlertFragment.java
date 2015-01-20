package gb.ml.com.timetobed.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by ccen on 1/19/15.
 */
public class GoToBedAlertFragment extends DialogFragment {
    public static final String GO_TO_BED = "GO TO BED!";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(GO_TO_BED).create();
    }
}
