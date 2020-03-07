package com.example.idene.instagram.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.idene.instagram.R;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;


    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    public TextView textPublicacaoes, textSeguidores, textSeguindo;

    private String idUsuarioLogado;
    private List<Postagem> postagens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //configuraçoes iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //inicializar componentes
        inicializarComponentes();

        //configurar toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        //configurar botao de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);//add outro botão de voltar

        //recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado =(Usuario) bundle.getSerializable("usuarioSelecionado");

            //configurar referencia postagens usuarios
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("postagens")
                    .child(usuarioSelecionado.getId());

            //configurar o nome do usuario na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //recuperar foto do usuario
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if (caminhoFoto != null){
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(url)
                        .into(imagePerfil);
            }
        }

        //Inicializar image loader
        inicializarImageLoader();

        //carrega as fotos das postagens de um usuario
        carregarFotosPostagem();

        //Abrir a foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get(position);//position da foto clicada
                Intent i = new Intent(getApplicationContext(),VisualizarPostagemActivity.class);
                i.putExtra("postagem",postagem);
                i.putExtra("usuario",usuarioSelecionado);

                startActivity(i);
            }
        });

    }

    //Instancia a UniversalImageLoad
    public void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

    }

    public void carregarFotosPostagem(){

        //recuperar as foros postadas pelo usuario
        postagens = new ArrayList<>();
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
                    postagens.add(postagem);// configurando um array list de postagem
                    //Log.i("postagem","url" + postagem.getCaminhoFoto());
                    urlFotos.add(postagem.getCaminhoFoto());// configurando um array list de fotos
                }
                //contando as qtde de fotos postada, demora mais
                //int qtdPostagem = urlFotos.size();
                //textPublicacaoes.setText(String.valueOf(qtdPostagem));

                //configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(),R.layout.grid_postagem,urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);//recuperar dados do usuario logado
                        //verificar se usuario ja esta seguindo amigo selecionado
                        verificaSegueUsuarioAmigo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }


    private void verificaSegueUsuarioAmigo(){

        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioSelecionado.getId())
                .child(idUsuarioLogado);
        seguidorRef.addListenerForSingleValueEvent(//consulta os dados umaunica vez
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){//se existe os dados no banco
                            //ja esta seguindo
                            Log.i("dadosUsuario",": Seguindo");
                            habilitarBotaoSeguir(true);
                        }else{
                            //ainda não esta seguindo
                            Log.i("dadosUsuario",": Seguir");
                            habilitarBotaoSeguir(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void habilitarBotaoSeguir(boolean segueUsuario){

        if (segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        }else{
            buttonAcaoPerfil.setText("Seguir");

            //adicionar evento para seguir usuario
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvar seguidor
                    salvarSeguidor(usuarioLogado,usuarioSelecionado);

                }
            });
        }

    }

    private void salvarSeguidor(Usuario uLogado,Usuario uAmigo){

        //estrutura do banco de seguidores
        //seguidores
           //id_carlos (amigo) salvar o id do amigo
                //dados seguidor
                   //id_usuario_logado (usuarioLogado) salvar o id do usuario logado
                        //dados usuario_logado
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();//para atulizar apenas alguns valores e nao todos
        dadosUsuarioLogado.put("nome",uLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto",uLogado.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(uAmigo.getId())
                .child(uLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        //alterar botao acao para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //incrementar segundo do usuario logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();//para atulizar apenas alguns valores e nao todos
        dadosSeguindo.put("seguindo",seguindo);
        DatabaseReference usuarioSeguindo = usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);
        //incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();//para atulizar apenas alguns valores e nao todos
        dadosSeguidores.put("seguidores",seguidores);
        DatabaseReference usuarioSeguidores = usuariosRef
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //recuperar dados do amigo selecionado
        reuperarDadosPerfilAmigo();

        //Recuperar dados usuario logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void reuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
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

    private void inicializarComponentes(){

        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
        textPublicacaoes = findViewById(R.id.textPublicacaoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil.setText("Carregando");
        imagePerfil = findViewById(R.id.imagePerfil);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
