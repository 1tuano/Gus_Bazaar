package com.tecgus.gusbazaar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tecgus.gusbazaar.R;
import com.tecgus.gusbazaar.model.Anuncio;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.ViewHolder> {

   private List<Anuncio> anuncios;
   private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterAnuncios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAnuncios.ViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get(position);

        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());

        //pegar image
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, valor;
        ImageView foto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            valor = itemView.findViewById(R.id.textPreco);
            foto = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}
