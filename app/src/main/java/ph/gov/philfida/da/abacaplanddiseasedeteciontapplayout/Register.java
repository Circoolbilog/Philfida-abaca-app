package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.text.DateFormat;
import java.util.Calendar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.TermsAndConditionsDialog;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TermsAndConditionsDialog.EulaListener {
    TextInputLayout lastName, firstName, middleName, birthday, email, password, confirmPassword,
            permanentAddress, occupation, institution;
    TextInputEditText birthdayPick;
    Button register;
    String sLastName, sFirstName, sMiddleName, sBirthday, sEmail, sPassword, sConfirmPassword,
            sPermanentAddress, sOccupation, sInstitution;
    ProgressBar progressBar, registrationProgress;
    ConstraintLayout cardLayout,rootView;
    int regProgress = 1;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        cardLayout = findViewById(R.id.cardLayout);
        rootView = findViewById(R.id.rootView);
        assignInputs();
        layoutAdjustments();
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
                    cardLayout.setVisibility(View.GONE);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    DialogFragment datePicker = new ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DatePickerDialog();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }else {
                    cardLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        register = findViewById(R.id.registerButton);
        registrationProgress = findViewById(R.id.progress);
        register.setText("Proceed");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(regProgress);
            }
        });
        //lastname, firstname, email, password, confirmPassword, permanentAddress, occupation = required;
    }

    public void validateData(int prog) {
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
        int percentage = (prog *100) /3;
        switch (prog) {
            case 1:
                UIUtil.hideKeyboard(this);
                progressBar.setVisibility(View.GONE);
                registrationProgress.setProgress(percentage);
                if (sLastName.isEmpty()) {
                    lastName.setError("Field is required");
                    lastName.requestFocus();
                    return;
                }
                if (sFirstName.isEmpty()) {
                    firstName.setError("Field is required");
                    firstName.requestFocus();
                    return;
                }
                if (sMiddleName.isEmpty()) {
                    sMiddleName = "N.A.";
                }
                if (sBirthday.isEmpty()) {
                    birthday.setError("Field is required");
                    birthday.requestFocus();
                    return;
                }
                lastName.setVisibility(View.GONE);
                firstName.setVisibility(View.GONE);
                middleName.setVisibility(View.GONE);
                birthday.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);
                regProgress += 1;
                return;
            case 2:
                UIUtil.hideKeyboard(this);
                progressBar.setVisibility(View.GONE);
                registrationProgress.setProgress(percentage);
                if (sEmail.isEmpty()) {
                    email.setError("Field is required");
                    email.requestFocus();
                    return;
                }
                if (sPassword.isEmpty()) {
                    password.setError("Field is required");
                    password.requestFocus();
                    return;
                }
                if (sConfirmPassword.isEmpty()) {
                    confirmPassword.setError("Field is required");
                    confirmPassword.requestFocus();
                    return;
                }
                email.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                confirmPassword.setVisibility(View.GONE);
                permanentAddress.setVisibility(View.VISIBLE);
                occupation.setVisibility(View.VISIBLE);
                institution.setVisibility(View.VISIBLE);
                register.setText("Register");
                regProgress += 1;
                return;
            case 3:
                UIUtil.hideKeyboard(this);
                progressBar.setVisibility(View.GONE);
                registrationProgress.setProgress(percentage);
                if (sPermanentAddress.isEmpty()) {
                    permanentAddress.setError("Field is required");
                    permanentAddress.requestFocus();
                    return;
                }
                if (sOccupation.isEmpty()) {
                    occupation.setError("Field is required");
                    occupation.requestFocus();
                    return;
                }
                if (sInstitution.isEmpty()) {
                    sInstitution = "N/A";
                }
                if (sPassword.equals(sConfirmPassword)) {
                    DialogFragment tnc = new TermsAndConditionsDialog();
                    tnc.show(getSupportFragmentManager(), "T&C/EULA");
                } else {
                    confirmPassword.setError("Passwords Does not match");
                    progressBar.setVisibility(View.GONE);
                }
        }


    }

    private void layoutAdjustments() {
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    cardLayout.setVisibility(View.GONE);
                } else {
                    cardLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void registerUser() {
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(sLastName, sMiddleName, sFirstName, sBirthday, sEmail, sPermanentAddress, sOccupation, sInstitution);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "User Registered Sucessfully",
                                                Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        Intent login = new Intent(Register.this, Login.class);
                                        startActivity(login);
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Failed to register. Try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
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
