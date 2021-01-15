package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.TermsAndConditionsDialog;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TermsAndConditionsDialog.EulaListener {
    TextInputLayout lastName, firstName, middleName, birthday, email, password, confirmPassword,
            permanentAddress, occupation, institution;
    TextInputEditText birthdayPick;
    Button register;
    String sLastName, sFirstName, sMiddleName, sBirthday, sEmail, sPassword, sConfirmPassword,
            sPermanentAddress, sOccupation, sInstitution;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        assignInputs();
    }


    private void assignInputs() {                                                                   //assign variables to inputs
        lastName = findViewById(R.id.lastName);
        firstName = findViewById(R.id.firstName);
        middleName = findViewById(R.id.middleName);
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
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    DialogFragment datePicker = new ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DatePickerDialog();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
            }
        });
        register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Validate Inputs
                validateData();

            }
        });
        //lastname, firstname, email, password, confirmPassword, permanentAddress, occupation = required;
    }

    public void validateData() {
        progressBar.setVisibility(View.VISIBLE);
        sLastName = lastName.getEditText().getText().toString();
        sFirstName = firstName.getEditText().getText().toString();
        sMiddleName = middleName.getEditText().getText().toString();
        sBirthday = birthday.getEditText().getText().toString();
        sEmail = email.getEditText().getText().toString();
        sPassword = password.getEditText().getText().toString();
        sConfirmPassword = confirmPassword.getEditText().getText().toString();
        sPermanentAddress = permanentAddress.getEditText().getText().toString();
        sOccupation = occupation.getEditText().getText().toString();
        sInstitution = institution.getEditText().getText().toString();
        if (sLastName.isEmpty()) {
            lastName.setError("Field is required");
        }
        if (sFirstName.isEmpty()) {
            firstName.setError("Field is required");
        }
        if (sMiddleName.isEmpty()) {
            sMiddleName = "N/A";
        }
        if (sBirthday.isEmpty()) {
            birthday.setError("Field is required");
        }
        if (sEmail.isEmpty()) {
            email.setError("Field is required");
        }
        if (sPassword.isEmpty()) {
            password.setError("Field is required");
        }
        if (sConfirmPassword.isEmpty()) {
            confirmPassword.setError("Field is required");
        }
        if (sPermanentAddress.isEmpty()) {
            permanentAddress.setError("Field is required");
        }
        if (sOccupation.isEmpty()) {
            occupation.setError("Field is required");
        }
        if (sInstitution.isEmpty()) {
            sInstitution = "N/A";
        }
        if (sPassword.equals(sConfirmPassword)) {
            DialogFragment tnc = new TermsAndConditionsDialog();
            tnc.show(getSupportFragmentManager(), "T&C/EULA");
        } else {
            confirmPassword.setError("Passwords Does not match");
        }

        progressBar.setVisibility(View.GONE);
    }

   /* public void postData() {
        //Start ProgressBar first (Set visibility VISIBLE)
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[9];
                field[0] = "last_name";
                field[1] = "first_name";
                field[2] = "middle_name";
                field[3] = "birthday";
                field[4] = "email_address";
                field[5] = "password";
                field[6] = "permanent_address";
                field[7] = "occupation";
                field[8] = "institution";
                //Creating array for data
                String[] data = new String[9];
                data[0] = sLastName;
                data[1] = sFirstName;
                data[2] = sMiddleName;
                data[3] = sBirthday;
                data[4] = sEmail;
                data[5] = sPassword;
                data[6] = sPermanentAddress;
                data[7] = sOccupation;
                data[8] = sInstitution;
                PutData putData = new PutData("http://192.168.2.103/abaca_app_login-register/signup.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("Sign Up Success")){
                            Toast.makeText(getApplicationContext(),"Successfully Registered", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),result +
                                    sLastName+sFirstName+sMiddleName+sBirthday+sEmail+sPassword+
                                    sPermanentAddress +sOccupation+sInstitution, Toast.LENGTH_LONG).show();
                        }

                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                //End Write and Read data with URL
            }
        });
    }
*/
    public void registerUser(){
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(sLastName, sMiddleName, sFirstName, sBirthday, sEmail, sPermanentAddress, sOccupation, sInstitution);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this,"User Registered Sucessfully",
                                                Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        finish();
                                        //redirect to login. finish()?
                                    }else{
                                        Toast.makeText(Register.this,"Failed to register. Try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Register.this, "Failed to register. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void openDatePicker(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        DialogFragment datePicker = new ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DatePickerDialog();
        datePicker.show(getSupportFragmentManager(), "date picker");
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


    @Override
    public void onYesClicked() {
        //postData();
        progressBar.setVisibility(View.VISIBLE);
        registerUser();
    }
}
