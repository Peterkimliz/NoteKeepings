package com.example.notesaving;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
     private RecyclerView recyclerView;
     private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String user;
    AlertDialog.Builder builder;
    private FirebaseRecyclerAdapter<Model,Holder> adapter;
    private FirebaseRecyclerOptions<Model> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notes");
        floatingActionButton=findViewById(R.id.mainFloat);
        recyclerView=findViewById(R.id.mainRecycler);
        firebaseAuth=FirebaseAuth.getInstance();

        builder = new AlertDialog.Builder(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,NoteAdd.class);
                intent.putExtra("key","null");
                intent.putExtra("title","Add Note");

                startActivity(intent);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            user=firebaseAuth.getCurrentUser().getUid();
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Notes").child(user);
            displayNotes(databaseReference);
        }

    }
    private void displayNotes(DatabaseReference databaseReferences) {
        options=new FirebaseRecyclerOptions.Builder<Model>().setQuery(databaseReferences, Model.class).build();
        adapter=new FirebaseRecyclerAdapter<Model, Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Model model) {
                final String postkey = getRef(position) .getKey();
                holder.textViewMessage.setText(model.getMessage());
                holder.textViewDate.setText(model.getDate());
                holder.textViewTitle.setText(model.getTitle());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.book).into(holder.circleImageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,ViewActivity.class);
                        intent.putExtra("key",postkey);
                        startActivity(intent);

                                          }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteActivity(postkey);
                        return true;
                    }
                });

                        }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);

                return new Holder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    private void deleteActivity(String keys) {

        builder.setMessage("Do you want to delete data")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.child(keys).removeValue();

                        dialog.cancel();


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Delete Data");
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(MainActivity.this, LOginActivity.class);
            startActivity(loginIntent);
        }
    }
}