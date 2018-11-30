package com.android.projetoagenda.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.android.projetoagenda.cadastro.CadastroAtividade;
import com.android.projetoagenda.R;
import com.android.projetoagenda.classe.Atividade;
import com.android.projetoagenda.classe.Usuario;
import com.android.projetoagenda.editar.EditarAtividade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MinhasAtividades extends AppCompatActivity {
    private FirebaseListAdapter<Atividade> adapter;
    private ListView listViewAtividade;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_atividades);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Minhas Atividades");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarAtividade();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listViewAtividade = (ListView) findViewById(R.id.listViewAtividadeConvidado);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.w("Numsei", user.getUid());
        Query query = FirebaseDatabase.getInstance().getReference().child("atividade").orderByChild("idOwner").equalTo(user.getUid());
        FirebaseListOptions<Atividade> options = new FirebaseListOptions.Builder<Atividade>()
                .setLayout(R.layout.item_list_atividade)
                .setQuery(query, Atividade.class)
                .build();

        adapter = new FirebaseListAdapter<Atividade>(options) {
            @Override
            protected void populateView(View v, Atividade model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.nomeTV);
                TextView materia = (TextView) v.findViewById(R.id.materiaTV);
                TextView data = (TextView) v.findViewById(R.id.dataTV);

                nome.setText(model.getNome());
                materia.setText(model.getMateria());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                data.setText(sdf.format(model.getData()));
            }
        };

        listViewAtividade.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 1, Menu.NONE, "Alterar");
                contextMenu.add(Menu.NONE, 2, Menu.NONE, "Incluir Participantes");
                contextMenu.add(Menu.NONE, 3, Menu.NONE, "Deletar");
            }
        });

        listViewAtividade.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        switch (item.getItemId()) {
            case 1:
                alterar(position);
            break;
            case 2:
                incluir(position);
            break;
            case 3:
                remover(position);
            break;
        }
        return super.onContextItemSelected(item);
    }
    private void criarAtividade() {
        Intent criarAtividade = new Intent(this, CadastroAtividade.class);
        startActivity(criarAtividade);
    }

    private void alterar(int position){
        Atividade atividade = adapter.getItem(position);
        Intent editarAtividade = new Intent(this, EditarAtividade.class);
        editarAtividade.putExtra("uid", atividade.getUidAtividade());
        startActivity(editarAtividade);
    }

    private void incluir(final int position){
        final Atividade atividade = adapter.getItem(position);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MinhasAtividades.this, android.R.layout.select_dialog_singlechoice);

        mDatabase.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<Usuario> uidUsuarios = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Usuario usuario = dataSnapshot1.getValue(Usuario.class);
                    boolean existe = false;
                    for(int i = 0; i < atividade.getUsuariosAtividade().size(); i++){

                        if(atividade.getUsuariosAtividade().get(i).getNome().equals(usuario.getNome())){
                            existe = true;
                        }
                    }
                    if(!existe && !user.getDisplayName().equals(usuario.getNome())){
                        arrayAdapter.add(usuario.getNome());
                        uidUsuarios.add(usuario);
                    }
                }
                if(!arrayAdapter.isEmpty()){
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(MinhasAtividades.this);
                    builderSingle.setTitle("Selecione o usuário a ser incluido:");
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<Usuario> listaUsuarios = atividade.getUsuariosAtividade();
                            listaUsuarios.add(uidUsuarios.get(which));
                            atividade.setUsuariosAtividade(listaUsuarios);
                            mDatabase.child("atividade").child(atividade.getUidAtividade()).setValue(atividade);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MinhasAtividades.this);
                            builderInner.setMessage(uidUsuarios.get(which).getNome());
                            builderInner.setTitle("Você incluiu o usuário: ");
                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builderInner.show();
                        }
                    });
                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.show();
                } else {
                    Toast.makeText(MinhasAtividades.this, "Nenhum usuário para incluir", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void remover(final int position){
        Log.w("Remover", "tentando remover");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remoção de atividades");
        builder.setMessage("Deseja remover o item?");
        //define um botão como positivo
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Atividade atividade = adapter.getItem(position);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("atividade").child(atividade.getUidAtividade()).removeValue();
                Toast.makeText(getApplicationContext(), "Atividade removida", Toast.LENGTH_LONG).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(getApplicationContext(), "Atividade não removida", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }
}
