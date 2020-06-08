package com.ingsoft.bancoapp.applicationForm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.myApplications.StatusActivity;

public class SuccessActivity extends AppCompatActivity {
    View click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ((TextView) findViewById(R.id.response)).setText(getIntent().getStringExtra("state"));

        click = (View)findViewById(R.id.click);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intent);

                overridePendingTransition(0,0);
            }
        });
    }


}
