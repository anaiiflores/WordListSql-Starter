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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Implements a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    /**
     *  Custom view holder with a text view and two buttons.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            wordItemView = (TextView) itemView.findViewById(R.id.word);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    }

    private static final String TAG = WordListAdapter.class.getSimpleName();

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "WORD";

    // (Añadido) También lo usa el flujo de editar
    public static final String EXTRA_POSITION = "POSITION";

    // (Añadido) Para indicar "añadir" en vez de editar
    public static final int WORD_ADD = -1;

    private final LayoutInflater mInflater;
    Context mContext;

    // (Añadido) Helper de BD
    private WordListOpenHelper mDB;

    // (Cambiado) ahora el adapter recibe también la BD
    public WordListAdapter(Context context, WordListOpenHelper db) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDB = db;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WordViewHolder holder, int position) {

        // 1) Pedimos a la BD el WordItem que toca (por posición)
        WordItem current = mDB.query(position);

        // 2) Mostramos la palabra
        holder.wordItemView.setText(current.getWord());

        // 3) DELETE: usa el listener del profesor (o tu listener)
        holder.delete_button.setOnClickListener(
                new MyButtonOnClickListener(current.getId(), current.getWord(), this, mDB, mContext)
        );

        // 4) EDIT: abre EditWordActivity con extras
        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(
                current.getId(), current.getWord(), this, mDB, mContext) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditWordActivity.class);
                intent.putExtra(EXTRA_ID, current.getId());
                intent.putExtra(EXTRA_POSITION, holder.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, current.getWord());

                ((Activity) mContext).startActivityForResult(
                        intent, MainActivity.WORD_EDIT
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        // (Cambiado) ahora devolvemos el número real de filas en BD
        return (int) mDB.count();
    }
}
