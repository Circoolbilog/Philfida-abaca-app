package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Login extends AppCompatActivity {
    Button login;
    TextView noAccount;
    TextInputLayout emailAddress;
    TextInputLayout password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layoutAdjustments();
        assignButtons();
        assignInputs();
    }

    private void layoutAdjustments() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);

        ConstraintLayout constraintLayout = findViewById(R.id.loginConstraintL);
        constraintLayout.getLayoutParams().height = displayMetrics.heightPixels;
        final ScrollView loginScrollView = findViewById(R.id.loginLayout);
        loginScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                loginScrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = loginScrollView.getRootView().getHeight();                       // r.bottom is the position above soft keypad or device button.
                int keypadHeight = screenHeight - r.bottom;                                         // if keypad is shown, the r.bottom is smaller than that before.
                                                                                                    // 0.15 ratio is perhaps enough to determine keypad height.
                if (keypadHeight > screenHeight * 0.15) {                                           // keyboard is opened
                    loginScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (emailAddress.hasFocus()) {
                                loginScrollView.fullScroll(View.FOCUS_DOWN);
                                emailAddress.requestFocus();
                            }else if (password.hasFocus()){
                                loginScrollView.fullScroll(View.FOCUS_DOWN);
                                password.requestFocus();
                            }
                        }
                    });
                }
            }
        });
        loginScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    private void assignInputs() {                                                                   //assign id to inputs(TextInputEditText)
        emailAddress = findViewById(R.id.emailAdd);
        password = findViewById(R.id.password);
    }

    private boolean validatePassword() {                                                            //validate password before sending it to database
        String inputPassword = password.getEditText().getText().toString();
        if (inputPassword.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
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

    private void assignButtons() {
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
                if (validateEmail() && validatePassword()) {
                    //TODO send info to server and validate before opening main activity
                    //Login.super.onBackPressed();
                    //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    finish();
                    //startActivityIfNeeded(intent, 0);
                }

            }
        });
    }
}
