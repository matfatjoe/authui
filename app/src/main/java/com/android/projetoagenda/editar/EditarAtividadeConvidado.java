package com.android.projetoagenda.editar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.android.projetoagenda.R;
import com.android.projetoagenda.classe.Atividade;
import com.android.projetoagenda.classe.Usuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditarAtividadeConvidado extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private DatabaseReference ref1;
    private FirebaseUser user;
    private EditText nome;
    private EditText data;
    private TextView listText;
    private Spinner materias;
    private ListView usuariosSP;
    private Bundle params;
    private ArrayList<Usuario> usuarios;
    private ArrayList<String> nomeUsuarios;
    private LinearLayout botoesConfirmar;
    private LinearLayout voltarBotao;

    private Button voltar;
    private ArrayAdapter atividadeArrayAdapter;
    private ArrayAdapter atividadeUsuarios;

    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Visualização de atividade");
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nome = (EditText) findViewById(R.id.nomeTF);
        nome.setEnabled(false);
        nome.setTextColor(Color.BLACK);
        data = (EditText) findViewById(R.id.dataTF);
        data.setEnabled(false);
        data.setTextColor(Color.BLACK);
        listText = (TextView) findViewById(R.id.textList);
        listText.setVisibility(View.VISIBLE);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(data, smf);
        data.addTextChangedListener(mtw);
        materias = (Spinner) findViewById(R.id.materiasSP);
        params = getIntent().getExtras();
        usuarios = new ArrayList<>();
        usuariosSP = findViewById(R.id.listUsuarios);
        usuariosSP.setVisibility(View.VISIBLE);
        nomeUsuarios = new ArrayList<>();
        voltar = (Button) findViewById(R.id.voltar);
        voltarBotao = (LinearLayout) findViewById(R.id.voltarLayout);
        voltarBotao.setVisibility(View.VISIBLE);
        botoesConfirmar = (LinearLayout) findViewById(R.id.botoes);
        botoesConfirmar.setVisibility(View.GONE);
        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> materiasList = new ArrayList<>();;
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("nome").getValue(String.class);
                    materiasList.add(areaName);
                }
                atividadeArrayAdapter = new ArrayAdapter<>(EditarAtividadeConvidado.this, android.R.layout.simple_spinner_item, materiasList);
                atividadeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                materias.setAdapter(atividadeArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Atividade ch = dataSnapshot.getValue(Atividade.class);
                nome.setText(ch.getNome());
                data.setText(sdf.format(ch.getData()));
                int spinnerMateria = atividadeArrayAdapter.getPosition(ch.getMateria());
                materias.setSelection(spinnerMateria);
                materias.setEnabled(false);
                usuarios.addAll(ch.getUsuariosAtividade());
                for(int i = 0; i < usuarios.size(); i++){
                    nomeUsuarios.add(usuarios.get(i).getNome());
                }
                atividadeUsuarios = new ArrayAdapter<>(EditarAtividadeConvidado.this, android.R.layout.simple_list_item_1, nomeUsuarios);
                usuariosSP.setAdapter(atividadeUsuarios);
                usuariosSP.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref1 = mDatabase.child("materias");
                ref1.addValueEventListener(valueEventListener1);
                ref = mDatabase.child("atividade").child(params.getString("uid"));
                ref.addValueEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });
    }


    private void cancelar(){
        ref.removeEventListener(valueEventListener);
        ref1.removeEventListener(valueEventListener1);
        finish();
    }
}
