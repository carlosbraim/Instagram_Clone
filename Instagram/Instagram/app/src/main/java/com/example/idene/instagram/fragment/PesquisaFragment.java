package com.example.idene.instagram.fragment;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.idene.instagram.R;
import com.example.idene.instagram.activity.PerfilAmigoActivity;
import com.example.idene.instagram.adapter.AdapterPesquisa;
import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.example.idene.instagram.helper.RecyclerItemClickListener;
import com.example.idene.instagram.helper.UsuarioFirebase;
import com.example.idene.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    //widget
    private android.support.v7.widget.SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;

    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;



    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        //toda vez q eu uso um fragment o findViewById e acessado pela view
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);

        //configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(),PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //configurar recyclerview
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios,getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        //configurar searchview
        searchViewPesquisa.setQueryHint("Buscar usuários");//texto de  uando o usuario inicia uma pesquisa
        searchViewPesquisa.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {//capturar o q o usuario digitou
            @Override
            public boolean onQueryTextSubmit(String query) {//recuperar dados digitado pelo usuario apos apertar o tbn de pesquisa
                //Log.d("onQueryTextSubmit","texto digitado: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//capturar o texto digitado em tempo real
                //Log.d("onQueryTextChange","texto digitado: " + newText);
                String textoDigitado = newText.toUpperCase();//toUpperCase = converter tudo em maiusculo
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });



        return view;
    }

    private void pesquisarUsuarios(String texto){

        //limpar lista
        listaUsuarios.clear();

        //pesquisar usuarios caso tenha texto na pesquisa
        if (texto.length() >= 2){

            Query query = usuariosRef.orderByChild("nome")
                    .startAt(texto)//localizar o texto q comeca com a leta ""
                    .endAt(texto + "\uf8ff");//localizar o texto q termina com a leta ""
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //limpar lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds: dataSnapshot.getChildren()){//percorrer e montar alista de itens

                        //verificar se é usuario logado e remover da lista
                        Usuario usuario = ds.getValue(Usuario.class);
                        if (idUsuarioLogado.equals(usuario.getId()))
                            continue;//volta para o inicio do for sem executar as linhas abaixo

                        listaUsuarios.add(usuario);
                    }

                    adapterPesquisa.notifyDataSetChanged();


                    //exibir o itens
                    //int total = listaUsuarios.size();

                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
