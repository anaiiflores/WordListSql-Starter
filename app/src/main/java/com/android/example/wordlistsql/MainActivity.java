/*
 * Copyright (C) Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.wordlistsql;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Implements a RecyclerView that displays a list of words from a SQL database.
 * - Clicking the fab button opens a second activity to add a word to the database.
 * - Clicking the Edit button opens an activity to edit the current word in the database.
 * - Clicking the Delete button deletes the current word from the database.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int WORD_EDIT = 1;
    public static final int WORD_ADD = -1;

    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;

    // (Añadido) Base de datos helper
    private WordListOpenHelper mDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Este layout NO tiene toolbar, solo recyclerview + fab
        setContentView(R.layout.activity_main);

        // (Añadido) Crear/Open BD (si no existe, ejecuta onCreate() del helper)
        mDB = new WordListOpenHelper(this);

        // Create recycler view.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // Create an mAdapter and supply the data to be displayed.
        // (Cambiado) ahora el adapter recibe también la BD
        mAdapter = new WordListAdapter(this, mDB);

        // Connect the mAdapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);

        // Give the recycler view a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditWordActivity.class);

                // (Añadido) marcamos que es "add"
                intent.putExtra(WordListAdapter.EXTRA_ID, WordListAdapter.WORD_ADD);

                startActivityForResult(intent, WORD_EDIT);
            }
        });
    }

    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(getBaseContext(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add code to update the database.

        if (requestCode == WORD_EDIT && resultCode == RESULT_OK && data != null) {

            String word = data.getStringExtra(EditWordActivity.EXTRA_REPLY);

            if (!TextUtils.isEmpty(word)) {
                int id = data.getIntExtra(WordListAdapter.EXTRA_ID, WordListAdapter.WORD_ADD);

                // Si viene WORD_ADD -> insert
                if (id == WordListAdapter.WORD_ADD) {
                    mDB.insert(word);
                }
                // Si id >= 0 -> update
                else if (id >= 0) {
                    mDB.update(id, word);
                }

                // Refrescar lista
                mAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "No se guardó: está vacío 😅", Toast.LENGTH_LONG).show();
            }
        }
    }
}
