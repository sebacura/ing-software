package com.ingsoft.bancoapp.bankEmployer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;

import java.util.HashMap;
import java.util.Map;


public class RequestDetailActivity extends AppCompatActivity {
    private RequestItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        user = (RequestItem) getIntent().getSerializableExtra("user");
//        String value = getIntent().getStringExtra("user");
//        Toast.makeText(getApplicationContext(),user.getDeliveryAddress(), Toast.LENGTH_SHORT).show();
        ((TextView) findViewById(R.id.name)).setText(user.getFirstName()+user.getLastName());
        ((TextView) findViewById(R.id.product)).setText(user.getProductId());
        ((TextView) findViewById(R.id.ci)).setText(user.getCi());
        ((TextView) findViewById(R.id.address)).setText(user.getAddress());
//        ((TextView) findViewById(R.id.birth)).setText(user.getBirth());

        ((TextView) findViewById(R.id.deliveryAddress)).setText(user.getDeliveryAddress());
        ((TextView) findViewById(R.id.salary)).setText(user.getSalary());
        ((TextView) findViewById(R.id.date)).setText(user.getDate());

        Button btnAccept = (Button) findViewById(R.id.accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                errorMessage.setVisibility(View.GONE);
//                loadingLayout.setVisibility(View.VISIBLE);
                RequestUpdateState("Aprobada");
            }
        });

        Button btnRefuse = (Button) findViewById(R.id.refuse);
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                errorMessage.setVisibility(View.GONE);
//                loadingLayout.setVisibility(View.VISIBLE);
                RequestUpdateState("Rechazada");
            }
        });
    }

    public void RequestUpdateState(String state) {
        String url = "https://ingsoft-backend.herokuapp.com/applications/updateState";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(RequestDetailActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loadingLayout.setVisibility(View.GONE);
//                        errorMessage.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
//                EditText id   = (EditText)findViewById(R.id.username);

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("idSolicitude", user.getId());
                params.put("state", state);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }


}
