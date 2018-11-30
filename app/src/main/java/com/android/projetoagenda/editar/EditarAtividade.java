package com.android.projetoagenda.editar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class EditarAtividade extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private DatabaseReference ref1;
    private FirebaseUser user;
    private EditText nome;
    private EditText data;
    private Spinner materias;
    private Bundle params;
    private ArrayList<Usuario> usuarios;


    private Button confirmar;
    private Button cancelar;
    private ArrayAdapter atividadeArrayAdapter;

    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edição de atividade");
        setSupportActionBar(toolbar);
        materias = (Spinner) findViewById(R.id.materiasSP);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nome = (EditText) findViewById(R.id.nomeTF);
        data = (EditText) findViewById(R.id.dataTF);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(data, smf);
        data.addTextChangedListener(mtw);
        materias = (Spinner) findViewById(R.id.materiasSP);
        params = getIntent().getExtras();
        usuarios = new ArrayList<>();
        confirmar = (Button) findViewById(R.id.confirmar);
        cancelar = (Button) findViewById(R.id.cancelar);
        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref1 = mDatabase.child("materias");
                ref1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final ArrayList<String> materiasList = new ArrayList<>();;
                        for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("nome").getValue(String.class);
                            materiasList.add(areaName);
                        }
                        atividadeArrayAdapter = new ArrayAdapter<>(EditarAtividade.this, android.R.layout.simple_spinner_item, materiasList);
                        atividadeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        materias.setAdapter(atividadeArrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                ref = mDatabase.child("atividade").child(params.getString("uid"));
                ref.addValueEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    Atividade ch = dataSnapshot.getValue(Atividade.class);
                    nome.setText(ch.getNome());
                    data.setText(sdf.format(ch.getData()));
                    int spinnerMateria = atividadeArrayAdapter.getPosition(ch.getMateria());
                    materias.setSelection(spinnerMateria);
                    usuarios.addAll(ch.getUsuariosAtividade());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(valueEventListener1);

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
        String key = params.getString("uid");
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
        Toast.makeText(this, "Atividade editada com sucesso!", Toast.LENGTH_SHORT).show();
        ref.removeEventListener(valueEventListener);
        ref1.removeEventListener(valueEventListener1);
        cancelar();
    }

    private void cancelar(){
        if(ref != null)
        ref.removeEventListener(valueEventListener);
        else if (mDatabase != null) {
            mDatabase.removeEventListener(valueEventListener1);
        }

        finish();
    }
}
