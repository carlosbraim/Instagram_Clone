package com.example.idene.instagram.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.idene.instagram.R;
import com.example.idene.instagram.adapter.AdapterComentario;
import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.example.idene.instagram.helper.UsuarioFirebase;
import com.example.idene.instagram.model.Comentario;
import com.example.idene.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;

    private String idPostagem;
    private Usuario usuario;
    private AdapterComentario adapterComentario;
    private List<Comentario> listaComentarios = new ArrayList<>();

    private DatabaseReference firebaseRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializar componentes
        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);

        //Configuracoes iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();


        //configurar toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);
        //configurar botao de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);//add outro botão de voltar

        //Configuração recyclerview
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());
        recyclerComentarios.setHasFixedSize( true );
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter( adapterComentario );

        //Recuperar id da postagem
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){
            idPostagem = bundle.getString("idPostagem");
        }
    }

    private void recuperarComentario(){

        comentariosRef = firebaseRef.child("comentarios")
                .child( idPostagem );
        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){//add itens dentro da lista
                    listaComentarios.add( ds.getValue(Comentario.class) );
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener( valueEventListenerComentarios );
    }

    public void salvarComentario(View view){

        String textoComentario = editComentario.getText().toString();
        if( textoComentario != null &&!textoComentario.equals("") ){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem( idPostagem );
            comentario.setIdUsuario( usuario.getId() );
            comentario.setNomeUuario( usuario.getNome() );
            comentario.setCaminhoFoto( usuario.getCaminhoFoto() );
            comentario.setComentario( textoComentario );
            if(comentario.salvar()){
                Toast.makeText(this,
                        "Comentário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,
                    "Insira o comentário antes de salvar!",
                    Toast.LENGTH_SHORT).show();
        }

        //Limpara comentario digitaado
        editComentario.setText("");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
