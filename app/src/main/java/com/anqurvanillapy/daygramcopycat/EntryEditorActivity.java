package com.anqurvanillapy.daygramcopycat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class EntryEditorActivity extends AppCompatActivity {

    private class EditorState {
        final public static int VIEW = 0;
        final public static int EDIT = 1;
    }

    private void editorStateHandler(Bundle extras) {
        EditText editorText = (EditText) findViewById(R.id.editorText);

        switch (extras.getInt("state")) {
            case EditorState.VIEW:
                // TODO
            case EditorState.EDIT:
                // TODO
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
