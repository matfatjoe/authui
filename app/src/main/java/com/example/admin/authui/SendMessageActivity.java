package com.example.admin.authui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.admin.authui.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SendMessageActivity extends AppCompatActivity {

    private EditText mensagem;
    private Button mandar;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private CheckBox isImportant;
    private Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        isImportant = (CheckBox) findViewById(R.id.isImportant);

        mensagem = (EditText) findViewById(R.id.mensagem);
        mandar = (Button) findViewById(R.id.mandar);
        params = getIntent().getExtras();

        if(params != null){
            String key = params.getString("uid");
            if(key != null) {
                DatabaseReference Ref = mDatabase.child("chat").child(key);
                Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chat ch = dataSnapshot.getValue(Chat.class);
                        mensagem.setText(ch.getMessage());
                        isImportant.setChecked(ch.isImportant());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        mandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = mensagem.getText().toString();
        boolean importante = isImportant.isChecked();

        if(params != null){
            String keyEdit = params.getString("uid");
            if(keyEdit != null) {
                Chat chatMessage = new Chat(keyEdit, user.getDisplayName(), message, importante, user.getUid());
                mDatabase.child("chat").child(keyEdit).setValue(chatMessage);
            } else {
                String key = mDatabase.child("chat").push().getKey();
                Chat chatMessage = new Chat(key, user.getDisplayName(), message, importante, user.getUid());
                mDatabase.child("chat").child(key).setValue(chatMessage);
            }
        } else {
            String key = mDatabase.child("chat").push().getKey();
            Chat chatMessage = new Chat(key, user.getDisplayName(), message, importante, user.getUid());
            mDatabase.child("chat").child(key).setValue(chatMessage);
        }
        finish();
    }

}
