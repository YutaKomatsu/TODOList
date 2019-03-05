package com.websarva.wings.android.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        StringBuilder sb = new StringBuilder();
        sb.append("create table todo_user (");
        sb.append("id text primary key,");
        sb.append("name text not null,");
        sb.append("password text not null,");
        sb.append("permissions integer not null");
        sb.append(");");

        String sql = sb.toString();

        db.execSQL(sql);

        sb = new StringBuilder();
        sb.append("create table todo_item (");
        sb.append("id INTEGER PRIMARY KEY,");
        sb.append("name text not null,");
        sb.append("user text not null,");
        sb.append("expire_date text not null,");
        sb.append("finished_date text");
        sb.append(");");

        sql = sb.toString();

        db.execSQL(sql);

        sb = new StringBuilder();
        sb.append("create table todo_progress (");
        sb.append("id INTEGER PRIMARY KEY,");
        sb.append("todo_id text not null,");
        sb.append("todo_progress text not null,");
        sb.append("date text not null");
        sb.append(");");

        sql = sb.toString();

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
