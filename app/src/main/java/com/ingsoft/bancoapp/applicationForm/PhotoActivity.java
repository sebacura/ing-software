package com.ingsoft.bancoapp.applicationForm;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.myApplications.StatusActivity;
import com.ingsoft.bancoapp.products.ProductListActivity;
import com.ingsoft.bancoapp.tools.Tools;
import com.ingsoft.bancoapp.applicationForm.lifeProof.EyesActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class PhotoActivity extends AppCompatActivity {

    View btnTomarFoto;
    Button btnIrFormulario3;
    public String KEY_CAMERA_PHOTO_BASE64 = "";
    public Integer numberOfAttemps = 0;
    View loadingLayout;
    View errorMessage;
    private ImageView imageView;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);

        loadingLayout = findViewById(R.id.loading_layout);

        //Tomar foto desde app
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent();
                Intent intent = new Intent(PhotoActivity.this, EyesActivity.class);
                //intent.putExtra("PhotoActivityInstance", );
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                //finish();
            }
        });
        //Fin tomar foto desde app

        btnIrFormulario3 = (Button) findViewById(R.id.irFormulario3);
        btnIrFormulario3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLayout.setVisibility(View.VISIBLE);
               /* try {
                    requestPhotoComparison();
                } catch (Exception e) {
                    // This will catch any exception, because they are all descended from Exception
                    Log.d("Error", e.getMessage());
                }*/
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
        System.out.println("En Activity result");
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            System.out.println(resultCode);
            if(resultCode== 1) {//&& resultCode == RESULT_OK) {
//            Bitmap imageBitmap = null;
                Bundle extras = data.getExtras();
                //Bitmap imageBitmap = (Bitmap) extras.get("IMAGE");
                KEY_CAMERA_PHOTO_BASE64 = (String) extras.get("IMAGE"); //encodeImage(imageBitmap);
                try {
                    requestPhotoComparison();
                    findViewById(R.id.instruccionfoto).setVisibility(View.GONE);
                    findViewById(R.id.avisofoto).setVisibility(View.VISIBLE);
                    findViewById(R.id.irFormulario3).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //KEY_CAMERA_PHOTO_BASE64 = encodeImage(imageBitmap);
            }else{
                try {
                    findViewById(R.id.avisofotoError).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Bitmap rotateImage(Bitmap bitmap, int rotate){

        if (rotate != 0) {

            // Getting width & height of the given image.
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }

        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
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
        ContentValues values = new ContentValues();
//        imageUri = getContentResolver().insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    //        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("result").equals("true")) {
                                Intent intent = new Intent(getApplicationContext(), ApplicantDetailsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            } else {
                                findViewById(R.id.loading_layout).setVisibility(View.GONE);
                                findViewById(R.id.irFormulario3).setVisibility(View.GONE);

                                String positiveButton;
                                String message;
                                if (numberOfAttemps.equals(2)) {
                                    new AlertDialog.Builder(PhotoActivity.this)
                                            .setTitle("Ha ocurrido un error en la validación de su identidad.")
                                            .setMessage("Lo sentimos. No ha sido posible aceptar su solicitud.\n\nPorfavor contáctese para más información.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(0,0);
                                                }
                                            }).setCancelable(false).show();
                                } else {
                                    new AlertDialog.Builder(PhotoActivity.this)
                                        .setTitle("Ha ocurrido un error en la validación de su identidad.")
                                        .setMessage("Lo sentimos. Su solicitud no cumplió con los requisitos de validación.\n\nPorfavor contáctese para más información o intente nuevamente.")
                                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                findViewById(R.id.instruccionfoto).setVisibility(View.VISIBLE);
                                                findViewById(R.id.avisofoto).setVisibility(View.GONE);
                                                numberOfAttemps += 1;
                                            }
                                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                            }
                                    }).setCancelable(false).show();
                                }
                            }
                        } catch (Throwable tx) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseBody = null;
                        findViewById(R.id.loading_layout).setVisibility(View.GONE);
                        findViewById(R.id.irFormulario3).setVisibility(View.GONE);
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            if (numberOfAttemps.equals(2)) {
                                new AlertDialog.Builder(PhotoActivity.this)
                                    .setTitle("Ha ocurrido un error en la validación de su identidad.")
                                    .setMessage("Lo sentimos. No ha sido posible aceptar su solicitud.\n\nPorfavor contáctese para más información.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0,0);
                                        }
                                    }).setCancelable(false).show();
                            } else {
                                String positiveButton;
                                String message;
                                String title;

                                if (data.optString("code").equals("NO_FACE")) {
                                    title = "Ha ocurrido un error.";
                                    message = "Lo sentimos.\n\n Porfavor reintente tomándose una foto donde su rostro esté visible.";
                                    positiveButton = "Reintentar";
                                } else if (data.optString("code").equals("INTERNAL_SERVER_ERROR")) {
                                    title = "Servicio no disponible.";
                                    message = "Lo sentimos. Ha ocurrido un error inesperado.\n\nPorfavor reintente nuevamente o contactese para más información.";
                                    positiveButton = "Reintentar";
                                } else {
                                    title = "Un error inesperado ha ocurrido.";
                                    message = "Lo sentimos. Ha ocurrido un error inesperado.\n\nPorfavor reintente nuevamente o contactese para más información.";
                                    positiveButton = "Reintentar";
                                }
                                new AlertDialog.Builder(PhotoActivity.this)
                                    .setTitle(title)
                                    .setMessage(message)
                                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            findViewById(R.id.instruccionfoto).setVisibility(View.VISIBLE);
                                            findViewById(R.id.avisofoto).setVisibility(View.GONE);
                                            numberOfAttemps +=1;
                                        }
                                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0,0);
                                    }
                                }).setCancelable(false).show();
                            }
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(postRequest);
    }
}