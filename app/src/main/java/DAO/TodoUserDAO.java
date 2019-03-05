package DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.websarva.wings.android.todo.DatabaseHelper;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;


public class TodoUserDAO {
    //インスタンス変数
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    //データベースヘルパーインスタンスの生成
    public TodoUserDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    //全ユーザーリスト取得
    public List<User> findAll(){
        List<User> userlist = new ArrayList<User>();
        db = helper.getReadableDatabase();

        //ユーザーリストの取得
        try {
            String sql = "select id ,name from todo_user";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int indexId = cursor.getColumnIndex("id");
                String id = cursor.getString(indexId);
                int indexUserName = cursor.getColumnIndex("name");
                String userName = cursor.getString(indexUserName);

                User user = new User(id, userName,"",1);
                userlist.add(user);
            }
        }catch(Exception e){
            e.printStackTrace();
            userlist = null;
        }finally {
            db.close();
        }
        return userlist;
    }

    //全ユーザーリスト取得
    public User find(String userId){
        User user = null;
        db = helper.getReadableDatabase();

        //ユーザーリストの取得
        try {
            String sql = "select * from todo_user where id = ?";
            //where文用の変数を生成
            String[] whereWord = {"" + userId};
            Cursor cursor = db.rawQuery(sql, whereWord);
            while (cursor.moveToNext()) {
                int indexId = cursor.getColumnIndex("id");
                String id = cursor.getString(indexId);
                int indexUserName = cursor.getColumnIndex("name");
                String userName = cursor.getString(indexUserName);
                int indexPassword = cursor.getColumnIndex("password");
                String password = cursor.getString(indexPassword);
                int indexPermissions = cursor.getColumnIndex("permissions");
                int permissions = cursor.getInt(indexPermissions);

                user = new User(id, userName, password, permissions);
            }
        }catch(Exception e){
            e.printStackTrace();
            user = null;
        }finally {
            db.close();
        }
        return user;
    }

    //ユーザー情報をを登録
    public boolean insert(User user) {
        db = helper.getReadableDatabase();
        //SQL
        try {
            String sql = "insert into todo_user(id,name,password,permissions) values (?,?,?,?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            stmt.bindString(1, user.getId());
            stmt.bindString(2, user.getName());
            stmt.bindString(3, user.getPass());
            stmt.bindLong(4, user.getPermissions());

            stmt.executeInsert();
        }catch(Exception e){
            e.printStackTrace();
            db.close();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    //ユーザー情報を更新
    public boolean update(User user) {
        db = helper.getReadableDatabase();

        //SQL
        try {
            String sql = "update todo_user set "
                    + "name = ?,"
                    + "password = ? ";
            sql += "where id = ?";
            SQLiteStatement stmt = db.compileStatement(sql);

            stmt.bindString(1, user.getName());
            stmt.bindString(2, user.getPass());
            stmt.bindString(3, user.getId());

            stmt.executeUpdateDelete();
        }catch(Exception e){
            e.printStackTrace();
            db.close();
            return false;
        }finally {
            db.close();
        }
        return true;
    }
}
