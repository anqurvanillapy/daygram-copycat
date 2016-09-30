/**
 * FIXME:
 * - [ ] All exceptions are generalized
 * - [ ] Ugly self-implemented error logging
 */

package com.anqurvanillapy.daygramcopycat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EntryEditorActivity extends AppCompatActivity {

    /**
     * =============================================================================================
     * Global Private Components & Variables
     * =============================================================================================
     */

    private Bundle extras;

    private File targetPath;
    private File targetFile;
    private String targetYear;
    private String targetMonth;
    private String targetDateString;

    private EditText editorText;
    private RelativeLayout editorFooterBack;
    private LinearLayout editorFooterDone;
    private TextView editorHeaderTitle;
    private FrameLayout backButton;
    private TextView buttonInsertTime;
    private TextView buttonEditDone;

    /**
     * =============================================================================================
     * Editor State Handlers
     * =============================================================================================
     */

    public class EditorState {
        public static final int VIEW = 0;
        public static final int EDIT = 1;
    }

    private void editorStateHandler(int state,
                                    EditText editorText,
                                    RelativeLayout editorFooterBack,
                                    LinearLayout editorFooterDone) {
        switch (state) {
            case EditorState.VIEW:
                editorText.setInputType(InputType.TYPE_NULL);
                editorText.setSingleLine(false);
                editorFooterDone.setVisibility(View.GONE);
                editorFooterBack.setVisibility(View.VISIBLE);
                break;
            case EditorState.EDIT:
                editorText.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                editorText.setSingleLine(false);
                editorFooterDone.setVisibility(View.VISIBLE);
                editorFooterBack.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        /**
         * =========================================================================================
         * UI Components (w/ Initialization)
         * =========================================================================================
         */

        editorHeaderTitle = (TextView) findViewById(R.id.editorHeaderTitle);
        editorText = (EditText) findViewById(R.id.editorText);
        editorFooterBack = (RelativeLayout) findViewById(R.id.editorFooterBack);
        backButton = (FrameLayout) findViewById(R.id.buttonBack);
        editorFooterDone = (LinearLayout) findViewById(R.id.editorFooterDone);
        buttonInsertTime = (TextView) findViewById(R.id.buttonInsertTime);
        buttonEditDone = (TextView) findViewById(R.id.buttonEditDone);

        extras = getIntent().getExtras();
        if (extras != null) {
            editorStateHandler(extras.getInt("state"),
                    editorText,
                    editorFooterBack,
                    editorFooterDone);
        } else {
            // FIXME: Ugly self-implemented error logging
            Log.e("EXTRAS", "Extras from Main to EntryEditor cannot be null");
            finish();
        }

        /**
         * =========================================================================================
         * DateTime & File Handlers
         * =========================================================================================
         */

        /* --- Current Local Time --- */

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat time = new SimpleDateFormat("HH:mm a");
        final String timeString = time.format(currentLocalTime) + " ";

        /* --- Target Date --- */

        DateFormat targetDateFormat = new SimpleDateFormat("yyyyMMdd");
        // TODO: Header title of Sunday is dark red
        DateFormat title = new SimpleDateFormat("EEEE / MMMM d / yyyy");
        Date targetDate = null;

        try {
            if (extras != null) {
                targetDate = targetDateFormat.parse(extras.getString("date"));
            } else {
                targetDate = currentLocalTime;
            }
            String titleString = title.format(targetDate).toUpperCase();
            editorHeaderTitle.setText(titleString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (targetDate == null) {
            targetDate = currentLocalTime;
        }

        DateFormat month = new SimpleDateFormat("MM");
        DateFormat year = new SimpleDateFormat("yyyy");

        targetYear = year.format(targetDate);
        targetMonth = month.format(targetDate);
        targetDateString = targetDateFormat.format(targetDate);

        final Context context = getApplicationContext();
        final File filesDir = context.getFilesDir();
        final File entryPath = new File(filesDir, "entries");
        entryPath.mkdirs();

        targetPath = new File(entryPath, targetYear + File.separator + targetMonth);
        targetFile = new File(targetPath, "thorough");

        if (targetFile.exists()) {
            LoadStringFromFile loader = new LoadStringFromFile();
            try {
                editorText.setText(new JSONObject(loader.loadStringFromFile(targetFile))
                        .getString(targetDateString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * =========================================================================================
         * VIEW State EventListeners
         * =========================================================================================
         */

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * =========================================================================================
         * EDIT State EventListeners
         * =========================================================================================
         */

        editorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editorText.getInputType() == InputType.TYPE_NULL) {
                    editorStateHandler(EditorState.EDIT,
                            editorText,
                            editorFooterBack,
                            editorFooterDone);
                }
            }
        });

        buttonInsertTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Append on cursor
                editorText.append(timeString);
            }
        });

        buttonEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entryTextString = editorText.getText().toString();
                if (entryTextString.isEmpty()) {
                    finish();
                } else {
                    targetPath.mkdirs();
                    JSONObject jsonThorough = null;

                    try {
                        if (targetFile.exists()) {
                            LoadStringFromFile loader = new LoadStringFromFile();
                            jsonThorough = new JSONObject(loader.loadStringFromFile(targetFile));
                        }

                        if (jsonThorough == null) {
                            jsonThorough = new JSONObject();
                        }

                        jsonThorough.put(targetDateString, entryTextString);
                        OutputStream osThorough = new FileOutputStream(targetFile);
                        osThorough.write(jsonThorough.toString().getBytes());
                        osThorough.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    editorStateHandler(EditorState.VIEW,
                            editorText,
                            editorFooterBack,
                            editorFooterDone);
                }
            }
        });
    }
}
