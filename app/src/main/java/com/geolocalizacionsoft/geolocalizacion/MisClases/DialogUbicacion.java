package com.geolocalizacionsoft.geolocalizacion.MisClases;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.geolocalizacionsoft.geolocalizacion.R;

import static android.app.Activity.RESULT_OK;


public class DialogUbicacion extends DialogFragment implements View.OnClickListener{

    private EditText nombre, descripcion;
    private ImageButton btnCamera;
    private ImageView foto;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle bundle)
    {
        final View content = getActivity().getLayoutInflater().inflate(R.layout.ubicacion_dialog,null);

        btnCamera = content.findViewById(R.id.btCamera);
        nombre = content.findViewById(R.id.input_nombre);
        descripcion = content.findViewById(R.id.input_descripcion);
        foto = content.findViewById(R.id.foto);
        btnCamera.setOnClickListener(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content)
                // Add action buttons

                .setPositiveButton("guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Codigo para cuando se haga click en negativo

                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btCamera:
                if(nombre.getText().toString().isEmpty() || descripcion.getText().toString().isEmpty()){
                    nombre.setBackgroundResource(R.drawable.border_validation);
                    descripcion.setBackgroundResource(R.drawable.border_validation);
                }
                else if(nombre.getText().toString().isEmpty()){
                    nombre.setBackgroundResource(R.drawable.border_validation);
                }
                else if(descripcion.getText().toString().isEmpty()){
                    descripcion.setBackgroundResource(R.drawable.border_validation);
                }
                else{
                    nombre.setBackgroundResource(R.drawable.border_validation_ok);
                    descripcion.setBackgroundResource(R.drawable.border_validation_ok);
                    SacarFoto();
                    break;
                }
            default:
                break;
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public void SacarFoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            foto.setVisibility(View.VISIBLE);
            foto.setImageBitmap(imageBitmap);
        }
    }
}

