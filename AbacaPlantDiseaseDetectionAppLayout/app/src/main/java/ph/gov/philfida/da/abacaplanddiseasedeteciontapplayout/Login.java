package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    Button login;
    TextView noAccount;
    TextInputLayout emailAddress;
    TextInputLayout password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        assignButtons();
        assignInputs();
    }

    private void assignInputs() {
        emailAddress = findViewById(R.id.emailAdd);
        password = findViewById(R.id.password);

    }

    private boolean validatePassword() {
        String inputPassword = password.getEditText().getText().toString();
        if (inputPassword.isEmpty()){
            password.setError("Field can't be empty");
            return false;
        }else {
            emailAddress.setError(null);
            return true;
        }

    }

    private boolean validateEmail() {
        String inputEmail = emailAddress.getEditText().getText().toString();
        if (inputEmail.isEmpty()) {
            emailAddress.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            emailAddress.setError("Please enter a valid email address");
            return false;
        } else {
            emailAddress.setError(null);
            return true;
        }
    }
        private void assignButtons(){
            noAccount = findViewById(R.id.openRegister);
            noAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);
                }
            });
            login = findViewById(R.id.loginButton);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateEmail()&&validatePassword()){
                        //TODO send info to server and validate before opening main activity
                        Login.super.onBackPressed();
                       // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                       // startActivity(intent);
                    }

                }
            });
        }
    }
