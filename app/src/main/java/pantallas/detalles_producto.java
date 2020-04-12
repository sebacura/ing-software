package pantallas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tananaev.passportreader.MainActivity;
import com.tananaev.passportreader.R;

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

    }

    public void irAFormulario(View v){
        Intent intent = new Intent(detalles_producto.this, MainActivity.class);
        startActivity(intent);
    }
}
