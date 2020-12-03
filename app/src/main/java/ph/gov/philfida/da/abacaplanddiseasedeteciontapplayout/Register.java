package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
  TextInputLayout lastName, firstName, middleName,birthday, email, password,confirmPassword,
          permanentAddress, occupation, institution;
  TextInputEditText birthdayPick;
  Button register;
  String sLastName, sFirstName, sMiddleName, sBirthday, sEmail, sPassword, sConfirmPassword,
            sPermanendAddress, sOccupation, sInstitution;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        assignInputs();
        register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Validate Inputs
                //TODO open Terms and Conditions dialog
            }
        });

    }

    private void assignInputs() {                                                                   //assign variables to inputs
        lastName = findViewById(R.id.lastName);
        firstName = findViewById(R.id.firstName);
        middleName = findViewById(R.id.middle);
        birthday = findViewById(R.id.birthday);
        email = findViewById(R.id.emailAdd);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPass);
        permanentAddress = findViewById(R.id.permanentAddress);
        occupation = findViewById(R.id.occupation);
        institution = findViewById(R.id.institution);
        birthdayPick = findViewById(R.id.birthdayPicker);
        birthdayPick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                   DialogFragment datePicker = new ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DatePickerDialog();
                   datePicker.show(getSupportFragmentManager(),"date picker");
               }
            }
        });
        //lastname, firstname, email, password, confirmPassword, permanentAddress, occupation = required;
    }

    public void openDatePicker(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        DialogFragment datePicker = new ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DatePickerDialog();
        datePicker.show(getSupportFragmentManager(),"date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        birthdayPick.setText(currentDateString);
    }
}
