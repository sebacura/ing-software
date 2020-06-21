package com.ingsoft.bancoapp.bankEmployer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.applicationForm.PhotoActivity;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;
import com.ingsoft.bancoapp.products.ProductListActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        ((TextView) findViewById(R.id.product)).setText(user.getProductName());
        ((TextView) findViewById(R.id.ci)).setText(user.getCi());
        ((TextView) findViewById(R.id.address)).setText(user.getAddress());
        ((TextView) findViewById(R.id.deliveryAddress)).setText(user.getDeliveryAddress());
        ((TextView) findViewById(R.id.salary)).setText(user.getSalary());
        ((TextView) findViewById(R.id.date)).setText(user.getDate()) ;

        ImageView imgFoto = (ImageView) findViewById(R.id.salaryPhoto);
        Log.d("Foto salario", user.getSalaryPhoto());
        Picasso.get().load(user.getSalaryPhoto()).into(imgFoto);

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
                                String comment = input.getText().toString();
                                // approve request
                                RequestUpdateState("Aprobada", comment);
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
                                String comment = input.getText().toString();
                                // approve request
                                RequestUpdateState("Rechazada", comment);
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

    public void RequestUpdateState(String state, String comment) {
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
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<String, String> ();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(RequestDetailActivity.this);
                String token = sharedPref.getString("token", "");
                headers.put("Authorization", "bearer " + token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {
//                EditText id   = (EditText)findViewById(R.id.username);

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("idSolicitude", user.getId());
                params.put("state", state);
                params.put("comment", comment);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

}
