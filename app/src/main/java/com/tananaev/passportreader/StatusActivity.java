package com.tananaev.passportreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kofigyan.stateprogressbar.StateProgressBar;

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
                switch (item.getItemId()) {
                    case R.id.action_main:
                        Intent a = new Intent(StatusActivity.this, MainActivity.class);
                        startActivity(a);
                        break;
                }
                return false;
            }
        });
    }
}
