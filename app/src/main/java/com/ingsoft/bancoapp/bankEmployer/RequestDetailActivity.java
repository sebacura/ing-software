package com.ingsoft.bancoapp.bankEmployer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.ingsoft.bancoapp.R;


public class RequestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        String value = getIntent().getStringExtra("data");
        Toast.makeText(getApplicationContext(),value, Toast.LENGTH_SHORT).show();
    }


}
