package com.android.projetoagenda.cadastro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CadastroAtividade extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private EditText nome;
    private EditText data;

    private Usuario usuario;
    private ArrayList<Usuario> usuarios;
    private ArrayList<String> spinnerArray;

    private Button confirmar;
    private Button cancelar;
    private Spinner materias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro de atividade");
        setSupportActionBar(toolbar);

        spinnerArray = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nome = (EditText) findViewById(R.id.nomeTF);
        data = (EditText) findViewById(R.id.dataTF);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(data, smf);
        data.addTextChangedListener(mtw);
        usuario = new Usuario(user.getDisplayName(), user.getEmail(), user.getUid());
        usuarios = new ArrayList<>();
        usuarios.add(usuario);
        materias = (Spinner) findViewById(R.id.materiasSP);
        mDatabase.child("materias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("nome").getValue(String.class);
                    spinnerArray.add(areaName);
                }
                ArrayAdapter<String> materiasAdapter = new ArrayAdapter<String>(CadastroAtividade.this,
                        android.R.layout.simple_spinner_item, spinnerArray);
                materiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                materias.setAdapter(materiasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        confirmar = (Button) findViewById(R.id.confirmar);
        cancelar = (Button) findViewById(R.id.cancelar);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmar();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelar();
            }
        });
    }

    private void confirmar(){
        String key = mDatabase.child("atividade").push().getKey();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtividade = data.getText().toString();
        Date dataAtividade1 = new Date();
        try {
             dataAtividade1 = sdf.parse(dataAtividade);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Atividade novaAtividade = new Atividade(key, nome.getText().toString(), dataAtividade1, materias.getSelectedItem().toString(), user.getUid(), usuarios);
        mDatabase.child("atividade").child(key).setValue(novaAtividade);
        Toast.makeText(this, "Atividade cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
        cancelar();
    }

    private void cancelar(){
        finish();
    }
}
