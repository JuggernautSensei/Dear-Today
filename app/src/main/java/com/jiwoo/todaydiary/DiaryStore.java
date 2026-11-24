package com.jiwoo.todaydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

final class DiaryStore extends SQLiteOpenHelper {
    DiaryStore(Context context) {
        this(context, "diary.db");
    }

    DiaryStore(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE entries (date TEXT PRIMARY KEY, title TEXT NOT NULL, body TEXT NOT NULL, sample INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE drafts (date TEXT PRIMARY KEY, title TEXT NOT NULL, body TEXT NOT NULL)");
        LocalDate today = LocalDate.now();
        insertSample(db, today.minusDays(1), "조금은 가벼워진 마음", "미뤄두었던 일을 끝냈다.\n시작하기 전에는 그렇게 막막했는데,\n끝내고 나니 생각보다 작은 일이었다.\n\n오늘은 조금 가벼운 마음으로 잠들 수 있겠다.");
        insertSample(db, today.minusDays(2), "집으로 돌아오는 길", "바람이 제법 차가워졌다.\n집으로 돌아오는 길에 따뜻한 커피를 샀다.\n손에 전해지는 온기가 좋았다.\n\n평범한 하루에도 기억하고 싶은 순간은 있다.");
        insertSample(db, today.minusDays(3), "작은 기쁨", "좋아하는 음악을 오래 들었다.\n창문으로 들어오는 햇빛이 따뜻했다.\n\n특별한 일이 없어도\n괜찮은 하루일 수 있다는 생각이 들었다.");
        insertSample(db, today.minusDays(4), "나의 속도로", "오늘은 조금 느리게 걸었다.\n급하게 지나쳤던 골목에\n작은 책방이 있다는 걸 처음 알았다.\n\n내일도 나의 속도로 살아가야지.");
    }

    private void insertSample(SQLiteDatabase db, LocalDate date, String title, String body) {
        ContentValues values = values(date, title, body);
        values.put("sample", 1);
        db.insertOrThrow("entries", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new AssertionError("No schema migration exists for this version.");
    }

    DiaryEntry entry(LocalDate date) {
        return find("entries", date);
    }

    DiaryEntry draft(LocalDate date) {
        return find("drafts", date);
    }

    private DiaryEntry find(String table, LocalDate date) {
        try (Cursor cursor = getReadableDatabase().query(table, null, "date = ?", new String[]{date.toString()}, null, null, null)) {
            if (!cursor.moveToFirst()) return null;
            return read(cursor, date, table.equals("entries"));
        }
    }

    List<DiaryEntry> entries(YearMonth month) {
        List<DiaryEntry> result = new ArrayList<>();
        String start = month.atDay(1).toString();
        String end = month.plusMonths(1).atDay(1).toString();
        try (Cursor cursor = getReadableDatabase().query("entries", null, "date >= ? AND date < ?", new String[]{start, end}, null, null, "date DESC")) {
            while (cursor.moveToNext()) {
                LocalDate date = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                result.add(read(cursor, date, true));
            }
        }
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM drafts WHERE date >= ? AND date < ? AND date NOT IN (SELECT date FROM entries)", new String[]{start, end})) {
            while (cursor.moveToNext()) {
                LocalDate date = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                result.add(read(cursor, date, false));
            }
        }
        result.sort(Comparator.comparing((DiaryEntry entry) -> entry.date).reversed());
        return result;
    }

    void saveDraft(LocalDate date, String title, String body) {
        getWritableDatabase().execSQL("INSERT OR REPLACE INTO drafts(date, title, body) VALUES (?, ?, ?)", new Object[]{date.toString(), title, body});
    }

    void save(LocalDate date, String title, String body) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT OR REPLACE INTO entries(date, title, body, sample) VALUES (?, ?, ?, 0)", new Object[]{date.toString(), title, body});
            db.delete("drafts", "date = ?", new String[]{date.toString()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    void delete(LocalDate date) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("entries", "date = ?", new String[]{date.toString()});
            db.delete("drafts", "date = ?", new String[]{date.toString()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    void discardDraft(LocalDate date) {
        getWritableDatabase().delete("drafts", "date = ?", new String[]{date.toString()});
    }

    private static ContentValues values(LocalDate date, String title, String body) {
        ContentValues values = new ContentValues();
        values.put("date", date.toString());
        values.put("title", title);
        values.put("body", body);
        return values;
    }

    private static DiaryEntry read(Cursor cursor, LocalDate date, boolean saved) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        boolean sample = saved && cursor.getInt(cursor.getColumnIndexOrThrow("sample")) != 0;
        return new DiaryEntry(date, title, body, sample, !saved);
    }
}
