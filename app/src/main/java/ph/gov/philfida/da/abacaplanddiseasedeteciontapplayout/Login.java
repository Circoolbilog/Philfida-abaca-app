package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

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
    ConstraintLayout loginLayout;
    ImageView card, logo;
    boolean keepMeLoggedIn = true;

    public static final String SHARED_PREFS = "loginData";
    public static final String REMEMBER = "rememberMe";

    @Override
    protected void onStart() {
        //TODO: Add Language selector on first start
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
        loginLayout = findViewById(R.id.loginLayout);
        card = findViewById(R.id.bgCard);
        logo = findViewById(R.id.logo);
        assignButtons();
        assignInputs();
        layoutAdjustments();
        loadUserData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
        finishAndRemoveTask();
        System.exit(1);
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
        UIUtil.hideKeyboard(this);
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

        login = findViewById(R.id.login);
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
                    if (keepMeLoggedIn){
                        saveUserData();
                    }
                    finish();
                }else{
                    Toast.makeText(Login.this,"Failed to login",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void layoutAdjustments() {
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    card.setVisibility(View.GONE);
                    logo.setVisibility(View.GONE);
                } else {
                    card.setVisibility(View.VISIBLE);
                    logo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void saveUserData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean(REMEMBER,keepMeLoggedIn);
        editor.apply();
    }
    public void loadUserData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        keepMeLoggedIn = sharedPreferences.getBoolean(REMEMBER,true);
        keepLoggedIn.setChecked(keepMeLoggedIn);

    }
}
