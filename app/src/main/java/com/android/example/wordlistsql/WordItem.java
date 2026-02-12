package com.android.example.wordlistsql;

public class WordItem {
    // ID de la fila en la BD
    private int mId;

    // Palabra
    private String mWord;

    public WordItem() {}

    public int getId() { return mId; }
    public String getWord() { return mWord; }

    public void setId(int id) { mId = id; }
    public void setWord(String word) { mWord = word; }
}
