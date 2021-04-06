package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DiagnoseModeDialog extends DialogFragment {
    DiagModeListener listener;
    //TODO: Add default mode and remember mode sleected checkbox
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Terms and Conditions")
                .setMessage("TODO: EULA or something here")
                .setNeutralButton("Single Capture Mode", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onSingleModeClicked();
                    }
                })
                .setNeutralButton("Dual Capture Mode", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDualModeClicked();
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface DiagModeListener{
        void onSingleModeClicked();
        void onDualModeClicked();
    }
}
