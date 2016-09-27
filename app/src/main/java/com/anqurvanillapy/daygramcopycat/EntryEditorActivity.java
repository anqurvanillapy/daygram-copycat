package com.anqurvanillapy.daygramcopycat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class EntryEditorActivity extends AppCompatActivity {

    public class EditorState {
        final public static int VIEW = 0;
        final public static int EDIT = 1;
    }

    private void editorStateHandler(int state) {
        EditText editorText = (EditText) findViewById(R.id.editorText);
        RelativeLayout editorFooterBack = (RelativeLayout) findViewById(R.id.editorFooterBack);
        LinearLayout editorFooterDone = (LinearLayout) findViewById(R.id.editorFooterDone);

        switch (state) {
            case EditorState.VIEW:
                editorText.setInputType(InputType.TYPE_NULL);
                editorFooterDone.setVisibility(View.GONE);
                editorFooterBack.setVisibility(View.VISIBLE);
                break;
            case EditorState.EDIT:
                editorText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editorFooterDone.setVisibility(View.VISIBLE);
                editorFooterBack.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editorStateHandler(extras.getInt("state"));
        } else {
            Log.e("EXTRAS", "Extras from Main to EntryEditor cannot be null");
            finish();
        }

        FrameLayout backButton = (FrameLayout) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final EditText editorText = (EditText) findViewById(R.id.editorText);
        editorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editorText.getInputType() == InputType.TYPE_NULL) {
                    editorStateHandler(EditorState.EDIT);
                }
            }
        });
    }
}
