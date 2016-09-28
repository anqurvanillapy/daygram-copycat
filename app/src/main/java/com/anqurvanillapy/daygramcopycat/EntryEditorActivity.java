package com.anqurvanillapy.daygramcopycat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class EntryEditorActivity extends AppCompatActivity {

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
         * DateTime & File Handlers
         * =========================================================================================
         */

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));
        Date currentLocalTime = cal.getTime();

        DateFormat title = new SimpleDateFormat("EEEE / MMM dd / yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm a");
        DateFormat day = new SimpleDateFormat("yyyMMdd");
        DateFormat month = new SimpleDateFormat("MM");
        DateFormat year = new SimpleDateFormat("yyyy");

        final String timeString = time.format(currentLocalTime) + " ";
        String titleString = title.format(currentLocalTime).toUpperCase();
        final String entryYear = year.format(currentLocalTime);
        final String entryMonth = month.format(currentLocalTime);
        final String entryFilename = day.format(currentLocalTime);

        final Context context = getApplicationContext();
        final File filesDir = context.getFilesDir();
        final File entryPath = new File(filesDir, "entries");
        entryPath.mkdirs();

        /**
         * =========================================================================================
         * UI Components (w/ Initialization)
         * =========================================================================================
         */

        final EditText editorText = (EditText) findViewById(R.id.editorText);
        final RelativeLayout editorFooterBack = (RelativeLayout) findViewById(R.id.editorFooterBack);
        final LinearLayout editorFooterDone = (LinearLayout) findViewById(R.id.editorFooterDone);

        final Bundle extras = getIntent().getExtras();
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

        TextView editorHeaderTitle = (TextView) findViewById(R.id.editorHeaderTitle);
        editorHeaderTitle.setText(titleString);

        FrameLayout backButton = (FrameLayout) findViewById(R.id.buttonBack);

        TextView buttonInsertTime = (TextView) findViewById(R.id.buttonInsertTime);
        TextView buttonEditDone = (TextView) findViewById(R.id.buttonEditDone);

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
                    File entryMonthPath = new File(entryPath, entryYear + File.separator + entryMonth);
                    entryMonthPath.mkdirs();
                    File entryFile = new File(entryMonthPath, entryFilename);
                    File entryAbstract = new File(entryMonthPath, "abstract");

                    try {
                        entryFile.createNewFile();
                        OutputStream osFile = new FileOutputStream(entryFile);
                        osFile.write(entryTextString.getBytes());
                        osFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject jsonAbstract = new JSONObject();

                        // FIXME: This string shortening algorithm is ugly
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
