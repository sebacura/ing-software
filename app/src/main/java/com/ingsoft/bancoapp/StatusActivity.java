package com.ingsoft.bancoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kofigyan.stateprogressbar.StateProgressBar;

import pantallas.main_productos;

public class StatusActivity extends AppCompatActivity {
    String[] descriptionData = {"En\nproceso", "Aprobado", "En\nimpresión", "En\ncamino","Entregado"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.status_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

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
                    case R.id.action_main:
                        Intent a = new Intent(StatusActivity.this, main_productos.class);
                        startActivity(a);
                        break;
                }
                return true;
            }
        });
        navigation.getMenu().findItem(R.id.action_status).setChecked(true);
    }
}
