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
import com.tananaev.passportreader.MainActivity;
import com.tananaev.passportreader.R;
import com.tananaev.passportreader.ResultActivity;
import com.tananaev.passportreader.StatusActivity;

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
        contenidoTarjeta.setText(tarjeta.getContenido());
        imgFoto.setImageResource(tarjeta.getImgFoto());

        // bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (int i = 0; i < navigation.getMenu().size(); i++) {
                    MenuItem menuItem = navigation.getMenu().getItem(i);
                    boolean isChecked = menuItem.getItemId() == item.getItemId();
                    menuItem.setChecked(isChecked);
                }
                switch (item.getItemId()) {
                    case R.id.action_status:
                        Intent a = new Intent(detalles_producto.this, StatusActivity.class);
                        startActivity(a);
                        break;
                }
                return true;
            }
        });
        navigation.getMenu().findItem(R.id.action_main).setChecked(true);

    }

    public void irAFormulario(View v){
        Intent intent = new Intent(detalles_producto.this, MainActivity.class);
        startActivity(intent);
    }
}
