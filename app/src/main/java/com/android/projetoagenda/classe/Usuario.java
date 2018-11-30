package com.android.projetoagenda.classe;

public class Usuario {
    private String nome;
    private String email;
    private String uidUser;

    public Usuario() {
    }

    public Usuario(String nome, String email, String uidUser) {
        this.nome = nome;
        this.email = email;
        this.uidUser = uidUser;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUidUser() {
        return uidUser;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }
}
