package com.ingsoft.bancoapp.bankEmployer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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
import com.ingsoft.bancoapp.applicationForm.PhotoActivity;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;
import com.ingsoft.bancoapp.products.ProductListActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestDetailActivity extends AppCompatActivity {
    private RequestItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);
        user = (RequestItem) getIntent().getSerializableExtra("user");
        ((TextView) findViewById(R.id.name)).setText(user.getFirstName());
        ((TextView) findViewById(R.id.lastName)).setText(user.getLastName());
        ((TextView) findViewById(R.id.product)).setText(user.getProductId());
        ((TextView) findViewById(R.id.ci)).setText(user.getCi());
        ((TextView) findViewById(R.id.address)).setText(user.getAddress());
//        ((TextView) findViewById(R.id.birth)).setText(user.getBirth());
        ((TextView) findViewById(R.id.deliveryAddress)).setText(user.getDeliveryAddress());
        ((TextView) findViewById(R.id.salary)).setText(user.getSalary());
        ((TextView) findViewById(R.id.date)).setText(user.getDate()) ;
        Button btnAccept = (Button) findViewById(R.id.accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                errorMessage.setVisibility(View.GONE);
//                loadingLayout.setVisibility(View.VISIBLE);
                EditText input = new EditText(RequestDetailActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setLines(3);
                input.setMaxLines(3);
                input.setGravity(Gravity.LEFT | Gravity.TOP);
                new AlertDialog.Builder(RequestDetailActivity.this)
                        .setTitle("Estas seguro que deseas aceptar esta solicitud?")
                        .setMessage("La solicitud del usuario será aprobada.")
                        .setView(input)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String comment = input.getText().toString(); // comentario para mandar al back

                                // approve request
                                RequestUpdateState("Aprobada");
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        Button btnRefuse = (Button) findViewById(R.id.refuse);
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                errorMessage.setVisibility(View.GONE);
//                loadingLayout.setVisibility(View.VISIBLE);
                EditText input = new EditText(RequestDetailActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setLines(3);
                input.setMaxLines(3);
                input.setGravity(Gravity.LEFT | Gravity.TOP);
                new AlertDialog.Builder(RequestDetailActivity.this)
                        .setTitle("Estas seguro que deseas rechazar esta solicitud?")
                        .setMessage("La solicitud del usuario será rechazada.")
                        .setView(input)
                        .setPositiveButton("Rechazar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String comment = input.getText().toString(); // comentario para mandar al back

                                // approve request
                                RequestUpdateState("Rechazada");
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
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
