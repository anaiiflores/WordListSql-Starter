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

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Instantiated for the Edit and Delete buttons in WordListAdapter.
 */

public class MyButtonOnClickListener implements View.OnClickListener {

    private final int id;
    private final String word;

    private final RecyclerView.Adapter<?> adapter;
    private final WordListOpenHelper db;
    private final Context context;

    public MyButtonOnClickListener(int id, String word,
                                   RecyclerView.Adapter<?> adapter,
                                   WordListOpenHelper db,
                                   Context context) {
        this.id = id;
        this.word = word;
        this.adapter = adapter;
        this.db = db;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        // Confirmación antes de borrar
        new AlertDialog.Builder(context)
                .setTitle("¿Borrar palabra?")
                .setMessage("Vas a borrar: \"" + word )
                .setPositiveButton("Sí, borrar", (dialog, which) -> {
                    db.delete(id);
                    adapter.notifyDataSetChanged(); // refresca RecyclerView
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
