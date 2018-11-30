package com.android.projetoagenda.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.android.projetoagenda.R;
import com.android.projetoagenda.classe.Atividade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AtividadeAdapter extends ArrayAdapter<Atividade>{

    private Context mContext;
    private List<Atividade> atividadeList;
    private FirebaseUser user;

    public AtividadeAdapter(@NonNull Context context, ArrayList<Atividade> list) {
        super(context, 0 , list);
        mContext = context;
        atividadeList = list;
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_list_atividade, parent, false);
        Atividade currentAtividade = atividadeList.get(position);

        ImageView img = listItem.findViewById(R.id.imageView);
        if(!user.getUid().equals(currentAtividade.getIdOwner())){
            img.setImageDrawable(listItem.getResources().getDrawable(R.drawable.ico_n_owner));
        } else {
            img.setImageDrawable(listItem.getResources().getDrawable(R.drawable.ico_owner));
        }
        TextView nome = (TextView) listItem.findViewById(R.id.nomeTV);
        nome.setText(currentAtividade.getNome());
        TextView materia = (TextView) listItem.findViewById(R.id.materiaTV);
        materia.setText(currentAtividade.getMateria());
        TextView data = (TextView) listItem.findViewById(R.id.dataTV);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        data.setText(sdf.format(currentAtividade.getData()));

        return listItem;
    }
}
