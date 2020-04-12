/*
 * Copyright 2016 - 2017 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tananaev.passportreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pantallas.MainCompletarDatos;
import pantallas.main_productos;

public class ResultActivity extends AppCompatActivity {

    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_STATE = "state";
    public static final String KEY_NATIONALITY = "nationality";
    public static final String KEY_CI = "ci";

//    public static final String KEY_PHOTO = "photo";
//    public static final String KEY_PHOTO_BASE64 = "photoBase64";

    Button btnIrFormulario2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.campos).setVisibility(View.GONE);
        findViewById(R.id.resultados).setVisibility(View.VISIBLE);


        ((TextView) findViewById(R.id.output_first_name)).setText(getIntent().getStringExtra(KEY_FIRST_NAME));
        ((TextView) findViewById(R.id.output_last_name)).setText(getIntent().getStringExtra(KEY_LAST_NAME));
//        ((TextView) findViewById(R.id.output_gender)).setText(getIntent().getStringExtra(KEY_GENDER));
//        ((TextView) findViewById(R.id.output_state)).setText(getIntent().getStringExtra(KEY_STATE));
//        ((TextView) findViewById(R.id.output_nationality)).setText(getIntent().getStringExtra(KEY_NATIONALITY));
        ((TextView) findViewById(R.id.output_ci)).setText(getIntent().getStringExtra(KEY_CI));

        btnIrFormulario2 = (Button)findViewById(R.id.irFormulario2);

        btnIrFormulario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainCompletarDatos.class);
                startActivity(intent);
            }
        });
//        if (getIntent().hasExtra(KEY_PHOTO)) {
//            ((ImageView) findViewById(R.id.view_photo)).setImageBitmap((Bitmap) getIntent().getParcelableExtra(KEY_PHOTO));
//        }

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
                        Intent a = new Intent(ResultActivity.this, StatusActivity.class);
                        startActivity(a);
                        break;
                }
                return true;
            }
        });
        navigation.getMenu().findItem(R.id.action_main).setChecked(true);

    }

}
