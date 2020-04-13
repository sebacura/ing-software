package com.ingsoft.bancoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    View loadingLayout;

    View errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingLayout = findViewById(R.id.loading_layout);
        errorMessage = findViewById(R.id.error_message);
        //Login
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                RealizarPost();
            }
        });
    }

    public void RealizarPost() {

        String url = "https://ingsoft-backend.herokuapp.com/login";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        EditText username   = (EditText)findViewById(R.id.username);
                        setContentView(R.layout.activity_dashboard);

                        ((TextView)findViewById(R.id.TextResult)).setText("Bienvenido " +(username.getText().toString()));
//                        finish();
//                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingLayout.setVisibility(View.GONE);
                        errorMessage.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                EditText username   = (EditText)findViewById(R.id.username);
                EditText password = (EditText)findViewById(R.id.password);

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}
