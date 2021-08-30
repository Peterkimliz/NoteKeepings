package com.example.notesaving;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewActivity extends AppCompatActivity {
   private EditText editText;
   private FirebaseAuth firebaseAuth;
   private DatabaseReference databaseReference;
   String user,key,date;
   private Toolbar toolbar;
   private Button button;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        key=getIntent().getExtras().get("key").toString();
        editText=findViewById(R.id.texting);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Notes").child(user);
        toolbar=findViewById(R.id.viewToolbars);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View/Edit");
        button=findViewById(R.id.buttonEdit);
        progressDialog=new ProgressDialog(this);
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String message=snapshot.child("message").getValue().toString();
                    editText.setText(message);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=editText.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(ViewActivity.this, "please enter text ", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setTitle("Updating note");
                    progressDialog.setMessage("please wait while we are updating note");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Calendar claDate=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
                    date=simpleDateFormat.format(claDate.getTime());

                    HashMap hashMap=new HashMap();
                    hashMap.put("message",text);
                    hashMap.put("date",date);
                    databaseReference.child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ViewActivity.this, "note updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ViewActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();

                            }
                            else{
                                String error=task.getException().getMessage();
                                Toast.makeText(ViewActivity.this, "Error occurred"+error, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        }
                    });
                }
            }
        });

    }






}