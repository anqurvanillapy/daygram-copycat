/**
 * FIXME:
 * - [ ] All exceptions are generalized
 */

package com.anqurvanillapy.daygramcopycat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    /**
     * =============================================================================================
     * Custom ListView Item Adapter
     * =============================================================================================
     */

    public class EntryItemType {
        final public static int EMPTY = 0;
        final public static int EMPTY_SUNDAY = 1;
        final public static int NONEMPTY = 2;
        final public static int NONEMPTY_SUNDAY = 3;
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

    public class EntryEmptySundayObject {
        String name;

        public EntryEmptySundayObject(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public class EntryNonemptyObject {
        String name;

        public EntryNonemptyObject(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public class EntryNonemptySundayObject {
        String name;

        public EntryNonemptySundayObject(String name) { this.name = name; }
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
            return 4;
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
                case EntryItemType.EMPTY_SUNDAY:
                    EntryEmptySundayObject entryEmptySundayObject = (EntryEmptySundayObject) entryObject;
                    ViewHolderEntryEmptySunday emptySundayHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_empty_red, null, false);
                        emptySundayHolder = new ViewHolderEntryEmptySunday(convertView);
                        convertView.setTag(emptySundayHolder);
                    } else {
                        emptySundayHolder = (ViewHolderEntryEmptySunday) convertView.getTag();
                    }

                    emptySundayHolder.getEntryEmptySunday().setText(entryEmptySundayObject.getName());
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
                case EntryItemType.NONEMPTY_SUNDAY:
                    EntryNonemptySundayObject entryNonemptySundayObject = (EntryNonemptySundayObject) entryObject;
                    ViewHolderEntryNonemptySunday nonemptySundayHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_red, null, false);
                        nonemptySundayHolder = new ViewHolderEntryNonemptySunday(convertView);
                        convertView.setTag(nonemptySundayHolder);
                    } else {
                        nonemptySundayHolder = (ViewHolderEntryNonemptySunday) convertView.getTag();
                    }

                    nonemptySundayHolder.getEntryNonemptySunday().setText(entryNonemptySundayObject.getName());
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

        private class ViewHolderEntryEmptySunday {
            private View row;
            private TextView entryEmptySunday;

            public ViewHolderEntryEmptySunday(View row) {
                this.row = row;
            }

            public TextView getEntryEmptySunday() {
                if (this.entryEmptySunday == null) {
                    this.entryEmptySunday = (TextView) row.findViewById(R.id.entryEmptyLabel);
                }

                return this.entryEmptySunday;
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

        private class ViewHolderEntryNonemptySunday {
            private View row;
            private TextView entryNonemptySunday;

            public ViewHolderEntryNonemptySunday(View row) {
                this.row = row;
            }

            public TextView getEntryNonemptySunday() {
                if (this.entryNonemptySunday == null) {
                    this.entryNonemptySunday = (TextView) row.findViewById(R.id.entryAbstract);
                }

                return this.entryNonemptySunday;
            }
        }
    }

    /**
     * =============================================================================================
     * ListView Item Generator
     * =============================================================================================
     */

    public ArrayList<EntryItem> listItemGenerator(String year, String month) {
        ArrayList<EntryItem> entryItems = new ArrayList<>();
        Context context = getApplicationContext();
        File entryPath = new File(context.getFilesDir(), "entries");
        File entryAbstract = new File(entryPath,
                year+ File.separator + month + File.separator + "abstract");
        entryPath.mkdirs();

        Calendar cal = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (entryAbstract.exists()) {
            try {
                JSONObject jsonAbstract = new JSONObject(loadJSONFromFile(entryAbstract));
                String stringAbstract;
                String key;
                for (int i = 1; i <= daysInMonth; i++) {
                    cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, i);
                    key = String.format(year + month + "%02d", i);

                    if (jsonAbstract.has(key)) {
                        stringAbstract = jsonAbstract.getString(key);
                        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            entryItems.add(new EntryItem(EntryItemType.NONEMPTY,
                                    new EntryNonemptyObject(stringAbstract)));
                        } else {
                            entryItems.add(new EntryItem(EntryItemType.NONEMPTY_SUNDAY,
                                    new EntryNonemptySundayObject(stringAbstract)));
                        }
                    } else {
                        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            entryItems.add(new EntryItem(EntryItemType.EMPTY,
                                    new EntryEmptyObject(year + month + i)));
                        } else {
                            entryItems.add(new EntryItem(EntryItemType.EMPTY_SUNDAY,
                                    new EntryEmptySundayObject(year + month + i)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 1; i <= daysInMonth; i++) {
                cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, i);

                if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    entryItems.add(new EntryItem(EntryItemType.EMPTY,
                            new EntryEmptyObject(year + month + i)));
                } else {
                    entryItems.add(new EntryItem(EntryItemType.EMPTY_SUNDAY,
                            new EntryEmptySundayObject(year + month + i)));
                }
            }
        }

        return entryItems;
    }

    /**
     * =============================================================================================
     * JSON File Loader
     * =============================================================================================
     */

     public String loadJSONFromFile(File file) {
         int size;
         byte[] buffer;
         InputStream is;
         String jsonString = null;

         try {
             is = new FileInputStream(file);
             size = is.available();
             buffer = new byte[size];
             is.read(buffer);
             is.close();
             jsonString = new String(buffer, "UTF-8");
         } catch (Exception e) {
             e.printStackTrace();
         }

         return jsonString;
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * =========================================================================================
         * DateTime Handlers
         * =========================================================================================
         */

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();

        DateFormat monthName = new SimpleDateFormat("MMMM");
        DateFormat month = new SimpleDateFormat("MM");
        DateFormat year = new SimpleDateFormat("yyyy");

        String currentYear = year.format(currentLocalTime);
        String currentMonth = month.format(currentLocalTime);
        String currentMonthName = monthName.format(currentLocalTime).toUpperCase();

        /**
         * =========================================================================================
         * UI Components (w/ Initialization)
         * =========================================================================================
         */

        final TextView buttonTodayEntry = (TextView) findViewById(R.id.buttonTodayEntry);
        TextView labelMonth = (TextView) findViewById(R.id.labelMonth);
        TextView labelYear = (TextView) findViewById(R.id.labelYear);
        labelMonth.setText(currentMonthName);
        labelYear.setText(currentYear);

        /**
         * =========================================================================================
         * ListView Handlers
         * =========================================================================================
         */

        ArrayList<EntryItem> _entryItems = listItemGenerator(currentYear, currentMonth);
        EntryAdapter entryAdapter = new EntryAdapter(this, _entryItems);
        ListView entryListView = (ListView) findViewById(R.id.entryListView);
        entryListView.setAdapter(entryAdapter);

        /**
         * =========================================================================================
         * EventListeners
         * =========================================================================================
         */

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentEntryItemEditor = new Intent(MainActivity.this, EntryEditorActivity.class);
                intentEntryItemEditor.putExtra("date", "20160921");
                intentEntryItemEditor.putExtra("state", EntryEditorActivity.EditorState.VIEW);
                MainActivity.this.startActivity(intentEntryItemEditor);
            }
        });

        buttonTodayEntry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentEntryEditor = new Intent(MainActivity.this, EntryEditorActivity.class);
                intentEntryEditor.putExtra("date", "20160921");
                intentEntryEditor.putExtra("state", EntryEditorActivity.EditorState.EDIT);
                MainActivity.this.startActivity(intentEntryEditor);
            }
        });
    }
}