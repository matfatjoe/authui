package com.example.admin.authui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private Bundle params;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private CheckBox isImportant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        isImportant = (CheckBox) findViewById(R.id.isImportant);

        mensagem = (EditText) findViewById(R.id.mensagem);
        mandar = (Button) findViewById(R.id.mandar);

        mandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensagem = recuperarCampo();
                sendMessage(mensagem);
            }
        });

        if (params != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference message = mDatabase.child("chat").child(params.getString("key"));
            ValueEventListener messageListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Recupera os dados da mensagem para atualizar a tela
                    Chat message = dataSnapshot.getValue(Chat.class);
                    EditText editTextMessage = findViewById(R.id.mensagem);
                    editTextMessage.setHint(message.getMessage());
                    CheckBox checkBox = findViewById(R.id.isImportant);
                    checkBox.setChecked(message.isImportant());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Não conseguiu obter os dados da mensage
                    Log.w("TASK", "loadMessage:onCancelled",
                            databaseError.toException());
                }
            };
            message.addValueEventListener(messageListener);
            recuperarCampo();
            sendMessage();
        }
    }

    private String recuperarCampo() {
        return mensagem.getText().toString();
    }

    private void sendMessage(String message) {
        String key = mDatabase.child("chat").push().getKey();
        boolean importante = isImportant.isChecked();
        Chat chatMessage = new Chat(key, user.getDisplayName(), message, importante, user.getUid());
        mDatabase.child("chat").child(key).setValue(chatMessage);
        finish();
    }

}
