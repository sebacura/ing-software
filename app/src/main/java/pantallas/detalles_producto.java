package pantallas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ingsoft.bancoapp.MainActivity;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.StatusActivity;

import static pantallas.main_productos.obtenerTarjeta;

public class detalles_producto extends AppCompatActivity {

    private TextView tituloTarjeta;
    private Entidad tarjeta = obtenerTarjeta();
    private TextView contenidoTarjeta;
    private ImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_producto);

        tituloTarjeta = findViewById(R.id.tituloTarjetaD);
        contenidoTarjeta = findViewById(R.id.contenidoTarjetaD);
        imgFoto = findViewById(R.id.imgFotoD);

        tituloTarjeta.setText(tarjeta.getTitulo());
        contenidoTarjeta.setText(tarjeta.getDetalles());
        imgFoto.setImageResource(tarjeta.getImgFoto());

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

    public void irAFormulario(View v){
        Intent intent = new Intent(detalles_producto.this, MainActivity.class);
        startActivity(intent);
    }
}
