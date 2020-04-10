package pantallas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.tananaev.passportreader.R;

public class MainCompletarDatos extends AppCompatActivity {

    private EditText et1;
    private EditText et2;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_completar_datos);

        et1 = (EditText)findViewById(R.id.txt_sueldo);
        et2 = (EditText)findViewById(R.id.txt_direcc);

        String v1= et1.getText().toString();
        int s = Integer.parseInt(v1);

        if(s>10000){
            Toast.makeText(getApplicationContext(), "Su sueldo no es suficiente para solicitar una tarjeta de crédito, disculpas",Toast.LENGTH_LONG ).show();
        }
    }
}
