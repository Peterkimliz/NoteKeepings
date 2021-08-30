package com.example.notesaving;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditTexts, usernameEditTexts, passwordEditTexts, cPasswordEditTexts;
    private Button registerButton;
    private TextView textviewLogin;
    ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = findViewById(R.id.registerButton);
        textviewLogin = findViewById(R.id.registerLogin);
        usernameEditTexts = findViewById(R.id.registerUsername);
        emailEditTexts = findViewById(R.id.registerEmail);
        passwordEditTexts = findViewById(R.id.registerPassword);
        cPasswordEditTexts = findViewById(R.id.registerConfirmPassword);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        textviewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(RegisterActivity.this, LOginActivity.class);
                startActivity(intentLogin);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private void createAccount() {
        String username = usernameEditTexts.getText().toString();
        String email = emailEditTexts.getText().toString();
        String password = passwordEditTexts.getText().toString();
        String cPassword = cPasswordEditTexts.getText().toString();
        progressDialog = new ProgressDialog(this);

        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please enter your username", Toast.LENGTH_SHORT).show();
        } else if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please enter your email", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please enter your password", Toast.LENGTH_SHORT).show();
        } else if (cPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "please confirm your password", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(getApplicationContext(), "password mismatch", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Creating your Account");
            progressDialog.setMessage("Please wait while we are creating your account...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveDataToDatabase(email, username, firebaseAuth);
                            } else {
                                String message = task.getException().getMessage().toString();
                                Toast.makeText(getApplicationContext(), "Error Occurrred:" + message, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void saveDataToDatabase(String email, String username, FirebaseAuth firebaseAuth) {
        String current_user_id = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        HashMap hashMap = new HashMap();
        hashMap.put("email", email);
        hashMap.put("username", username);
        hashMap.put("uid", current_user_id);
        databaseReference.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Your account has been successfully created", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                        } else {
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(getApplicationContext(), "Error Occurrred:" + message, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}