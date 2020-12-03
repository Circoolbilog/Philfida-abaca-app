package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs;

import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        return new android.app.DatePickerDialog(getActivity(),
                (android.app.DatePickerDialog.OnDateSetListener)getActivity(),year,month,day);
    }
}
