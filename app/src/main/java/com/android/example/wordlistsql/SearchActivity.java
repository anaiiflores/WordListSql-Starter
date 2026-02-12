package com.android.example.wordlistsql;


import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private TextView mTextView;
    private EditText mEditWordView;
    private WordListOpenHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mEditWordView = findViewById(R.id.search_word);
        mTextView = findViewById(R.id.search_result);

        // BD helper
        mDB = new WordListOpenHelper(this);
    }

    // onClick desde XML
    public void showResult(View view) {
        String word = mEditWordView.getText().toString();

        mTextView.setText("Result for: " + word + "\n\n");

        Cursor cursor = mDB.search(word);

        // Procesar cursor
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                int index = cursor.getColumnIndex(WordListOpenHelper.KEY_WORD);
                String result = cursor.getString(index);
                mTextView.append("• " + result + "\n");
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            mTextView.append("No results 😅");
        }
    }
}
