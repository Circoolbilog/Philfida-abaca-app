package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public  class TermsAndConditionsDialog extends DialogFragment {
    private EulaListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Terms and Conditions")
                .setMessage(getString(R.string.EULAA))
                .setPositiveButton("Accept & Register", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onYesClicked();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EulaListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    +"mustImplementEulaListener");
        }

    }

    public interface EulaListener{
        void onYesClicked();
    }
}