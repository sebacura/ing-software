package com.ingsoft.bancoapp.applicationForm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.myApplications.StatusActivity;
import com.ingsoft.bancoapp.tools.Tools;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoActivity extends AppCompatActivity {

    View btnTomarFoto;
    Button btnIrFormulario3;

    public String KEY_CAMERA_PHOTO_BASE64 = "";

    View loadingLayout;
    View errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        loadingLayout = findViewById(R.id.loading_layout);
        //Tomar foto desde app
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        //Fin tomar foto desde app

        btnIrFormulario3 = (Button)findViewById(R.id.irFormulario3);
        btnIrFormulario3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLayout.setVisibility(View.VISIBLE);
                try {
                    requestPhotoComparison();
                } catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    Log.d("Error", e.getMessage());
                    Tools.exceptionToast(getApplicationContext(), "Service unavailable");
                }
            }
        });


        // bottom nav bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.action_main);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_main:
                        return true;
                    case R.id.action_status:
                        Intent a = new Intent(getApplicationContext(), StatusActivity.class);
                        startActivity(a);
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            KEY_CAMERA_PHOTO_BASE64 = encodeImage(imageBitmap);
        }
    }
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void requestPhotoComparison() {
        String url = "https://ingsoft-backend.herokuapp.com/applications/assets";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        // TO DO: add check logic if backend's answer is success: true or false,
                        // if false, show message to user that his photo wasn't good enough to comparison
                        // or the error that causes the success:false,
                        // if success:true, it means that the comparison of photos was ok.
                        Intent intent = new Intent(getApplicationContext(), ApplicantDetailsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
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
                Map<String, String>  params = new HashMap<>();
//                Log.d("a",getIntent().getStringExtra(KEY_CI));
//                Log.d("b",getIntent().getStringExtra("CI_PHOTO_BASE_64"));
                params.put("userIdCardNumber", getIntent().getStringExtra("KEY_CI"));
                params.put("fotoCedula", getIntent().getStringExtra("CI_PHOTO_BASE_64"));
                params.put("fotoSelfie", KEY_CAMERA_PHOTO_BASE64);
//                Log.d("C",params.toString());

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}