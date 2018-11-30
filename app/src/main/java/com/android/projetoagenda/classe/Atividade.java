package com.android.projetoagenda.classe;

import java.util.ArrayList;
import java.util.Date;

public class Atividade {
    private String uidAtividade;
    private String nome;
    private Date data;
    private String materia;
    private String idOwner;
    private ArrayList<Usuario> usuariosAtividade;

    public Atividade() {}

    public Atividade(String uidAtividade, String nome, Date data, String materia, String idOwner, ArrayList<Usuario> usuariosAtividade) {
        this.uidAtividade = uidAtividade;
        this.nome = nome;
        this.data = data;
        this.materia = materia;
        this.idOwner = idOwner;
        this.usuariosAtividade = usuariosAtividade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getIdOwner() {
        return idOwner;
    }

    public void setIdOwner(String idOwner) {
        this.idOwner = idOwner;
    }

    public String getUidAtividade() {
        return uidAtividade;
    }

    public void setUidAtividade(String uidAtividade) {
        this.uidAtividade = uidAtividade;
    }

    public ArrayList<Usuario> getUsuariosAtividade() {
        return usuariosAtividade;
    }

    public void setUsuariosAtividade(ArrayList<Usuario> usuariosAtividade) {
        this.usuariosAtividade = usuariosAtividade;
    }
}
