package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Login extends AppCompatActivity {
    Button login;
    TextView noAccount;
    TextInputLayout emailAddress;
    TextInputLayout password;
    String inputEmail;
    String inputPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layoutAdjustments();
        assignButtons();
        assignInputs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
        finishAndRemoveTask();
        System.exit(1);
    }

    private void layoutAdjustments() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);

        ConstraintLayout constraintLayout = findViewById(R.id.loginConstraintL);
        constraintLayout.getLayoutParams().height = displayMetrics.heightPixels;
        /*
         *TODO: remove this if users have small phones
         * the following parts are not necessary for tall phones.
         */
        /* final ScrollView loginScrollView = findViewById(R.id.loginLayout);
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
                            }else if (password.hasFocus()){
                                loginScrollView.fullScroll(View.FOCUS_DOWN);
                                password.requestFocus();
                            }
                        }
                    });
                }
            }

        });
        */
    }

    private void assignInputs() {                                                                   //assign id to inputs(TextInputEditText)
        emailAddress = findViewById(R.id.emailAdd);
        password = findViewById(R.id.password);
    }

    private boolean validatePassword() {                                                            //validate password before sending it to database
        inputPassword = password.getEditText().getText().toString();
        if (inputPassword.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
            emailAddress.setError(null);
            return true;
        }

    }

    private boolean validateEmail() {
        inputEmail = emailAddress.getEditText().getText().toString();
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
                    postData();
                   // setSharedPref();
                    //finish();
                }

            }
        });
    }

    void postData(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Starting Write and Read data with URL
                //Creating array for parameters
                String[] field = new String[2];
                field[0] = "email_address";
                field[1] = "password";
                //Creating array for data
                String[] data = new String[2];
                data[0] = inputEmail;
                data[1] = inputPassword;

                PutData putData = new PutData("http://192.168.2.103/abaca_app_login-register/login.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("Login Success")){
                            getData();
                            setSharedPref();
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
                        }
                        //End ProgressBar (Set visibility to GONE)
                    }
                }

                //End Write and Read data with URL
            }
            private void getData() {
                FetchData fetchData = new FetchData("http://192.168.2.103/abaca_app_login-register/get.php");
                if (fetchData.startFetch()) {
                    if (fetchData.onComplete()) {
                        String result = fetchData.getResult();
                        //End ProgressBar (Set visibility to GONE)
                        Log.i("FetchData", result);
                    }
                }
            }
        });
    }
    private void setSharedPref() {
        SaveSharedPreference.setPrefUserName(this,inputEmail);
    }
}
