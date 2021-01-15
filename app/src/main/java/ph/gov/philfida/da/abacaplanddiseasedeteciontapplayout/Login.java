package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.ForgotPasswordActivity;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button login;
    TextView noAccount, forgotPass;
    TextInputLayout emailAddress;
    TextInputLayout password;
    String inputEmail;
    String inputPassword;
    ProgressBar progressBar;
    CheckBox keepLoggedIn;
    boolean keepMeLoggedIn;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser!=null){
            Intent login = new Intent(Login.this,MainActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
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
        keepLoggedIn = findViewById(R.id.keepLogged);
        keepLoggedIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                keepMeLoggedIn = keepLoggedIn.isChecked();
            }
        });
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

        login = findViewById(R.id.resetPasswordButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() && validatePassword()) {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser();
                    //postData();
                }

            }
        });
        forgotPass = findViewById(R.id.openForgotPass);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot = new Intent(Login.this, ForgotPasswordActivity.class);
                startActivity(forgot);
            }
        });
    }
    void loginUser(){
        mAuth.signInWithEmailAndPassword(inputEmail,inputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Intent login = new Intent(Login.this,MainActivity.class);
                    startActivity(login);
                }else{
                    Toast.makeText(Login.this,"Failed to login",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
//    void postData(){
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                //Starting Write and Read data with URL
//                //Creating array for parameters
//                String[] field = new String[2];
//                field[0] = "email_address";
//                field[1] = "password";
//                //Creating array for data
//                String[] data = new String[2];
//                data[0] = inputEmail;
//                data[1] = inputPassword;
//
//                PutData putData = new PutData("http://192.168.2.103/abaca_app_login-register/login.php", "POST", field, data);
//                if (putData.startPut()) {
//                    if (putData.onComplete()) {
//                        String result = putData.getResult();
//                        if (result.equals("Login Success")){
//                            getData();
//                            setSharedPref();
//                            finish();
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
//                        }
//                        //End ProgressBar (Set visibility to GONE)
//                    }
//                }
//
//                //End Write and Read data with URL
//            }
//            private void getData() {
//                FetchData fetchData = new FetchData("http://192.168.2.103/abaca_app_login-register/get.php");
//                if (fetchData.startFetch()) {
//                    if (fetchData.onComplete()) {
//                        String result = fetchData.getResult();
//                        //End ProgressBar (Set visibility to GONE)
//                        Log.i("FetchData", result);
//                    }
//                }
//            }
//        });
//    }
//    private void setSharedPref() {
//        SaveSharedPreference.setPrefUserName(this,inputEmail);
//    }
}
