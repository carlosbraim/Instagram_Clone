package com.example.idene.instagram.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PostProcessor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.idene.instagram.R;
import com.example.idene.instagram.adapter.AdapterMiniaturas;
import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.example.idene.instagram.helper.RecyclerItemClickListener;
import com.example.idene.instagram.helper.UsuarioFirebase;
import com.example.idene.instagram.model.Postagem;
import com.example.idene.instagram.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");//bloco de inicialização estatic q inica a biblioteca de filter para essa class
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private TextInputEditText textDescricaoFiltro;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private AlertDialog dialog;



    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        //configuraçõs iniciais
        listaFiltros = new ArrayList<>();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");


        //iniciar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);

        //recuperar dados para uma nova postagem
        recuperarDadosPostagem();

        //configurar toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);
        //configurar botao de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);//add outro botão de voltar

        //recuoerar a imagem escolhida pelo usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);//primeiro a imagem, segundo o inicio da imagem e terceiro o final da imagem
            imageFotoEscolhida.setImageBitmap(imagem);
            imagemFiltro = imagem.copy(imagem.getConfig(), true);//transfiri imagem da activity para a imagem que vai receber o filtro e o true permite a imagem ser alterada


            //configurar recyclerview de filtros
            adapterMiniaturas = new AdapterMiniaturas(listaFiltros,getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
            recyclerFiltros.setLayoutManager(layoutManager);
            recyclerFiltros.setAdapter(adapterMiniaturas);

            //adicionar evento de clique no recyclerview
            recyclerFiltros.addOnItemTouchListener(new RecyclerItemClickListener(
                    getApplicationContext(),
                    recyclerFiltros,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            ThumbnailItem item = listaFiltros.get(position);

                            imagemFiltro = imagem.copy(imagem.getConfig(), true);//transfiri imagem da activity para a imagem que vai receber o filtro e o true permite a imagem ser alterada
                            Filter filtro = item.filter;
                            imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }
            ));

            //recupera filtros
            recuperarFiltros();

            /*//aplicar o filtro em uma imagem
            imagemFiltro = imagem.copy(imagem.getConfig(), true);//transfiri imagem da activity para a imagem que vai receber o filtro e o true permite a imagem ser alterada
            Filter filter = FilterPack.getAdeleFilter(getApplicationContext());
            imageFotoEscolhida.setImageBitmap(filter.processFilter(imagemFiltro));
            */
        }
    }

    private void abrirDialogCarregamento(String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false);//usuario tem que aguardar o carregamento
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }


    private void recuperarDadosPostagem(){

        abrirDialogCarregamento("Carregamento dados, aguarde!");
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //recuperar dados do usuario logado
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);

                        //recuperar seguidores
                        //criação do feed que deve ser implementado no servidor porem a titulo de estudo farei o carregamento no app
                        DatabaseReference seguidoresRef =  firebaseRef
                                .child("seguidores")
                                .child(idUsuarioLogado);
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                seguidoresSnapshot = dataSnapshot;
                                dialog.cancel();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    public void recuperarFiltros(){

        //limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        //configurar filtros normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);//gerencia os itens

        //listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());//getFilterPack retorna um list de objetos filters
        for (Filter filtro: filtros){

            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));//recupera os item do ThumbnailsManager onde estao as miniaturas em seguida processThumbs processa todas as fotinhas"miniaturas" de filtros
        adapterMiniaturas.notifyDataSetChanged();
    }

    private void publicarPostagem(){

            abrirDialogCarregamento("Salvando postagem");
            final Postagem postagem = new Postagem();
            postagem.setIdUsuario(idUsuarioLogado);
            postagem.setDescricao(textDescricaoFiltro.getText().toString());

            //recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFiltro.compress(Bitmap.CompressFormat.JPEG,70,baos);
            byte[] dadosImagem = baos.toByteArray();

            //salvar imagem no firebase storage
            StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
            StorageReference imagemRef = storageRef
                    .child("imagens")
                    .child("postagens")
                    .child(postagem.getId() + ".jpeg");

            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FiltroActivity.this,
                            "Erro ao salvar a imagem, tente novamente",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //recuperar local da foto
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri url) {
                            postagem.setCaminhoFoto(url.toString());

                            //atualizar a qtde de postagem
                            int qtdPostagem = usuarioLogado.getPostagens() + 1;
                            usuarioLogado.setPostagens(qtdPostagem);
                            usuarioLogado.atualizarQtdPostagem();

                            //salvar postagem
                            if (postagem.salvar(seguidoresSnapshot)){

                                Toast.makeText(FiltroActivity.this,
                                        "Sucesso ao salvar postagem!",
                                        Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                finish();
                            }
                        }
                    });

                    Toast.makeText(FiltroActivity.this,
                            "Sucesso ao salvar postagem!",
                            Toast.LENGTH_SHORT).show();
                }
            });


    }

    //configuracao do menu com o botao de publicar na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //definir os itens selecionados
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_salvar_postagem:
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //açao do botao fechar na toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
