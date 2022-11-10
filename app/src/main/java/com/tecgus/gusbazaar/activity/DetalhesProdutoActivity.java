package com.tecgus.gusbazaar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.tecgus.gusbazaar.R;
import com.tecgus.gusbazaar.databinding.ActivityDetalhesProdutoBinding;
import com.tecgus.gusbazaar.model.Anuncio;

public class DetalhesProdutoActivity extends AppCompatActivity {

    ActivityDetalhesProdutoBinding binding;

    private CarouselView carouselView;
    private Anuncio anuncioSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setTitle("Detalhes do produto");

        carouselView = (CarouselView) findViewById(R.id.carouselView);

        anuncioSelecionada = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if (anuncioSelecionada != null){

            binding.textTituloDetalhe.setText(anuncioSelecionada.getTitulo());
            binding.textDescricaoDetalhe.setText(anuncioSelecionada.getDescricao());
            binding.textEstadoDetalhe.setText(anuncioSelecionada.getEstado());
            binding.textPrecoDetalhe.setText(anuncioSelecionada.getValor());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlString = anuncioSelecionada.getFotos().get(position);

                    Picasso.get().load(urlString).into(imageView);

                }
            };

            carouselView.setPageCount(anuncioSelecionada.getFotos().size());
            carouselView.setImageListener(imageListener);

        }

        binding.btnVerTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visualizarTelefone(view);
            }
        });

    }

    public void visualizarTelefone(View view){

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionada.getTelefone(), null));
        startActivity(intent);

    }
}