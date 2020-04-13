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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.action_status);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_status:
                        return true;
                    case R.id.action_main:
                        Intent a = new Intent(getApplicationContext(), main_productos.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}
