package com.android.projetoagenda.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.android.projetoagenda.R;
import com.android.projetoagenda.activity.Atividades;
import com.android.projetoagenda.activity.AtividadesConvidado;
import com.android.projetoagenda.activity.MinhasAtividades;
import com.android.projetoagenda.classe.Usuario;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private TextView emailTF;
    private TextView funcaoTF;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    static LoginActivity activityLogin;
    private Button loginButton;
    private Button logoutButton;
    private Button listarButton;
    private Button listarConviteButton;
    private Button listarAtividadesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home Projeto");
        setSupportActionBar(toolbar);
        activityLogin = this;
        loginButton = findViewById(R.id.loginBT);
        logoutButton = findViewById(R.id.logoutBT);
        listarButton = findViewById(R.id.listarBT);
        listarConviteButton = findViewById(R.id.listarConviteBT);
        listarAtividadesButton = findViewById(R.id.listarAtividadesBT);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailTF = (TextView) findViewById(R.id.emailTV);
        funcaoTF = findViewById(R.id.funcaoTV);
        auth = FirebaseAuth.getInstance();
        if ( auth.getCurrentUser() != null) {
            updateUI(auth.getCurrentUser());
        } else {
            updateUI(null);
            login();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        listarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listar();
            }
        });

        listarConviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listarConvidado();
            }
        });

        listarAtividadesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listarAtividades();
            }
        });
    }

    private void login() {
        // Definindo os providers de autenticação para sua aplicação, nesse
        // caso vamos utilizer Email e Senha, Google e Facebook
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
        // Cria e carrega a intent para signin
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if ( user != null) {
            emailTF.setText(user.getEmail());
            funcaoTF.setText("Funções: ");
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            listarConviteButton.setVisibility(View.VISIBLE);
            listarButton.setVisibility(View.VISIBLE);
            listarAtividadesButton.setVisibility(View.VISIBLE);
        } else {
            emailTF.setText("Usuário deslogado");
            funcaoTF.setText("Funções: Desabilitadas devido ao logout");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            listarConviteButton.setVisibility(View.GONE);
            listarButton.setVisibility(View.GONE);
            listarAtividadesButton.setVisibility(View.GONE);
        }

    }

    private void listar() {
        if(auth.getCurrentUser() != null) {
            Intent lista = new Intent(this, MinhasAtividades.class);
            startActivity(lista);
        } else {
            Toast.makeText(this, "Você precisa estar logado para listar suas atividades", Toast.LENGTH_LONG).show();
        }
    }
    private void listarAtividades() {
        if(auth.getCurrentUser() != null) {
            Intent lista = new Intent(this, Atividades.class);
            startActivity(lista);
        } else {
            Toast.makeText(this, "Você precisa estar logado para listar atividades", Toast.LENGTH_LONG).show();
        }
    }
    private void listarConvidado() {
        if(auth.getCurrentUser() != null) {
            Intent lista = new Intent(this, AtividadesConvidado.class);
            startActivity(lista);
        } else {
            Toast.makeText(this, "Você precisa estar logado para listar atividades convidadas", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == RC_SIGN_IN ) {
            if(resultCode == RESULT_OK) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mDatabase.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean existe = false;
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Usuario usuario = dataSnapshot1.getValue(Usuario.class);
                            if(user.getUid().equals(usuario.getUidUser())){
                                existe = true;
                            }
                        }
                        if(!existe){
                            String key = mDatabase.child("usuarios").push().getKey();
                            Usuario novoUsuario = new Usuario(user.getDisplayName(), user.getEmail(), user.getUid());
                            mDatabase.child("usuarios").child(key).setValue(novoUsuario);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                updateUI(user);
            }
        }
    }

}
