/**
 * FIXME:
 * - [ ] All exceptions are generalized
 * - [ ] Item views adaption is trivial
 * - [ ] Accesses of methods
 * - [ ] Content abstract algorithm is not desirable
 * TODO:
 * - [ ] Spannable is decent for colorful parts in TextView
 */

package com.anqurvanillapy.daygramcopycat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
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
     * Global Private Components & Variables
     * =============================================================================================
     */

    private boolean mainState;

    private ListView entryListView;
    private EntryAdapter entryAdapter;
    private ArrayList<EntryItem> entryItems = new ArrayList<>();

    Date currentLocalTime;
    private String mainYear;
    private String mainMonth;
    private String mainToday;

    private TextView labelMonth;
    private TextView labelYear;
    private TextView buttonTodayEntry;
    private RelativeLayout buttonMonthly;

    /**
     * =============================================================================================
     * Main Activity States
     * =============================================================================================
     */

    public class MainState {
        final public static boolean ABSTRACT = false;
        final public static boolean THOROUGH = true;
    }

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
        final public static int THOROUGH = 4;
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

    public class EntryEmptyObject {}

    public class EntryNonemptyObject {
        private String entryAbstract;
        private String dayOfWeek;
        private String date;

        public EntryNonemptyObject(String entryAbstract, String dayOfWeek, String date) {
            this.entryAbstract = entryAbstract;
            this.dayOfWeek = dayOfWeek;
            this.date = date;
        }

        public String getEntryAbstract() {
            return entryAbstract;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public String getDate() {
            return date;
        }
    }

    public class EntryThoroughObject {
        private String entryThorough;

        public EntryThoroughObject(String entryThorough, String dayOfWeek, String date) {
            // TODO: Spannable is decent
            if (dayOfWeek.equals("Sunday")) {
                this.entryThorough = String.format("<b>%s <font color=\"#B80000\">%s</font> /</b> %s",
                        date, dayOfWeek, entryThorough);
            } else {
                this.entryThorough = String.format("<b>%s %s /</b> %s", date, dayOfWeek, entryThorough);
            }
        }

        public String getEntryThorough() {
            return entryThorough;
        }
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
            return 5;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup viewGroup) {
            LayoutInflater inflater = activity.getLayoutInflater();
            Object entryObject = null;
            entryObject = entryItems.get(position).getObject();

            // FIXME: Item views adaption is trivial
            switch (getItemViewType(position)) {
                case EntryItemType.EMPTY:
                    ViewHolderEntryEmpty emptyHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_empty, null, false);
                        emptyHolder = new ViewHolderEntryEmpty(convertView);
                        convertView.setTag(emptyHolder);
                    } else {
                        emptyHolder = (ViewHolderEntryEmpty) convertView.getTag();
                    }

                    return convertView;
                case EntryItemType.EMPTY_SUNDAY:
                    ViewHolderEntryEmpty emptySundayHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_empty_red, null, false);
                        emptySundayHolder = new ViewHolderEntryEmpty(convertView);
                        convertView.setTag(emptySundayHolder);
                    } else {
                        emptySundayHolder = (ViewHolderEntryEmpty) convertView.getTag();
                    }

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

                    nonemptyHolder.getEntryAbstract().setText(entryNonemptyObject.getEntryAbstract());
                    nonemptyHolder.getItemDayOfWeek().setText(entryNonemptyObject.getDayOfWeek());
                    nonemptyHolder.getItemDate().setText(entryNonemptyObject.getDate());
                    return convertView;
                case EntryItemType.NONEMPTY_SUNDAY:
                    EntryNonemptyObject entryNonemptySundayObject = (EntryNonemptyObject) entryObject;
                    ViewHolderEntryNonempty nonemptySundayHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_red, null, false);
                        nonemptySundayHolder = new ViewHolderEntryNonempty(convertView);
                        convertView.setTag(nonemptySundayHolder);
                    } else {
                        nonemptySundayHolder = (ViewHolderEntryNonempty) convertView.getTag();
                    }

                    nonemptySundayHolder.getEntryAbstract().setText(entryNonemptySundayObject.getEntryAbstract());
                    nonemptySundayHolder.getItemDayOfWeek().setText(entryNonemptySundayObject.getDayOfWeek());
                    nonemptySundayHolder.getItemDate().setText(entryNonemptySundayObject.getDate());
                    return convertView;
                case EntryItemType.THOROUGH:
                    EntryThoroughObject entryThoroughObject = (EntryThoroughObject) entryObject;
                    ViewHolderEntryThorough thoroughHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.entry_adapter_thorough, null, false);
                        thoroughHolder = new ViewHolderEntryThorough(convertView);
                        convertView.setTag(thoroughHolder);
                    } else {
                        thoroughHolder = (ViewHolderEntryThorough) convertView.getTag();
                    }

                    thoroughHolder.getEntryThorough().setText(
                            Html.fromHtml(entryThoroughObject.getEntryThorough()));
                    return convertView;
            }

            return null;
        }

        private class ViewHolderEntryEmpty {
            private View row;

            public ViewHolderEntryEmpty(View row) {
                this.row = row;
            }
        }

        private class ViewHolderEntryNonempty {
            private View row;
            private TextView entryAbstract;
            private TextView itemDayOfWeek;
            private TextView itemDate;

            public ViewHolderEntryNonempty(View row) {
                this.row = row;
            }

            public TextView getEntryAbstract() {
                if (this.entryAbstract == null) {
                    this.entryAbstract = (TextView) row.findViewById(R.id.entryAbstract);
                }

                return this.entryAbstract;
            }

            public TextView getItemDayOfWeek() {
                if (this.itemDayOfWeek == null) {
                    this.itemDayOfWeek = (TextView) row.findViewById(R.id.itemDayOfWeek);
                }

                return this.itemDayOfWeek;
            }

            public TextView getItemDate() {
                if (this.itemDate == null) {
                    this.itemDate = (TextView) row.findViewById(R.id.itemDate);
                }

                return this.itemDate;
            }
        }

        private class ViewHolderEntryThorough {
            private View row;
            private TextView entryThorough;
            public ViewHolderEntryThorough(View row) {
                this.row = row;
            }

            public TextView getEntryThorough() {
                if (this.entryThorough == null) {
                    this.entryThorough = (TextView) row.findViewById(R.id.entryThorough);
                }

                return this.entryThorough;
            }
        }
    }

    /**
     * =============================================================================================
     * ListView Item Updater
     * =============================================================================================
     */

    public void listItemUpdater(String year, String month) {
        Context context = getApplicationContext();
        File entryPath = new File(context.getFilesDir(), "entries");
        File entryThorough = new File(entryPath,
                year+ File.separator + month + File.separator + "thorough");
        entryPath.mkdirs();

        Calendar cal = new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (entryThorough.exists()) {
            try {
                LoadStringFromFile loader = new LoadStringFromFile();
                JSONObject jsonThorough = new JSONObject(loader.loadStringFromFile(entryThorough));
                String stringThoroughBuffer;
                String key;
                String dayOfWeek;
                DateFormat dateFormat = new SimpleDateFormat("EE");
                DateFormat dateFormatFull = new SimpleDateFormat("EEEE");

                for (int i = 1; i <= daysInMonth; i++) {
                    cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, i);
                    key = String.format(year + month + "%02d", i);

                    if (mainState == MainState.ABSTRACT) {
                        dayOfWeek = dateFormat.format(cal.getTime()).toUpperCase();
                        if (jsonThorough.has(key)) {
                            stringThoroughBuffer = jsonThorough.getString(key);

                            if (stringThoroughBuffer.length() > 50) {
                                // Thorough string becomes abstracted
                                stringThoroughBuffer = stringThoroughBuffer.substring(0, 50);

                                if (stringThoroughBuffer.contains(" ")) {
                                    stringThoroughBuffer = stringThoroughBuffer.substring(0,
                                            stringThoroughBuffer.lastIndexOf(" ")) + " ...";
                                }
                            }

                            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                entryItems.add(new EntryItem(EntryItemType.NONEMPTY,
                                        new EntryNonemptyObject(stringThoroughBuffer, dayOfWeek, i + "")));
                            } else {
                                entryItems.add(new EntryItem(EntryItemType.NONEMPTY_SUNDAY,
                                        new EntryNonemptyObject(stringThoroughBuffer, dayOfWeek, i + "")));
                            }
                        } else {
                            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                entryItems.add(new EntryItem(EntryItemType.EMPTY,
                                        new EntryEmptyObject()));
                            } else {
                                entryItems.add(new EntryItem(EntryItemType.EMPTY_SUNDAY,
                                        new EntryEmptyObject()));
                            }
                        }
                    } else {
                        dayOfWeek = dateFormatFull.format(cal.getTime());
                        if (jsonThorough.has(key)) {
                            stringThoroughBuffer = jsonThorough.getString(key);
                            entryItems.add(new EntryItem(EntryItemType.THOROUGH,
                                    new EntryThoroughObject(stringThoroughBuffer, dayOfWeek, i + "")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mainState == MainState.ABSTRACT) {
                for (int i = 1; i <= daysInMonth; i++) {
                    cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, i);

                    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        entryItems.add(new EntryItem(EntryItemType.EMPTY,
                                new EntryEmptyObject()));
                    } else {
                        entryItems.add(new EntryItem(EntryItemType.EMPTY_SUNDAY,
                                new EntryEmptyObject()));
                    }
                }
            }
        }
    }

    /**
     * =============================================================================================
     * ListView Refresher (called by onResume, etc.)
     * =============================================================================================
     */

    private void refreshListView() {
        entryItems.clear();
        listItemUpdater(mainYear, mainMonth);

        if (entryAdapter == null) {
            entryAdapter = new EntryAdapter(this, entryItems);
            entryListView.setAdapter(entryAdapter);
        } else {
            entryAdapter.notifyDataSetChanged();
        }
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
        currentLocalTime = cal.getTime();

        DateFormat monthName = new SimpleDateFormat("MMMM");
        DateFormat month = new SimpleDateFormat("MM");
        DateFormat year = new SimpleDateFormat("yyyy");
        DateFormat today = new SimpleDateFormat("yyyMMdd");

        mainYear = year.format(currentLocalTime);
        mainMonth = month.format(currentLocalTime);
        mainToday = today.format(currentLocalTime);
        String currentMonthName = monthName.format(currentLocalTime).toUpperCase();

        /**
         * =========================================================================================
         * UI Components Initialization
         * =========================================================================================
         */

        mainState = MainState.ABSTRACT;

        entryListView = (ListView) findViewById(R.id.entryListView);
        labelMonth = (TextView) findViewById(R.id.labelMonth);
        labelYear = (TextView) findViewById(R.id.labelYear);
        labelMonth.setText(currentMonthName);
        labelYear.setText(mainYear);
        buttonTodayEntry = (TextView) findViewById(R.id.buttonTodayEntry);
        buttonMonthly = (RelativeLayout) findViewById(R.id.buttonMonthly);

        /**
         * =========================================================================================
         * Event Listeners
         * =========================================================================================
         */

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EntryItem selected = (EntryItem) entryListView.getItemAtPosition(position);
                String targetDate = mainYear + mainMonth + (position + 1);

                Intent intentEntryItemEditor = new Intent(MainActivity.this, EntryEditorActivity.class);
                intentEntryItemEditor.putExtra("date", targetDate);
                intentEntryItemEditor.putExtra("state", EntryEditorActivity.EditorState.VIEW);
                MainActivity.this.startActivity(intentEntryItemEditor);
            }
        });

        buttonTodayEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEntryEditor = new Intent(MainActivity.this, EntryEditorActivity.class);
                intentEntryEditor.putExtra("date", mainToday);
                intentEntryEditor.putExtra("state", EntryEditorActivity.EditorState.EDIT);
                MainActivity.this.startActivity(intentEntryEditor);
            }
        });

        buttonMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainState = !mainState;
                refreshListView();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }
}