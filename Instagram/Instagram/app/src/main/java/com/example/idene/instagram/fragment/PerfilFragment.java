package com.example.idene.instagram.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.instagram.R;
import com.example.idene.instagram.activity.EditarPerfilActivity;
import com.example.idene.instagram.adapter.AdapterGrid;
import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.example.idene.instagram.helper.UsuarioFirebase;
import com.example.idene.instagram.model.Postagem;
import com.example.idene.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    public ProgressBar progressBar;
    public CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    public TextView textPublicacaoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    private Usuario usuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference postagensUsuarioRef;
    private AdapterGrid adapterGrid;




    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        //configurar referencia postagens usuarios
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioLogado.getId());

        //configuracoes dos componentes
        inicializarComponentes(view);

        //recuperar usuario logado



        //abrir a edicao do perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        //Inicializar image loader
        inicializarImageLoader();

        //carrega as fotos das postagens de um usuario
        carregarFotosPostagem();

        return view;
    }

    public void carregarFotosPostagem(){

        //recuperar as foros postadas pelo usuario
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {//carregar uma unica vez
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;//recuperar o tamanho real da tela do usuario
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);//colocar o tamanho do grid sempre no mesmo tamanho usando o tamanho da tela

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    //Log.i("postagem","url" + postagem.getCaminhoFoto());
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                //contando as qtde de fotos postada, demora mais
                //int qtdPostagem = urlFotos.size();
                //textPublicacaoes.setText(String.valueOf(qtdPostagem));

                //configurar adapter
                adapterGrid = new AdapterGrid(getActivity(),R.layout.grid_postagem,urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Instancia a UniversalImageLoad
    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getActivity())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

    }


    private void inicializarComponentes(View view){
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        textPublicacaoes = view.findViewById(R.id.textPublicacaoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);
    }

    private void reuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getId());
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//recuperar os dados e exibir na tela

                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //configurar valores recuperados
                        textPublicacaoes.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void recuperarFotoUsuario(){

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //recuperar foto do usuario
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if (caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity())
                    .load(url)
                    .into(imagePerfil);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //recuperar dados do usuario logado
        reuperarDadosUsuarioLogado();

        //Recuperar foto usuário
        recuperarFotoUsuario();

    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);

    }
}
