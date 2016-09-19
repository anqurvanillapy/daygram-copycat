package com.anqurvanillapy.daygramcopycat;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<HashMap<String, String>> entryData = new ArrayList<>();
        HashMap<String, String> item;
        item = new HashMap<>();
        item.put("dayOfWeek", "MON");
        item.put("date", "1");
        item.put("abstract", "Blue Mondays");
        entryData.add(item);
        item = new HashMap<>();
        item.put("dayOfWeek", "TUE");
        item.put("date", "2");
        item.put("abstract", "Today is good.");
        entryData.add(item);

        String[] entryFrom = {"dayOfWeek", "date", "abstract"};
        int[] entryTo = {R.id.itemDayOfWeek, R.id.itemDate, R.id.entryAbstract};

        ListView entryListView = (ListView) findViewById(R.id.entryListView);
        SimpleAdapter entryAdapter = new SimpleAdapter(this,
                entryData,
                R.layout.entry_adapter,
                entryFrom, entryTo);
        entryListView.setAdapter(entryAdapter);
    }
}