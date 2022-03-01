package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.ForgotPasswordActivity;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button login, revLogin;
    TextView noAccount, forgotPass, guest;
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

    @SuppressLint("ResourceType")
    @Override
    protected void onStart() {
        //TODO: Add Language selector on first start
        super.onStart();
        //hide all elements. add 2 buttons, login and skip login
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
        guest = findViewById(R.id.guest);
        revLogin = findViewById(R.id.login2);
        revLogin.setOnClickListener(view -> revealLogin());
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
        keepLoggedIn.setOnCheckedChangeListener((buttonView, isChecked) -> keepMeLoggedIn = keepLoggedIn.isChecked());
    }

    private boolean validatePassword() {                                                            //validate password before sending it to database
        UIUtil.hideKeyboard(this);
        inputPassword = Objects.requireNonNull(password.getEditText()).getText().toString();
        if (inputPassword.isEmpty()) {
            password.setError("Field can't be empty");
            return false;
        } else {
            emailAddress.setError(null);
            return true;
        }

    }

    private boolean validateEmail() {
        inputEmail = Objects.requireNonNull(emailAddress.getEditText()).getText().toString();
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
        guest.setOnClickListener(v -> {
            try {
                setGuest(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        noAccount = findViewById(R.id.openRegister);
        noAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
        });

        login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            if (validateEmail() && validatePassword()) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser();
                setGuest(false);
                //postData();
            }

        });
        forgotPass = findViewById(R.id.openForgotPass);
        forgotPass.setOnClickListener(v -> {
            Intent forgot = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(forgot);
        });
    }

    private void setGuest(Boolean isGuest) {
        ((SettingsContainer) this.getApplication()).setGuest(isGuest);
    }

    void loginUser(){
        mAuth.signInWithEmailAndPassword(inputEmail,inputPassword).addOnCompleteListener(task -> {
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
        });
    }

    private void layoutAdjustments() {
        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                card.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            } else {
                card.setVisibility(View.VISIBLE);
                logo.setVisibility(View.VISIBLE);
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
    public void revealLogin(){
        emailAddress.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        keepLoggedIn.setVisibility(View.VISIBLE);
        forgotPass.setVisibility(View.VISIBLE);
        noAccount.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        revLogin.setVisibility(View.INVISIBLE);
//        guest.setVisibility(View.GONE);
    }

}
