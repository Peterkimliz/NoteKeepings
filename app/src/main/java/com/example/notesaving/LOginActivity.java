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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LOginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView textviewRegisters;
    private EditText passwordEditTexts,emailEditTexts;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l_ogin);

        loginButton=findViewById(R.id.loginButton);
        textviewRegisters=findViewById(R.id.loginRegister);
        emailEditTexts=findViewById(R.id.loginEmail);
        passwordEditTexts=findViewById(R.id.loginPassword);

        progressDialog=new ProgressDialog(this);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth =FirebaseAuth.getInstance();


        textviewRegisters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LOginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);

            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


    }


    private void loginUser() {
        String email=emailEditTexts.getText().toString();
        String  password=passwordEditTexts.getText().toString();

        if (email.isEmpty()){
            Toast.makeText(getApplicationContext(),"please enter your email address...",Toast.LENGTH_SHORT).show();
        }

        else if (password.isEmpty()){
            Toast.makeText(getApplicationContext(),"please enter your password...",Toast.LENGTH_SHORT).show();
        }

        else{
            progressDialog.setTitle("Login in");
            progressDialog.setMessage("please wait while we are login you in...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String currentUserId=firebaseAuth.getCurrentUser().getUid();
                                databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Signed in successfully",Toast.LENGTH_SHORT).show();
                                            Intent intentMain=new Intent(LOginActivity.this, MainActivity.class);
                                            startActivity(intentMain);
                                            finish();
                                            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                                        }
                                        else{
                                            progressDialog.dismiss();
                                            Toast.makeText(LOginActivity.this, "medicts not allowed in this account", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                            else{
                                progressDialog.dismiss();
                                String message=task.getException().getMessage().toString();
                                Toast.makeText(getApplicationContext(),"Error Occurred"+message,Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }


    }
}