package com.ingsoft.bancoapp.applicationForm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.myApplications.StatusActivity;
import com.ingsoft.bancoapp.applicationForm.PlaceAutoSuggestAdapter;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplicantDetailsActivity extends AppCompatActivity implements LocationListener {

    private EditText et1;
    private static EditText et2;
    private EditText ubic;
    private CheckBox mySwitch;
    public LocationManager handle;
    private String provider;
    private TextView txtGPS;
    private EditText etxtLatitud;
    private EditText etxtLongitud;
   // private EditText etxtDirec;
    static EditText edtextDirecc;

    private static String direccion1;
    private static String direccion2;

    private View loadingLayout;
    private View errorMessage;
    private  SharedPreferences sharedPref;
    View btnTomarFoto;
    public String KEY_CAMERA_PHOTO_BASE64 = "";
    private ImageView imageView;

    @SuppressLint({"WrongConstant", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_completar_datos);
        this.imageView = (ImageView)this.findViewById(R.id.btnFotoSueldo);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        loadingLayout = findViewById(R.id.loading_layout);
        errorMessage = findViewById(R.id.error_message);

        et1 = findViewById(R.id.txt_sueldo);
        et2 = findViewById(R.id.txt_direcc);
        mySwitch = (CheckBox) findViewById(R.id.chubic);
        txtGPS = (TextView) findViewById(R.id.txtGPS);
        //etxtDirec = (EditText) findViewById(R.id.etxtDirec);
        edtextDirecc= findViewById(R.id.editTextDirecc);
        Places.initialize(getApplicationContext(), "AIzaSyCQ27Bj40QYHiAJHYF-n999qVqvzQGXrZQ");
        PlacesClient placesClient = Places.createClient(this);

        //Tomar foto desde app
        btnTomarFoto = findViewById(R.id.btnFotoSueldo);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        //Fin tomar foto desde app

        edtextDirecc.setFocusable(false);
        edtextDirecc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("UY").build(ApplicantDetailsActivity.this);
                startActivityForResult(intent, 100);
                edtextDirecc.getText().clear();
            }
            });
        et2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).setCountry("UY").build(ApplicantDetailsActivity.this);
                startActivityForResult(intent, 101);
                et2.getText().clear();
            }
        });

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEstadoSwitch(isChecked);
            }
        });

        // bottom nav bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.action_main);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_main:
                        return true;
                    case R.id.action_status:
                        Intent a = new Intent(getApplicationContext(), StatusActivity.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        ContentValues values = new ContentValues();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void SendRequest() {

        String url = "https://ingsoft-backend.herokuapp.com/applications";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        OneSignal.setExternalUserId(sharedPref.getString("cedulaPersona", "Not Available"));
                        Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        
                        String state = null;
                        try {
                            state = jsonResponse.getString("estado");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("estado: ", state);
                        intent.putExtra("state", state);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingLayout.setVisibility(View.GONE);
                        errorMessage.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("nombre",  sharedPref.getString("nombrePersona", "Not Available"));
                params.put("apellido",  sharedPref.getString("apellidoPersona", "Not Available"));
                params.put("cedula",  sharedPref.getString("cedulaPersona", "Not Available"));
                params.put("direccion",  sharedPref.getString("direccionPersona", "Not Available"));
                params.put("sueldo",  sharedPref.getString("sueldoPersona", "Not Available"));
                params.put("direccionEntrega",  sharedPref.getString("direccionEntrega", "Not Available"));
                params.put("producto",  sharedPref.getString("producto", "Tarjeta Black"));
//                params.put("stateId",  sharedPref.getString("stateId", "Not Available"));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    void setEstadoSwitch(boolean x){
        if(x){
            IniciarServicio();
        }else{
            pararServicio();
        }
    }

    public void IniciarServicio() {
        handle = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        provider = handle.getBestProvider(c, true);
        txtGPS.setText("Proveedor: " + provider);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mySwitch.setChecked(false);
            ActivityCompat.requestPermissions(ApplicantDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 120); //*** Agrega la petición!
        } else {
            Toast.makeText(this, "Busqueda de ubicación activada", Toast.LENGTH_SHORT).show();
            muestraPosicionActual();
            handle.requestLocationUpdates(provider, 10000, 1, this);
        }
    }

    public void muestraPosicionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 120); //*** Agrega la petición!
        }
        Location location = handle.getLastKnownLocation(provider);
        if(location==null){
            //etxtLatitud.setText("Desconocida");
           // etxtLongitud.setText("Desconocida");
        }else{
           // etxtLatitud.setText(String.valueOf(location.getLatitude()));
           // etxtLongitud.setText(String.valueOf(location.getLongitude()));


            }
        setDireccion(location);
        }
        
        public void setDireccion(Location loc){
            if(loc!=null){
                if(loc.getLatitude()!=0.0 && loc.getLongitude()!=0.0){
                    try{
                        Geocoder geocoder= new Geocoder(this, Locale.getDefault());
                        List<Address> list= geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                        if(!list.isEmpty()){
                            Address direccion = list.get(0);
                            edtextDirecc.setText(direccion.getAddressLine(0));
                        }
                    } catch (IOException e) {
                        edtextDirecc.setText("" +e);
                        e.printStackTrace();
                    }
                }

            }

        }

        public void pararServicio(){
//            handle.removeUpdates(this);
//            etxtLatitud.setText(null);
//            etxtLongitud.setText(null);
            edtextDirecc.setText(null);
            Toast.makeText(this, "Busqueda de ubicación desactivada", Toast.LENGTH_SHORT).show();
        }

    public void agregarUbicActual(View view){

        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            et2.setText(edtextDirecc.getText());
            et2.setVisibility(View.GONE);
        } else{
            et2.setVisibility(View.VISIBLE);
            et2.setText(null);
        }
    }
    public void btnSig(View view){
        String sueldoString= et1.getText().toString(); //sueldo
        String dirEntrega = et2.getText().toString(); //direccion entrega
        String dirResidencia = edtextDirecc.getText().toString(); //direccion residencia


        if(sueldoString.isEmpty()){
            Toast.makeText(this, "Debe ingresar su salario", Toast.LENGTH_LONG).show();
        } else {
            int salario = Integer.parseInt(sueldoString);
            if (salario < 0) {
                Toast.makeText(this, "Salario no aceptado", Toast.LENGTH_LONG).show();
            } else if (salario < 10000) {
                Toast.makeText(this, "Su salario no es suficiente para solicitar una tarjeta, disculpe", Toast.LENGTH_LONG).show();
            } else if (dirEntrega.isEmpty()) {
                Toast.makeText(this, "Debe ingresar su dirección", Toast.LENGTH_LONG).show();
            } else {
                //now get Editor
                SharedPreferences.Editor editor = sharedPref.edit();
                //put your value
                if (dirResidencia.isEmpty()) {
                    dirResidencia = dirEntrega;
                }
                editor.putString("direccionPersona", dirResidencia);
                editor.putString("sueldoPersona", sueldoString);
                editor.putString("direccionEntrega", dirEntrega);
                //commits your edits
                editor.commit();
                SendRequest();
//                Log.d("datos solicitud", sharedPref.getAll().toString());

            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            etxtLatitud.setText("Desconocida");
            etxtLongitud.setText("Desconocida");
        }else{
           // etxtLatitud.setText(String.valueOf(location.getLatitude()));
           // etxtLongitud.setText(String.valueOf(location.getLongitude()));

        }
        setDireccion(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap imageBitmap = null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            KEY_CAMERA_PHOTO_BASE64 = encodeImage(imageBitmap);
            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(
//                        getContentResolver(), imageUri);
//                imageBitmap = rotateImage(imageBitmap, 90);
                imageBitmap = getResizedBitmap(imageBitmap, 320); //limito el tamaño de la imagen
                //Para imprimir la foto en pantalla (para probar)
                imageView.setImageBitmap(imageBitmap);
                findViewById(R.id.instruccionfoto2).setVisibility(View.GONE);
                findViewById(R.id.avisofoto2).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            KEY_CAMERA_PHOTO_BASE64 = encodeImage(imageBitmap);
        }

            if (requestCode == 100) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    edtextDirecc.setText(place.getName());
                    direccion2 = place.getName();
                    //et2.setText(place.getName());
                    // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    // Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                et2.setText(place.getName());
                // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                // Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static String getDirection1(){
        return direccion1;
    }

    public static String getDirection2(){
        return direccion2;
    }
}

