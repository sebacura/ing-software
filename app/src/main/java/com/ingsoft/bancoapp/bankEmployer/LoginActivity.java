package com.ingsoft.bancoapp.bankEmployer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.products.ProductListActivity;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    View loadingLayout;
    View errorMessage;
    private Menu menu;

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
        String url = "https://ingsoft-backend.herokuapp.com/bank/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        EditText username = (EditText) findViewById(R.id.username);
                        OneSignal.setExternalUserId(username.getText().toString());
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String token = null;
                        try {
                             token = jsonObject.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear(); //remove old keys
                        editor.putString("token", token);
                        //commits your edits
                        editor.commit();

//                        ((TextView)findViewById(R.id.TextResult)).setText("Bienvenido " +(username.getText().toString()));
//                        finish();
                        loadingLayout.setVisibility(View.GONE);

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login  , menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }

}
