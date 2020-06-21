package com.ingsoft.bancoapp.bankEmployer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Dash;
import com.google.gson.JsonObject;
import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.bankEmployer.data.RequestItem;
import com.ingsoft.bancoapp.bankEmployer.data.model.CustomListAdapter;
import com.otaliastudios.cameraview.video.VideoRecorder;

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
    private View loadingLayout;
    private View isEmpty;

    private Spinner opciones;
    private String currentState;
    private String productName=null;
    private ArrayList<RequestItem> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        loadingLayout = findViewById(R.id.loading_layout);
        isEmpty = findViewById(R.id.empty);
        textView=(TextView)findViewById(R.id.textView);
//        MenuItem search = _menu.findItem(R.id.search);
//        search.setVisible(true);
        currentState = "getAllApplications";

        opciones= (Spinner)findViewById(R.id.filtro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opciones, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opciones.setAdapter(adapter);
        String[] states = getResources().getStringArray(R.array.opciones);

        opciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();
                if (states[index].equals("Mostrar pendientes")) {
                    currentState = "pending";
                } else if (states[index].equals("Mostrar aprobadas")) {
                    currentState = "approved";
                } else {
                    currentState = "getAllApplications";
                }
                getListData(currentState);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    private void getListData(String state) {
        loadingLayout.setVisibility(View.VISIBLE);
        String url = "https://ingsoft-backend.herokuapp.com/applications/"+state;
        results = new ArrayList<>();
        final ListView lv = (ListView) findViewById(R.id.requestlist);
        lv.setAdapter(new CustomListAdapter(DashboardActivity.this, results));

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
                        if (pendings!=null) {
                            isEmpty.setVisibility(View.GONE);
                            for (int i = 0; i < pendings.length(); i++) {
                                try {
//                                Log.d("pendiente", pendings.getJSONObject(i).getString("id"));
                                    RequestItem user = new RequestItem();
//                                    getProduct(pendings.getJSONObject(i).getString("id"));

                                    JSONObject product = pendings.getJSONObject(i).getJSONObject("product");
                                    user.setId(pendings.getJSONObject(i).getString("id"));
                                    user.setFirstName(pendings.getJSONObject(i).getString("personFirstName"));
                                    user.setLastName(pendings.getJSONObject(i).getString("personLastName"));
                                    user.setCi(pendings.getJSONObject(i).getString("personCedula"));
                                    user.setAddress(pendings.getJSONObject(i).getString("personAddress"));
                                    user.setSalary(pendings.getJSONObject(i).getString("personSalary"));
                                    user.setDate(pendings.getJSONObject(i).getString("createdAt"));
                                    user.setDeliveryAddress(pendings.getJSONObject(i).getString("personDeliveryAddress"));
                                    user.setProductId(pendings.getJSONObject(i).getString("productId"));
                                    user.setProductName(product.getString("name"));
                                    user.setStateId(pendings.getJSONObject(i).getString("StateId"));
                                    user.setSalaryPhoto(pendings.getJSONObject(i).getString("personSalaryPhoto"));
//                                user.setBirth(pendings.getJSONObject(i).getString(""));

                                    results.add(user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            isEmpty.setVisibility(View.VISIBLE);
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
                        loadingLayout.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingLayout.setVisibility(View.GONE);
                        isEmpty.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<String, String> ();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                String token = sharedPref.getString("token", "");
                headers.put("Authorization", "bearer " + token);
                return headers;
            }

        };
        Volley.newRequestQueue(this).add(getRequest);
    }

    //Search ITEM IN TOP BAR



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Buscar por apellido");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                if(query.equals("")){
                    getListData(currentState);
                }else{
                    FilterList(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    getListData(currentState);
                }
                return false;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    public void FilterList(String lastName) {
        Log.d("last name", lastName);
        String url = "https://ingsoft-backend.herokuapp.com/applications/pendingByName?clientLastName="+lastName;
        Log.d("url", url);
        loadingLayout.setVisibility(View.VISIBLE);
        results = new ArrayList<>();
        final ListView lv = (ListView) findViewById(R.id.requestlist);
        lv.setAdapter(new CustomListAdapter(DashboardActivity.this, results));
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
                            pendings = new JSONArray(jsonObject.getString("applicationsByName"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (pendings!=null) {
                            isEmpty.setVisibility(View.GONE);
                            for (int i = 0; i < pendings.length(); i++) {
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
                        } else {
                            isEmpty.setVisibility(View.VISIBLE);
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
                        loadingLayout.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingLayout.setVisibility(View.GONE);
                        isEmpty.setVisibility(View.VISIBLE);
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<String, String> ();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);
                String token = sharedPref.getString("token", "");
                headers.put("Authorization", "bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(getRequest);
    }
}
