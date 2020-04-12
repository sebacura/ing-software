package pantallas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tananaev.passportreader.LoginActivity;
import com.tananaev.passportreader.MainActivity;
import com.tananaev.passportreader.R;
import com.tananaev.passportreader.StatusActivity;

import java.util.ArrayList;

public class main_productos extends AppCompatActivity {

    private ListView lvItems;
    private Adaptador adaptador;
    Button btnIrFormulario;
    private int navBarItemId;
    private BottomNavigationView navBarItemView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_productos);

        lvItems = (ListView) findViewById(R.id.lvItems);
        adaptador = new Adaptador(this, GetArrayItems());
        lvItems.setAdapter(adaptador);

        //Pasar a siguiente pantalla
        btnIrFormulario = (Button) findViewById(R.id.btnIrFormulario);
        btnIrFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main_productos.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //Fin pasar a siguiente pantalla

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
                        Intent a = new Intent(main_productos.this, StatusActivity.class);
                        startActivity(a);
                        break;
                }
                return true;
            }
        });
        navigation.getMenu().findItem(R.id.action_main).setChecked(true);
    }

    private ArrayList<Entidad> GetArrayItems(){
        ArrayList<Entidad> listItems = new ArrayList<>();
        listItems.add(new Entidad(R.drawable.photo, "Tarjeta 1","Descripción"));
        listItems.add(new Entidad(R.drawable.photo, "Tarjeta 2","Descripción"));
        listItems.add(new Entidad(R.drawable.photo, "Tarjeta 3","Descripción"));
        listItems.add(new Entidad(R.drawable.photo, "Tarjeta 4","Descripción"));

        return listItems;
    }

    //LOGIN ITEM IN TOP BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_login:
                finish();
                Intent intent = new Intent(main_productos.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
