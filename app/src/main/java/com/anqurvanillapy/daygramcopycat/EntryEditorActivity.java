/**
 * FIXME:
 * - [ ] All exceptions are generalized
 * - [ ] Ugly self-implemented error logging
 * - [ ] Content abstract algorithm is ugly
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
    private String entryFilename;

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
        final public static int VIEW = 0;
        final public static int EDIT = 1;
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
        entryFilename = targetDateFormat.format(targetDate);

        final Context context = getApplicationContext();
        final File filesDir = context.getFilesDir();
        final File entryPath = new File(filesDir, "entries");
        entryPath.mkdirs();

        targetPath = new File(entryPath, targetYear + File.separator + targetMonth);
        targetFile = new File(targetPath, entryFilename);

        if (targetFile.exists()) {
            LoadStringFromFile loader = new LoadStringFromFile();
            editorText.setText(loader.loadStringFromFile(targetFile));
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
                    File entryFile = new File(targetPath, entryFilename);
                    File entryAbstract = new File(targetPath, "abstract");
                    JSONObject jsonAbstract = null;

                    try {
                        if (entryAbstract.exists()) {
                            LoadStringFromFile loader = new LoadStringFromFile();
                            jsonAbstract = new JSONObject(loader.loadStringFromFile(entryAbstract));
                        }

                        entryFile.createNewFile();
                        OutputStream osFile = new FileOutputStream(entryFile);
                        osFile.write(entryTextString.getBytes());
                        osFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (jsonAbstract == null) {
                            jsonAbstract = new JSONObject();
                        }

                        // FIXME: This content abstract algorithm is ugly
                        if (entryTextString.length() > 50) {
                            String stringAbstract = entryTextString.substring(0, 50);

                            if (stringAbstract.contains(" ")) {
                                stringAbstract = stringAbstract.substring(0,
                                        stringAbstract.lastIndexOf(" "));
                            }

                            jsonAbstract.put(entryFilename, stringAbstract + " ...");
                        } else {
                            jsonAbstract.put(entryFilename, entryTextString);
                        }

                        OutputStream osAbstract = new FileOutputStream(entryAbstract);
                        osAbstract.write(jsonAbstract.toString().getBytes());
                        osAbstract.close();
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
