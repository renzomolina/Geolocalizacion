package com.geolocalizacionsoft.geolocalizacion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.geolocalizacionsoft.geolocalizacion.BaseDeDatos.BaseApplication;
import com.geolocalizacionsoft.geolocalizacion.MisClases.GaleriaAdapter;
import com.geolocalizacionsoft.geolocalizacion.MisClases.Lugares;
import com.geolocalizacionsoft.geolocalizacion.MisClases.MisLugares;
import com.geolocalizacionsoft.geolocalizacion.MisClases.Ubicacion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        AppCompatCallback, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener, NavigationView.OnNavigationItemSelectedListener {

    static final int PETICION_PERMISO_LOCALIZACION = 0,
            PETICION_CONFIG_UBICACION = 0;
    static final String TAG = MainActivity.class.getSimpleName();
    public static Boolean abierto = false;
    int NotificationId = 001;
    boolean NotifyActiv=false;

    GoogleApiClient apiClient = null;
    LocationRequest locationRequest = null;
    GoogleMap googleMap;
    AlertDialog alertGPS = null;
    LocationManager locationManager;
    Cursor cursor;
    //-----------------------------------------------------------------------------------------------------
    Ubicacion ubicacion;
    Location Localizacion;
    LatLng LocalizacionCoord, latitudLongitud;
    ArrayList<Circle> radios;
    //-----------------------------------------------------------------------------------------------------

    //-----------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ubicacion = new Ubicacion();
        radios = new ArrayList<>();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        ConectarAPI();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GpsDesactivado();
        }

    }

    //---------------------------------------- MENU LATERAL ---------------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_lugares) {
            Intent intent = new Intent(this, MisLugares.class);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //------------------------------------------------------------------------------------------------------
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);
        } else {
            MyLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Se interrumpio la conexion con Google Play Services\"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error al conectar con Google Play Services", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        LocalizacionCoord = new LatLng(location.getLatitude(), location.getLongitude());
        ActualizarCamara(LocalizacionCoord);
        if(NotifyActiv==false)
            VerificarPosicion(LocalizacionCoord);

    }

    //------------------------------SOLICITUD DE PERMISOS-------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyLocation();
                Toast.makeText(this, "Permisos de ubicacion aceptado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permimos de ubicacion ignorados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //------------------------------------------------------------------------------------------------------
    private void ConectarAPI() {
        apiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa)).getMapAsync(new Mapa());
    }

    private void ActualizarCamara(LatLng COORDS) {
        CameraPosition CamPos = new CameraPosition
                .Builder()
                .target(COORDS)
                .zoom(16)
                .bearing(-10)
                .tilt(0)
                .build();
        CameraUpdate camUpdateSL = CameraUpdateFactory.newCameraPosition(CamPos);
        googleMap.animateCamera(camUpdateSL);
    }

    private void enableLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100000);
        //locRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, MainActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void GpsDesactivado() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertGPS = builder.create();
        alertGPS.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.dark));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        cursor = Leer();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    latitudLongitud = new LatLng(Double.valueOf(cursor.getString(3)), Double.valueOf(cursor.getString(4)));
                    //ByteArrayInputStream imageStream = new ByteArrayInputStream(cursor.getBlob(5));
                    //Bitmap theImage= BitmapFactory.decodeStream(imageStream);
                    googleMap.addMarker(new MarkerOptions()
                            .position(latitudLongitud)
                            .title(cursor.getString(1))
                            .snippet(cursor.getString(2))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.points_mark_maps)));

                    CrearRadio(googleMap, latitudLongitud);
                } while (cursor.moveToNext());
            }
        }


    }

    //-------------------------------GUARDAR Y LEER BASE DE DATOS-----------------------------------------------------------------
    public void Guardar(Ubicacion ubicacion) {
        ((BaseApplication) getApplication()).InsertarUbicacion(ubicacion);
    }

    public void GuardarIMG(String idLocation, byte[] imagen) {
        ((BaseApplication) getApplication()).InsertarImagenes(idLocation, imagen);
    }

    public Cursor Leer() {
        return (((BaseApplication) getApplication()).DatosUbicacion());
    }

    public Cursor LeerLocation(String lat,String lng){
        return (((BaseApplication)getApplication()).getUbicacion(lat,lng));
    }

    public Cursor LeerLugares(){return (((BaseApplication)getApplication()).getLugares());}

    public Cursor LeerPosciciones() {
        return (((BaseApplication) getApplication()).Posiciones());
    }

    public Cursor LeerIdUltimaUbicacion() {
        return (((BaseApplication) getApplication()).IdUltimaUbicacion());
    }

    //-----------------------------METODO PARA MAPA MARKS, LOCATION Y RADIO--------------------------------------------------------------
    public void MyLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Localizacion = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            LocalizacionCoord = new LatLng(Localizacion.getLatitude(), Localizacion.getLongitude());
            ActualizarCamara(LocalizacionCoord);
        } catch (Exception ex) {
        }
        googleMap.setMyLocationEnabled(true);
        enableLocationUpdates();
    }

    private void CrearRadio(GoogleMap map, LatLng latitudLongitud) {
        radios.add(map.addCircle(new CircleOptions()
                .center(new LatLng(latitudLongitud.latitude, latitudLongitud.longitude))
                .radius(100)
                .strokeColor(Color.BLUE)
                .fillColor(Color.TRANSPARENT)));
    }

    private void CrearMarks(LatLng latLng) {
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(ubicacion.getTitulo())
                .snippet(ubicacion.getDescripcion())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.points_mark_maps)));
    }

    //SE DEBE EJECUTAR EN 2DO PLANO
    private void VerificarPosicion(LatLng COORDS) {
        Cursor location = LeerLugares();
        Ubicacion marks = new Ubicacion();
        if(!NotifyActiv) {
            if (location != null) {
                if (location.moveToFirst()) {
                    do {
                        float[] disResultado = new float[2];
                        LatLng compare = new LatLng(Double.valueOf(location.getString(3)), Double.valueOf(location.getString(4)));
                        Location.distanceBetween(COORDS.latitude, COORDS.longitude, compare.latitude, compare.longitude, disResultado);
                        if (disResultado[0] <= 100) {
                            marks.setTitulo(location.getString(0));
                            marks.setDescripcion(location.getString(1));
                            byte[] blob = location.getBlob(2);
                            ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);
                            Bitmap image = BitmapFactory.decodeStream(imageStream);
                            marks.setLatitud(location.getString(3));
                            marks.setLongitud(location.getString(4));
                            CrearNotificacion(marks,image);
                            NotifyActiv = true;
                        }
                    } while (location.moveToNext());

                }
            }
        }
    }

    private void CrearNotificacion(Ubicacion marks,Bitmap image){
        long[] pattern = new long[]{1000,500,1000};
        Intent galleryIntent = new Intent(this,MisLugares.class);
        PendingIntent NotifyGallery = PendingIntent.getActivity(this, 0, galleryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_maps)
                .setLargeIcon(image)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Estas cerca de " + marks.getTitulo() + " " + marks.getDescripcion())
                .addAction(android.R.drawable.ic_menu_gallery, "MIS LUGARES", NotifyGallery)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setLights(Color.CYAN, 1, 0)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(pattern)
                .setOngoing(true)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager MyNotify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        MyNotify.notify(NotificationId, mBuilder.build());
        NotificationId++;
    }

    //---------------------------------LLAMAR A CLASE INTERNA DIALOG------------------------------------------------------------------------------
    private void InstanciarDialogo(GoogleMap map,LatLng latLng) {
        DialogUbicacion dialog = new DialogUbicacion(map,latLng); //Instanciamos la clase con el dialogo
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "NEWPOSITION");// Mostramos el dialogo
    }
    //-----------------------------------CLASE INTERNA MAPA---------------------------------------------------------------------------
    private class Mapa implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap mapa) {
            googleMap = mapa;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //googleMap.clear();
                }
            });
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onMapLongClick(LatLng latLng) {
                    ubicacion.setLatitud(String.valueOf(latLng.latitude));
                    ubicacion.setLongitud(String.valueOf(latLng.longitude));
                    InstanciarDialogo(googleMap,latLng);
                }
            });
        }
    }
    //-------------------------------CLASE INTERNA DIALOG UBICACION----------------------------------------------------------------
    @SuppressLint("ValidFragment")
    public class DialogUbicacion extends DialogFragment implements View.OnClickListener {

        static final int REQUEST_IMAGE_CAPTURE = 1;
        LatLng latLng;
        ArrayList<byte[]> galeriaImagenes;
        RecyclerView recyclerGaleria;
        EditText nombre, descripcion;
        ImageButton btnCamera,btnGaleria;
        LinearLayout layout;
        ArrayList<Lugares> lugar;
        GaleriaAdapter galeriaAdapter;
        GoogleMap map;

        public DialogUbicacion(GoogleMap map,LatLng latLng){
            this.latLng = latLng;
            this.map = map;
            galeriaImagenes = new ArrayList<>();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View content = inflater.inflate(R.layout.ubicacion_dialog, null);

            btnCamera = content.findViewById(R.id.btCamera);
            btnGaleria = content.findViewById(R.id.btnGaleria);
            nombre = content.findViewById(R.id.input_nombre);
            descripcion = content.findViewById(R.id.input_descripcion);
            recyclerGaleria = content.findViewById(R.id.recyclerGaleria);
            recyclerGaleria.setLayoutManager(new LinearLayoutManager(getContext()));
            layout = content.findViewById(R.id.ContenedorEditText);
            btnCamera.setOnClickListener(this);
            btnGaleria.setOnClickListener(this);


            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(content);
            builder.setPositiveButton("guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if(Validacion()){
                        ubicacion.setTitulo(nombre.getText().toString());
                        ubicacion.setDescripcion(descripcion.getText().toString());
                        Guardar(ubicacion);
                        Cursor c = LeerIdUltimaUbicacion();
                        if(c!=null && c.moveToFirst()) {
                            for (byte[] img : galeriaImagenes) {
                                GuardarIMG(c.getString(0),img);
                            }
                        }
                        CrearMarks(latLng);
                        CrearRadio(map,latLng);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Lo siento, campos obligatorios", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btCamera: {
                    SacarFoto();
                }break;

                case R.id.btnGaleria:{
                    if(galeriaImagenes.size() > 0) {
                        layout.setVisibility(View.INVISIBLE);
                        recyclerGaleria.setVisibility(View.VISIBLE);
                        lugar = new ArrayList<>();
                        ByteArrayInputStream imageStream = new ByteArrayInputStream(galeriaImagenes.get(0));
                        Bitmap image = BitmapFactory.decodeStream(imageStream);
                        lugar.add(new Lugares(nombre.getText().toString(),descripcion.getText().toString(),image));
                        GaleriaAdapter galeriaAdapter = new GaleriaAdapter(lugar);
                        recyclerGaleria.setAdapter(galeriaAdapter);

                        /*for (byte[] img : galeriaImagenes) {
                            ByteArrayInputStream imageStream = new ByteArrayInputStream(img);
                            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                            foto.setImageBitmap(theImage);
                        }*/

                    }
                }break;
                default:
                    break;
            }
        }


        public void SacarFoto() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                galeriaImagenes.add(baos.toByteArray());
            }
        }

        public boolean Validacion(){
            Boolean res=false;

            if (nombre.getText().toString().isEmpty() && descripcion.getText().toString().isEmpty()) {
                nombre.setBackgroundResource(R.drawable.border_validation);
                descripcion.setBackgroundResource(R.drawable.border_validation);
            }
            else if (nombre.getText().toString().isEmpty()) {
                nombre.setBackgroundResource(R.drawable.border_validation);
            }
            else if (descripcion.getText().toString().isEmpty()) {
                descripcion.setBackgroundResource(R.drawable.border_validation);
            }
            else {
                nombre.setBackgroundResource(R.drawable.border_validation_ok);
                descripcion.setBackgroundResource(R.drawable.border_validation_ok);
                res = true;
            }
            return res;
        }
    }

}

