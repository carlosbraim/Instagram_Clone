package com.example.idene.instagram.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.idene.instagram.R;
import com.example.idene.instagram.fragment.FeedFragment;
import com.example.idene.instagram.fragment.PerfilFragment;
import com.example.idene.instagram.fragment.PesquisaFragment;
import com.example.idene.instagram.fragment.PostagemFragment;
import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configurar toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Instagram");
        setSupportActionBar(toolbar);

        //configuracaoe de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //configuracao bottom navigation view
        configuraBottomNavigationView();
        //por padrão carregar o feed fragments mesmo codigo usado no metodo responsalver por tratar os clicks do bottomnavigation
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPage, new FeedFragment()).commit();

    }

    //metodo responsalvel por criar a bottomnavigation
    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //faz configuracaoes iniciais do bottom navigation//mais legal true,true,false,true//
        bottomNavigationViewEx.enableAnimation(true);//animacao de aumentar icone
        bottomNavigationViewEx.enableItemShiftingMode(false);//efeito no modo de exibicao do item com o nome embaixo
        bottomNavigationViewEx.enableShiftingMode(false);//efeito para o lado
        bottomNavigationViewEx.setTextVisibility(false);//retira os textos

        //habilitar navegacao ao clicar no icone abrir um fragment
        habilitarNavegacao(bottomNavigationViewEx);

        //configurar item selecionado inicialmente usando o foco no botao
        //Menu menu = bottomNavigationViewEx.getMenu();
        //MenuItem menuItem = menu.getItem(0);
        //menuItem.setCheckable(true);


    }

    //metodo responsavel por tratar eventos de click na bottomnavigation viewRx
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()){
                    case R.id.ic_home :
                        fragmentTransaction.replace(R.id.viewPage, new FeedFragment()).commit();//substitui o fragment para o passado em parametro
                    return true;
                    case R.id.ic_pesquisa :
                        fragmentTransaction.replace(R.id.viewPage, new PesquisaFragment()).commit();//substitui o fragment para o passado em parametro
                        return true;
                    case R.id.ic_postagem :
                        fragmentTransaction.replace(R.id.viewPage, new PostagemFragment()).commit();//substitui o fragment para o passado em parametro
                        return true;
                    case R.id.ic_perfil :
                        fragmentTransaction.replace(R.id.viewPage, new PerfilFragment()).commit();//substitui o fragment para o passado em parametro
                        return true;
                }
                return false;
            }
        });

    }

    //criar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    //click do botao
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
