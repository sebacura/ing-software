package pantallas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tananaev.passportreader.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainCompletarDatos extends AppCompatActivity implements LocationListener {

    private EditText et1;
    private EditText et2;
    private EditText ubic;
    private Switch mySwitch;
    public LocationManager handle;
    private String provider;
    private TextView txtGPS;
    private EditText etxtLatitud;
    private EditText etxtLongitud;
    private EditText etxtDirec;


    @SuppressLint({"WrongConstant", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_completar_datos);

        et1 = findViewById(R.id.txt_sueldo);
        et2 = findViewById(R.id.txt_direcc);
        mySwitch = (Switch) findViewById(R.id.swubic);
        txtGPS = (TextView) findViewById(R.id.txtGPS);
       // etxtLatitud = (EditText) findViewById(R.id.etxtLatitud);
       // etxtLatitud = (EditText) findViewById(R.id.etxtLatitud);
        etxtDirec = (EditText) findViewById(R.id.etxtDirec);
       // IniciarServicio();
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEstadoSwitch(isChecked);
            }
        });
    }

    void setEstadoSwitch(boolean x){
        if(x){
            IniciarServicio();
            muestraPosicionActual();
        }else{
            pararServicio();
        }
    }

    public void IniciarServicio() {
        Toast.makeText(this, "Busqueda de ubicación activada", Toast.LENGTH_SHORT).show();
        handle = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        provider = handle.getBestProvider(c, true);
        txtGPS.setText("Proveedor: " + provider);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 120); //*** Agrega la petición!
            return;
        }
        handle.requestLocationUpdates(provider, 10000, 1, this);
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
                            etxtDirec.setText(direccion.getAddressLine(0));
                        }
                    } catch (IOException e) {
                        etxtDirec.setText("" +e);
                        e.printStackTrace();
                    }
                }

            }

        }

        public void pararServicio(){
            handle.removeUpdates(this);
            etxtLatitud.setText(null);
            etxtLongitud.setText(null);
            etxtDirec.setText(null);
            Toast.makeText(this, "Busqueda de ubicación desactivada", Toast.LENGTH_SHORT).show();


        }

    
    public void btnSig(View view){
        String v1= et1.getText().toString();
        String v2= et2.getText().toString();
        int salario= Integer.parseInt(v1);

        if(v1.isEmpty()){
            Toast.makeText(this, "Debe ingresar su salario", Toast.LENGTH_LONG).show();
        }else {

            if (salario < 0) {
                Toast.makeText(this, "Salario no aceptado", Toast.LENGTH_LONG).show();
            } else if (salario < 10000) {
                Toast.makeText(this, "Su salario no es suficiente para solicitar una tarjeta, disculpe", Toast.LENGTH_LONG).show();
            }
        }

        if(v2.isEmpty()){
            Toast.makeText(this, "Debe ingresar su dirección", Toast.LENGTH_LONG).show();
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
}

