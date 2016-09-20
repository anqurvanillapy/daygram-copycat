package com.anqurvanillapy.daygramcopycat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public class EntryItemType {
        final public static int EMPTY = 0;
        final public static int NONEMPTY = 1;
    }

    public class EntryItem {
        int type;
        Object object;

        public EntryItem(int type, Object object) {
            this.type = type;
            this.object = object;
        }

        public int getType() {
            return type;
        }
        public void setType(int type) {
            this.type = type;
        }
        public Object getObject() {
            return object;
        }
        public void setObject(Object object) {
            this.object = object;
        }
    }

    public class EntryEmptyObject {
        String name;

        public EntryEmptyObject(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public class EntryNonemptyObject {
        String name;

        public EntryNonemptyObject(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public class EntryAdapter extends BaseAdapter {
        Activity activity;
        ArrayList<EntryItem> entryItems;

        public EntryAdapter(Activity activity, ArrayList<EntryItem> entryItems) {
            this.activity = activity;
            this.entryItems = entryItems;
        }

        @Override
        public EntryItem getItem(int position) {
            return entryItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return entryItems.get(position).getType();
        }

        @Override
        public int getCount() {
            return entryItems.size();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup viewGroup) {
            LayoutInflater inflater = activity.getLayoutInflater();
            Object entryObject = null;
            entryObject = entryItems.get(position).getObject();

            switch (getItemViewType(position)) {
                case EntryItemType.EMPTY:
                    EntryEmptyObject entryEmptyObject = (EntryEmptyObject) entryObject;
                    ViewHolderEntryEmpty emptyHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_empty, null, false);
                        emptyHolder = new ViewHolderEntryEmpty(convertView);
                        convertView.setTag(emptyHolder);
                    } else {
                        emptyHolder = (ViewHolderEntryEmpty) convertView.getTag();
                    }

                    emptyHolder.getEntryEmpty().setText(entryEmptyObject.getName());
                    return convertView;
                case EntryItemType.NONEMPTY:
                    EntryNonemptyObject entryNonemptyObject = (EntryNonemptyObject) entryObject;
                    ViewHolderEntryNonempty nonemptyHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter, null, false);
                        nonemptyHolder = new ViewHolderEntryNonempty(convertView);
                        convertView.setTag(nonemptyHolder);
                    } else {
                        nonemptyHolder = (ViewHolderEntryNonempty) convertView.getTag();
                    }

                    nonemptyHolder.getEntryNonempty().setText(entryNonemptyObject.getName());
                    return convertView;
            }

            return null;
        }

        private class ViewHolderEntryEmpty {
            private View row;
            private TextView entryEmpty;

            public ViewHolderEntryEmpty(View row) {
                this.row = row;
            }

            public TextView getEntryEmpty() {
                if (this.entryEmpty == null) {
                    this.entryEmpty = (TextView) row.findViewById(R.id.entryEmptyLabel);
                }

                return this.entryEmpty;
            }
        }

        private class ViewHolderEntryNonempty {
            private View row;
            private TextView entryNonempty;

            public ViewHolderEntryNonempty(View row) {
                this.row = row;
            }

            public TextView getEntryNonempty() {
                if (this.entryNonempty == null) {
                    this.entryNonempty = (TextView) row.findViewById(R.id.entryAbstract);
                }

                return this.entryNonempty;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<EntryItem> entryItems = new ArrayList<>();
        entryItems.add(new EntryItem(EntryItemType.EMPTY, new EntryEmptyObject("WTF")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.EMPTY, new EntryEmptyObject("WTF")));
        entryItems.add(new EntryItem(EntryItemType.EMPTY, new EntryEmptyObject("WTF")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));
        entryItems.add(new EntryItem(EntryItemType.NONEMPTY, new EntryNonemptyObject("Fuck")));

        EntryAdapter entryAdapter = new EntryAdapter(this, entryItems);
        ListView entryListView = (ListView) findViewById(R.id.entryListView);
        entryListView.setAdapter(entryAdapter);

        final TextView buttonTodayEntry = (TextView) findViewById(R.id.buttonTodayEntry);
        buttonTodayEntry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentEntryEditor = new Intent(MainActivity.this, EntryEditorActivity.class);
                intentEntryEditor.putExtra("date", "oh shit");
                MainActivity.this.startActivity(intentEntryEditor);
            }
        });

//        File entry_file = new File(getApplicationContext().getFilesDir(), "Dgcpcat");
//        String entry_filename = "hello";
//        String entry_content = "what a day";
//        FileOutputStream fos;
//
//        try {
//            fos = openFileOutput(entry_filename, Context.MODE_PRIVATE);
//            fos.write(entry_content.getBytes());
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}