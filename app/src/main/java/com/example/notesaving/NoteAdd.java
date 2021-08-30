package com.example.notesaving;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoteAdd extends AppCompatActivity {
    private EditText editText,editTextTitle;
    private CircleImageView circleImageView;
    private Toolbar toolbar;
    private Button button;
    String title,key;
    Uri imageUrl;
    int galleryPick=10;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String user,time,randomName,downloadUrl,date;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        title=getIntent().getExtras().get("title").toString();
        key=getIntent().getExtras().get("key").toString();

        progressDialog=new ProgressDialog(this);
        toolbar=findViewById(R.id.noteAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Notes").child(user);
        storageReference= FirebaseStorage.getInstance().getReference().child("usersPic");

        editText=findViewById(R.id.noteWrite);
        editTextTitle=findViewById(R.id.noteTitle);
        button=findViewById(R.id.noteSave);
        circleImageView=findViewById(R.id.noteAddImage);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,galleryPick);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyData();
            }
        });

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String mess=snapshot.child("message").getValue().toString();
                    String title=snapshot.child("title").getValue().toString();
                    editTextTitle.setText(title);
                    editText.setText(mess);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void verifyData() {
        String text=editText.getText().toString().trim();
        String title=editTextTitle.getText().toString().trim();
        Calendar claDate=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
        date=simpleDateFormat.format(claDate.getTime());
        Calendar calTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        time=currentTime.format(calTime.getTime());
        randomName=date+time;
        if (imageUrl==null){
            Toast.makeText(this, "please Select image", Toast.LENGTH_SHORT).show();
        }
        else if (title.isEmpty()){
            Toast.makeText(this, "please enter title", Toast.LENGTH_SHORT).show();
        }
       else if (text==null){
            Toast.makeText(this, "please enter text", Toast.LENGTH_SHORT).show();
        }
       else{
           progressDialog.setTitle("Saving note");
           progressDialog.setMessage("please wait while we are saving note");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();

            HashMap hashMap=new HashMap();
            hashMap.put("image",downloadUrl);
            hashMap.put("title",title);
            hashMap.put("message",text);
            hashMap.put("date",date);
            databaseReference.child(title).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(NoteAdd.this, "note saved successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(NoteAdd.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        String error=task.getException().getMessage();
                        Toast.makeText(NoteAdd.this, "Error occurred"+error, Toast.LENGTH_SHORT).show();

                    }
                }
            });



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==galleryPick&&resultCode==RESULT_OK&&data!=null){
            imageUrl=data.getData();
            circleImageView.setImageURI(imageUrl);
            final StorageReference filepath=storageReference.child(imageUrl.getLastPathSegment()+randomName+ ".jpg");
            filepath.putFile(imageUrl).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener( new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                downloadUrl=task.getResult().toString();
                                progressDialog.dismiss();
                            }

                            else {
                                String message=task.getException().getMessage();
                                Toast.makeText(getApplicationContext(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }
                        }
                    } );
                }
            } );


        }
    }
}