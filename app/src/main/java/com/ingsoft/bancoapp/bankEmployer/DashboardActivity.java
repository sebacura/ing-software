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

import com.ingsoft.bancoapp.R;

import java.util.ArrayList;
import java.util.HashMap;
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

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        String[] from = { "php_key","c_key","android_key","hacking_key" };

        datum.put("name", "Juan Perez");
        datum.put("detail", "Platino | 20/05/2020 | 47976654");
//        datum.put("date", "20/05/2020");
//        datum.put("ci", "47976654");

        datum2.put("name", "Luis Rodriguez");
        datum2.put("detail", "Platino | 25/05/2020 | 41234567");
        data.add(datum);
        data.add(datum2);


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
//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + value, Toast.LENGTH_LONG)
//                        .show();
            }
        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // TODO Auto-generated method stub
//                Sting value = adapter.getItem(position);
//                Intent intent = new Intent(getApplicationContext(), RequestDetailActivity.class);
//                intent.putExtra("data", (Bundle) value);
//                startActivity(intent);
//
//
//            }
//        });
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


}
