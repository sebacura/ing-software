package com.ingsoft.bancoapp.bankEmployer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.ingsoft.bancoapp.R;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);
//        MenuItem logout = _menu.findItem(R.id.item_logout);
//        logout.setVisible(true);

        Map<String, String> datum = new HashMap<String, String>(2);
        Map<String, String> datum2 = new HashMap<String, String>(2);
        Map<String, String> datum3 = new HashMap<String, String>(2);
        Map<String, String> datum4 = new HashMap<String, String>(2);
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();



        datum.put("name", "Juan Perez");
        datum.put("detail", "Platinum | 22/05/2020 | 47976654");

        datum2.put("name", "Luis Rodriguez");
        datum2.put("detail", "Platinum | 25/05/2020 | 41234567");

        datum3.put("name", "José Gonzalez");
        datum3.put("detail", "Gold | 28/05/2020 | 35798554");

        datum4.put("name", "Maria Pereira");
        datum4.put("detail", "Black | 30/05/2020 | 11181188");
        data.add(datum);
        data.add(datum2);
        data.add(datum3);
        data.add(datum4);


        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"name", "detail"},
                new int[] {android.R.id.text1,
                        android.R.id.text2 });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String value = adapter.getItem(position).toString();
                Intent intent = new Intent(getApplicationContext(), RequestDetailActivity.class);
                    intent.putExtra("data", value);
                startActivity(intent);
                overridePendingTransition(0,0);

//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + value, Toast.LENGTH_LONG)
//                        .show();
            }
        });
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

//    private void arrayAdapterListView(String response) throws JSONException {
//        listView=(ListView)findViewById(R.id.listView);
//        textView=(TextView)findViewById(R.id.textView);
////        MenuItem logout = _menu.findItem(R.id.item_logout);
////        logout.setVisible(true);
//
//
//        JSONObject responseJson = new JSONObject(response);
//        responseJson = responseJson.get("applications");
//        Iterator<?> keys = responseJson.keys();
//        while(keys.hasNext() ) {
//            String key = (String)keys.next();
//            if ( resobj.get(key) instanceof JSONObject ) {
//                JSONObject xx = new JSONObject(resobj.get(key).toString());
//                Log.d("res1",xx.getString("something"));
//                Log.d("res2",xx.getString("something2"));
//            }
//        }
//        Map<String, String> datum = new HashMap<String, String>(2);
//        Map<String, String> datum2 = new HashMap<String, String>(2);
//
//        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
//
//        JSONObject json = new JSONObject();
//        try {
//            json.put("name", "student");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        datum.put("name", "Juan Perez");
////        datum.put("detail", json.t);
////        datum.put("date", "20/05/2020");
////        datum.put("ci", "47976654");
//
//        datum2.put("name", "Luis Rodriguez");
//        datum2.put("detail", "Platino | 25/05/2020 | 41234567");
////        data.add(datum);
//        data.add(datum2);
//
//
//        SimpleAdapter adapter = new SimpleAdapter(this, data,
//                android.R.layout.simple_list_item_2,
//                new String[] {"name", "detail"},
//                new int[] {android.R.id.text1,
//                        android.R.id.text2 });
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                String value = adapter.getItem(position).toString();
//                Intent intent = new Intent(getApplicationContext(), RequestDetailActivity.class);
//                intent.putExtra("data", value);
//                startActivity(intent);
//                overridePendingTransition(0,0);
//
////                Toast.makeText(getApplicationContext(),
////                        "Click ListItem Number " + value, Toast.LENGTH_LONG)
////                        .show();
//            }
//        });
//
//    }

}
