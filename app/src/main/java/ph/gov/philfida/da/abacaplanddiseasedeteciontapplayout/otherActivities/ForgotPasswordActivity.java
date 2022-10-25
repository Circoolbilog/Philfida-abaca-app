/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout email;
    private Button resetPassword;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        progressBar=findViewById(R.id.progressBar);
        assignIds();
        firebaseAuthStuff();
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });
    }

    private void resetPass() {
        String sEmail = email.getEditText().getText().toString().trim();
        if (sEmail.isEmpty()){
            email.setError("Required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()){
            email.setError("Invalid email");
            email.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(sEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Pleas check your email to reset your password", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this,"Failed, Try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void firebaseAuthStuff() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void assignIds() {
        email=findViewById(R.id.emailAdd);
        resetPassword = findViewById(R.id.resetPasswordButton);
        progressBar = findViewById(R.id.progressBar);
    }
}