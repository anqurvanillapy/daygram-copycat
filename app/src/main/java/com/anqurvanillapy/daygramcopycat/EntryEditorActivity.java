package com.anqurvanillapy.daygramcopycat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class EntryEditorActivity extends AppCompatActivity {

    public class EditorState {
        final public static int VIEW = 0;
        final public static int EDIT = 1;
    }

    private void editorStateHandler(Bundle extras) {
        EditText editorText = (EditText) findViewById(R.id.editorText);
        RelativeLayout editorFooterBack = (RelativeLayout) findViewById(R.id.editorFooterBack);
        RelativeLayout editorFooterDone = (RelativeLayout) findViewById(R.id.editorFooterDone);

        switch (extras.getInt("state")) {
            case EditorState.VIEW:
                editorText.setEnabled(false);
                editorFooterDone.setVisibility(View.GONE);
                editorFooterBack.setVisibility(View.VISIBLE);
                break;
            case EditorState.EDIT:
                editorText.setEnabled(true);
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
            editorStateHandler(extras);
        } else {
            Log.e("EXTRAS", "Extras from Main to EntryEditor cannot be null");
            finish();
        }

        FrameLayout backButton = (FrameLayout) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
