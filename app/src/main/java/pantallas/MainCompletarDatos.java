package pantallas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
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

        et1 = findViewById(R.id.txt_sueldo);
        et2=findViewById(R.id.txt_direcc);

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
}
