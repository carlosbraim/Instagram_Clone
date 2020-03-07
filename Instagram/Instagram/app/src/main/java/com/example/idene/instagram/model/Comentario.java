package com.example.idene.instagram.model;

import com.example.idene.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Comentario {

    private String idComentario;
    private String idPostagem;
    private String idUsuario;
    private String caminhoFoto;//foto perfil do usuario
    private String nomeUuario;
    private String comentario;

    public Comentario() {
    }

    public boolean salvar(){

        /*
        +Comentario
            +id_postagem
                +id_comentario
                    +comentario
        * */
        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebase()
                .child("comentarios")
                .child( getIdPostagem() );

        String chaveComentario = comentariosRef.push().getKey();
        setIdComentario( chaveComentario );
        comentariosRef.child( getIdComentario() ).setValue( this );

        return true;
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getNomeUuario() {
        return nomeUuario;
    }

    public void setNomeUuario(String nomeUuario) {
        this.nomeUuario = nomeUuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
