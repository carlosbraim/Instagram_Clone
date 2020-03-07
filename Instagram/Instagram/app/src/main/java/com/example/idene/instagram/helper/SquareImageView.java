package com.example.idene.instagram.helper;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

//https://gist.github.com/xingrz/c95cdedf57f45f60dd28 codigo do git hub para configurar a imagem
public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//configurando largura e altura com o mesmo tamanho
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
