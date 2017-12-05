package com.geolocalizacionsoft.geolocalizacion.MisClases;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geolocalizacionsoft.geolocalizacion.R;

import java.util.ArrayList;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder>{

    ArrayList<Lugares> listaImagenes;

    public GaleriaAdapter(ArrayList<Lugares> listaImagenes){
        this.listaImagenes=listaImagenes;
    }

    @Override
    public GaleriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GaleriaViewHolder holder, int position) {
        holder.txtNombre.setText(listaImagenes.get(position).getNombre());
        holder.txtDescripcion.setText(listaImagenes.get(position).getDescripcion());
        holder.ivFoto.setImageBitmap(listaImagenes.get(position).getImagen());
    }

    @Override
    public int getItemCount() {
        return listaImagenes.size();
    }

    public class GaleriaViewHolder extends RecyclerView.ViewHolder{
        TextView txtNombre,txtDescripcion;
        ImageView ivFoto;

        public GaleriaViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.nombre);
            txtDescripcion = itemView.findViewById(R.id.descripcion);
            ivFoto = itemView.findViewById(R.id.photo);
        }
    }
}
