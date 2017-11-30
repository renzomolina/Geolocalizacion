package com.geolocalizacionsoft.geolocalizacion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;



import com.geolocalizacionsoft.geolocalizacion.BaseDeDatos.BaseApplication;
import com.geolocalizacionsoft.geolocalizacion.BaseDeDatos.SQLiteUbicaciones;
import com.geolocalizacionsoft.geolocalizacion.MisClases.DialogUbicacion;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements
        AppCompatCallback, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener, NavigationView.OnNavigationItemSelectedListener {


    private static final int PETICION_PERMISO_LOCALIZACION = 0;
    private static final int PETICION_CONFIG_UBICACION = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private AlertDialog alertGPS = null;
    private LocationManager locationManager;
    private SQLiteUbicaciones preferencia;
    private Cursor cursor;
    //-------------------------------------------------------------------------------------------------

    private Ubicacion ubicacion;
    private Location Localizacion;
    private LatLng LocalizacionCoord;

    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ubicacion = new Ubicacion();
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

        if (id == R.id.nav_camera) {

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
            try {
                Localizacion = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                LocalizacionCoord = new LatLng(Localizacion.getLatitude(), Localizacion.getLongitude());
                ActualizarCamara(LocalizacionCoord);
            } catch (Exception ex) {
            }
            googleMap.setMyLocationEnabled(true);
            enableLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                Toast.makeText(this, "Permisos de ubicacion aceptado", Toast.LENGTH_SHORT).show();
            } else {
                //Permiso denegado:
                Toast.makeText(this, "Permimos de ubicacion ignorados", Toast.LENGTH_SHORT).show();
            }
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
    }

//--------------------------------------------------------------------------------------------------------
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
        locationRequest.setInterval(30000);
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
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }

    //--------------------------------------------------------------------------------------------------------------
    private class Mapa implements OnMapReadyCallback {
        int indice=0;
        @Override
        public void onMapReady(GoogleMap mapa) {
            googleMap = mapa;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMap.clear();
                }
            });
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    indice++;
                    ubicacion.setTitulo("Ubicacion " + indice);
                    ubicacion.setDescripcion("Insertar descripcion...!");
                    ubicacion.setPosicion(String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude));
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(ubicacion.getTitulo())
                            .snippet(ubicacion.getDescripcion())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.points_mark_maps)));
                   // Guardar(ubicacion);
                   dialogoUbicacion();
                }
            });
        }
    }


    private void Guardar(Ubicacion ubicacion) {
        ((BaseApplication)getApplication()).InsertarUbicacion(ubicacion);
    }

    private void Leer() {
        cursor = ((BaseApplication)getApplication()).DatosUbicacion();
    }

//---------------------------------------------------------------------------------------------------------------
    private void dialogoUbicacion() {
        DialogUbicacion d = new DialogUbicacion(); //Instanciamos la clase con el dialogo
        d.setCancelable(false);//Hacemos que no se pueda saltar el dialogo (opcional)
        d.show(getFragmentManager(), "NEWPOSITION");// Mostramos el dialogo
    }
}

