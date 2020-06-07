package com.ingsoft.bancoapp.bankEmployer;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ingsoft.bancoapp.R;
import com.ingsoft.bancoapp.products.GroupProducts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    ArrayList<String> listItem=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        listView=(ListView)findViewById(R.id.listView);
        textView=(TextView)findViewById(R.id.textView);


        Map<String, String> datum = new HashMap<String, String>(2);
        Map<String, String> datum2 = new HashMap<String, String>(2);

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

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

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // TODO Auto-generated method stub
//                String value=adapter.getItem(position);
//                Toast.makeText(getApplicationContext(),value, Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }


}
