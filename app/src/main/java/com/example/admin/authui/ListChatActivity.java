package com.example.admin.authui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.authui.model.Chat;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;



public class ListChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<Chat> adapter;
    private ListView listViewChat;
    private FirebaseListAdapter<Chat> adapter1;
    private ListView listViewChatUser;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mandarMensagem();
            }
        });

        listViewChat = (ListView) findViewById(R.id.listViewChat);
        listViewChatUser = (ListView) findViewById(R.id.listViewChatUser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference().child("chat").orderByChild("important").equalTo(true);
        Query query1 = FirebaseDatabase.getInstance().getReference().child("chat").orderByChild("uidUser").equalTo(user.getUid());

        FirebaseListOptions<Chat> options = new FirebaseListOptions.Builder<Chat>()
                .setLayout(R.layout.item_list_chat)
                .setQuery(query, Chat.class)
                .build();

        FirebaseListOptions<Chat> options1 = new FirebaseListOptions.Builder<Chat>()
                .setLayout(R.layout.item_list_chat)
                .setQuery(query1, Chat.class)
                .build();
        FloatingActionButton voltar = (FloatingActionButton) findViewById(R.id.back);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarMain();
            }
        });

        adapter = new FirebaseListAdapter<Chat>(options) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.nome);
                TextView mensagem = (TextView) v.findViewById(R.id.mensagem);

                nome.setText(model.getName());
                mensagem.setText(model.getMessage());

            }
        };

        adapter1 = new FirebaseListAdapter<Chat>(options1) {
            @Override
            protected void populateView(View v, Chat model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.nome);
                TextView mensagem = (TextView) v.findViewById(R.id.mensagem);

                nome.setText(model.getName());
                mensagem.setText(model.getMessage());
            }
        };

        listViewChatUser.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 1, Menu.NONE, "Editar");
                contextMenu.add(Menu.NONE, 2, Menu.NONE, "Excluir");
            }
        });

        listViewChatUser.setAdapter(adapter1);
        listViewChat.setAdapter(adapter);
    }
    private void voltarMain(){
        Intent voltar = new Intent(this, MainActivity.class);
        startActivity(voltar);
        this.finish();
    }
    private void mandarMensagem() {
        Intent sendMessage = new Intent(this, SendMessageActivity.class);
        startActivity(sendMessage);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter1.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter1.stopListening();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int posittion = menuInfo.position;

        switch (item.getItemId()) {
            case 1:
                editar(posittion);
        }
        return super.onContextItemSelected(item);
    }

    private void editar(int position) {
        DatabaseReference itemRef = adapter1.getRef(position);

        Intent edit = new Intent(this, SendMessageActivity.class);

        Toast toast = Toast.makeText(this, itemRef.getKey(), Toast.LENGTH_SHORT);
        toast.show();
    }
}
