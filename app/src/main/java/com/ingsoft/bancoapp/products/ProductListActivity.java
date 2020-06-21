package com.ingsoft.bancoapp.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.ingsoft.bancoapp.bankEmployer.LoginActivity;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.applicationForm.ReadNfcActivity;
import com.ingsoft.bancoapp.myApplications.StatusActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ProductListActivity extends AppCompatActivity {


    private int navBarItemId;
    private BottomNavigationView navBarItemView;
    private MyExpandableListAdapter adapter;
    private ExpandableListView listView;
    SparseArray<GroupProducts> groups = new SparseArray<GroupProducts>();
    private int lastExpandedPosition = -1;
    View loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_productos);
        loadingLayout = findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.VISIBLE);
        getProductList(this);

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
                return false;
            }
        });
    }

    public void irAFormulario (View v){
        Log.d("View", v.toString());
        Intent intent = new Intent(getApplicationContext(), ReadNfcActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //LOGIN ITEM IN TOP BAR
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.item_login:
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getProductList (Activity activity) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://ingsoft-backend.herokuapp.com/product";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray products;
                    try {
                        products = response.getJSONArray("products");
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject product = products.getJSONObject(i);
                            String title = (String) product.get("name");
                            String image = (String) product.get("image");
                            String description = (String) product.get("descripcion");
                            String descriptionShort = (String) product.get("descriptionShort");
                            GroupProducts group = new GroupProducts(image, title, descriptionShort);
                            group.children.add(description);
                            groups.append(i, group);
                        }
                        loadingLayout.setVisibility(View.GONE);
                        listView = (ExpandableListView) findViewById(R.id.lvItems);
                        listView.setGroupIndicator(null);
                        adapter = new MyExpandableListAdapter(activity, groups);
                        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            @Override
                            public void onGroupExpand(int groupPosition) {
                                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                                    listView.collapseGroup(lastExpandedPosition);
                                }
                                lastExpandedPosition = groupPosition;
                            }
                        });
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        queue.add(request);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
