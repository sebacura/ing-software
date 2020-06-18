package com.ingsoft.bancoapp.bankEmployer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;
import com.ingsoft.bancoapp.bankEmployer.data.model.CustomListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    ArrayList<String> listItem=new ArrayList<String>();
    private Menu _menu;

    private ListView mListView;
    private ArrayAdapter aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
//        MenuItem logout = _menu.findItem(R.id.item_logout);
//        logout.setVisible(true);



        getListData();



    }
    private void getListData() {
            ArrayList<RequestItem> results = new ArrayList<>();

        String url = "https://ingsoft-backend.herokuapp.com/applications/pending";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        JSONArray pendings = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            pendings = new JSONArray(jsonObject.getString("applications"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0 ; i < pendings.length() ; i++){
                            try {

//                                Log.d("pendiente", pendings.getJSONObject(i).getString("id"));
                                RequestItem user = new RequestItem();
                                user.setId(pendings.getJSONObject(i).getString("id"));
                                user.setFirstName(pendings.getJSONObject(i).getString("personFirstName"));
                                user.setLastName(pendings.getJSONObject(i).getString("personLastName"));
                                user.setCi(pendings.getJSONObject(i).getString("personCedula"));
                                user.setAddress(pendings.getJSONObject(i).getString("personAddress"));
                                user.setSalary(pendings.getJSONObject(i).getString("personSalary"));
                                user.setDate(pendings.getJSONObject(i).getString("createdAt"));
                                user.setDeliveryAddress(pendings.getJSONObject(i).getString("personDeliveryAddress"));
                                user.setProductId(pendings.getJSONObject(i).getString("productId"));
                                user.setStateId(pendings.getJSONObject(i).getString("StateId"));
//                                user.setBirth(pendings.getJSONObject(i).getString(""));

                                results.add(user);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        final ListView lv = (ListView) findViewById(R.id.requestlist);
                        lv.setAdapter(new CustomListAdapter(DashboardActivity.this, results));
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(getApplicationContext(), RequestDetailActivity.class);
                                RequestItem user = (RequestItem) lv.getItemAtPosition(position);
                                intent.putExtra("user", (Serializable)user);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        });
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

        };
        Volley.newRequestQueue(this).add(getRequest);
    }

    //Logout ITEM IN TOP BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        _menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                finish();
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void RequestList(String state) {
        String url = "https://ingsoft-backend.herokuapp.com/applications";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String response) {

//                        arrayAdapterListView(response);
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

        };
        Volley.newRequestQueue(this).add(getRequest);
    }


}
