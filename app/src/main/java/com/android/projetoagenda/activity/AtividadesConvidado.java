package com.android.projetoagenda.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.android.projetoagenda.R;
import com.android.projetoagenda.adapter.AtividadeAdapter;
import com.android.projetoagenda.classe.Atividade;
import com.android.projetoagenda.editar.EditarAtividadeConvidado;

import java.util.ArrayList;

public class AtividadesConvidado extends AppCompatActivity {
    private ArrayList<Atividade> listViewAtividades;
    private AtividadeAdapter mAdapter;
    private ListView listViewAtividade;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Atividades Convidado");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listViewAtividade = (ListView) findViewById(R.id.listViewAtividadeConvidado);
        user = FirebaseAuth.getInstance().getCurrentUser();
        listViewAtividades = new ArrayList<>();
        mAdapter = new AtividadeAdapter(this, listViewAtividades);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Atividade ch = dataSnapshot1.getValue(Atividade.class);
                    for(int i = 0; i < ch.getUsuariosAtividade().size(); i++){
                        if(user.getUid().equals(ch.getUsuariosAtividade().get(i).getUidUser()) && !user.getUid().equals(ch.getIdOwner())){
                            listViewAtividades.add(ch);
                        }
                    }
                }
                listViewAtividade.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child("atividade").addValueEventListener(valueEventListener);

        listViewAtividade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                abrirEditar(mAdapter.getItem(i).getUidAtividade());
            }
        });
    }

    private void abrirEditar (String uid){
        Intent editar = new Intent(this, EditarAtividadeConvidado.class);
        editar.putExtra("uid", uid);
        startActivity(editar);
    }

}
