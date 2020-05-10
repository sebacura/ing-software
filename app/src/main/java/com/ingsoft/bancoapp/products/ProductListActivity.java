package com.ingsoft.bancoapp.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ingsoft.bancoapp.bankEmployer.LoginActivity;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.applicationForm.ReadNfcActivity;
import com.ingsoft.bancoapp.myApplications.StatusActivity;


public class ProductListActivity extends AppCompatActivity {


    private int navBarItemId;
    private BottomNavigationView navBarItemView;
    private MyExpandableListAdapter adapter;
    private ExpandableListView listView;
    SparseArray<GroupProducts> groups = new SparseArray<GroupProducts>();
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_productos);

        createData();

        listView = (ExpandableListView) findViewById(R.id.lvItems);
        listView.setGroupIndicator(null);

        adapter = new MyExpandableListAdapter(this, groups);


        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    listView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        listView.setAdapter(adapter);

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

        public void createData() {
            GroupProducts group = new GroupProducts(R.mipmap.creditcard_standard_foreground, "Tarjeta Standard", "Para las compras diarias y más allá.");
            group.children.add("Al tener más control de sus finanzas usted se siente más seguro. Es por ese motivo que su tarjeta Standard le provee de beneficios y servicios para ayudarle a realizar compras de manera más inteligente y más segura, ya sea en línea o no. Además le provee con diversas herramientas para optimizar el presupuesto.");
            groups.append(0, group);

            group = new GroupProducts(R.mipmap.tarjeta_gold_foreground, "Tarjeta Gold", "Convertir buenas ofertas en excelentes añadiendo valor a compras grandes y pequeñas con comodidad, protección y beneficios mejorados.");
            group.children.add("La tarjeta Gold es una tarjeta de crédito creada para facilitar su rutina diaria y simplificar los diferentes procesos de compra. Con ella usted podrá disfrutar más de su familia y su vida personal mientras ahorra gracias a sus beneficios únicos y ofertas.");
            groups.append(1, group);

            group= new GroupProducts(R.mipmap.tarjeta_platinum_foreground, "Tarjeta Platinum", "Te brinda la flexibilidad de explorar los lugares y los objetivos que más te importan.");
            group.children.add("La tarjeta Platinum te permite hacer más de lo que disfrutas mientras te proporciona tranquilidad y conveniencia. La combinación de aceptación mundial y grandes beneficios significa que tienes la libertad de hacer exactamente lo que quieras donde lo desees.");
            groups.append(2, group);

            group= new GroupProducts(R.mipmap.tarjeta_black_foreground, "Tarjeta Black", "Un sorprendente poder adquisitivo y las más exquisitas características y beneficios");
            group.children.add("La tarjeta Black es una tarjeta personalizada que te brinda el servicio necesario para experimentar los momentos más memorables de la vida. En cualquier momento y lugar del mundo.");
            groups.append(3, group);
    }

        public void irAFormulario (View v){
            Intent intent = new Intent(getApplicationContext(), ReadNfcActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }


        //LOGIN ITEM IN TOP BAR
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.login, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case R.id.item_login:
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }


    }
