package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class DiagnoseModeDialog extends DialogFragment {
    DiagModeListener listener;
    CheckBox rememberChoice;
    Boolean isRememberChoice;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DiagModeListener)context;
        }catch(ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "mustImplementEulaListener");
        }
    }

    //TODO: Add default mode and remember mode sleected checkbox
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.diagnose_mode_dialog, null);


        builder.setView(view)
                .setNeutralButton("Single Capture Mode", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isRememberChoice = rememberChoice.isChecked();
                        listener.onSingleModeClicked(isRememberChoice);
                    }
                })
                .setPositiveButton("Dual Capture Mode", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isRememberChoice = rememberChoice.isChecked();
                        listener.onDualModeClicked(isRememberChoice);
                    }
                });
        // Create the AlertDialog object and return it
        rememberChoice = view.findViewById(R.id.rememberChoice);

        return builder.create();
    }

    public interface DiagModeListener{
        void onSingleModeClicked(Boolean isRemember);
        void onDualModeClicked(Boolean isRemember);
    }
}
