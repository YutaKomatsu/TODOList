package DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.websarva.wings.android.todo.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import model.Search;
import model.TodoItem;


public class TodoItemDAO {
    //インスタンス変数
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    //データベースヘルパーインスタンスの生成
    public TodoItemDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    //すべてのTODOを取得
    public List<TodoItem> findAll(){
        List<TodoItem> itemlist = new ArrayList<TodoItem>();
        db = helper.getReadableDatabase();

        try {
            String sql = "select todo_item.id,todo_item.name as workName,todo_item.user,todo_user.name as userName,todo_item.expire_date,todo_item.finished_date "
                    + "from todo_item "
                    + "join todo_user "
                    + "on todo_item.user = todo_user.id";
            Cursor cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext()) {
                //TODOのIDを取得
                int indexId = cursor.getColumnIndex("todo_item.id");
                int id = cursor.getInt(indexId);
                //TODOの項目名を取得
                int indexWorkName = cursor.getColumnIndex("workName");
                String workName = cursor.getString(indexWorkName);
                //TODOの担当者のIDを取得
                int indexUserId = cursor.getColumnIndex("todo_item.user");
                String userId = cursor.getString(indexUserId);
                //TODOの担当者名を取得
                int indexUserName = cursor.getColumnIndex("userName");
                String userName = cursor.getString(indexUserName);
                //TODOの期限日を取得
                int indexExpireDate = cursor.getColumnIndex("todo_item.expire_date");
                String expireDate = cursor.getString(indexExpireDate);
                //TODOの完了日を取得
                int indexFinishedDate = cursor.getColumnIndex("todo_item.finished_date");
                String finishedDate = cursor.getString(indexFinishedDate);

                TodoItem todoItem = new TodoItem(id,workName,userId,userName,expireDate,finishedDate);
                itemlist.add(todoItem);
            }
    }catch(Exception e){
        e.printStackTrace();
        itemlist = null;
    }finally {
        db.close();
    }
        return itemlist;
    }

    //特定IDのTODOを取得
    public TodoItem find(int todoId){
        TodoItem todoItem = null;
        db = helper.getReadableDatabase();

        try {


            String sql = "select todo_item.id,todo_item.name as workName,todo_item.user,todo_user.name as userName,todo_item.expire_date,todo_item.finished_date "
                    + "from todo_item "
                    + "join todo_user "
                    + "on todo_item.user = todo_user.id "
                    + "where todo_item.id = ?";
            //where文用の変数を生成
            String[] whereWord = {"" + todoId};
            Cursor cursor = db.rawQuery(sql, whereWord);
            while(cursor.moveToNext()) {
                //TODOのIDを取得
                int indexId = cursor.getColumnIndex("todo_item.id");
                int id = cursor.getInt(indexId);
                //TODOの項目名を取得
                int indexWorkName = cursor.getColumnIndex("workName");
                String workName = cursor.getString(indexWorkName);
                //TODOの担当者のIDを取得
                int indexUserId = cursor.getColumnIndex("todo_item.user");
                String userId = cursor.getString(indexUserId);
                //TODOの担当者名を取得
                int indexUserName = cursor.getColumnIndex("userName");
                String userName = cursor.getString(indexUserName);
                //TODOの期限日を取得
                int indexExpireDate = cursor.getColumnIndex("todo_item.expire_date");
                String expireDate = cursor.getString(indexExpireDate);
                //TODOの完了日を取得
                int indexFinishedDate = cursor.getColumnIndex("todo_item.finished_date");
                String finishedDate = cursor.getString(indexFinishedDate);

                todoItem = new TodoItem(id,workName,userId,userName,expireDate,finishedDate);
            }
        }catch(Exception e){
            e.printStackTrace();
            todoItem = null;
        }finally {
            db.close();
        }
        return todoItem;
    }
    //TODOを登録
    public boolean insert(TodoItem todoItem) {
        db = helper.getReadableDatabase();
        //SQL
        try {
            String sql = "insert into todo_item(name,user,expire_date) values (?,?,?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            stmt.bindString(1, todoItem.getWorkName());
            stmt.bindString(2, todoItem.getUserId());
            stmt.bindString(3, todoItem.getExpireDate());

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

    //TODOを更新
    public boolean update(TodoItem todoItem) {
        db = helper.getReadableDatabase();
        String finishedDate = null;
        if(!StringUtils.isBlank(todoItem.getFinishedDate()) && !StringUtils.equals(todoItem.getFinishedDate(),"none")) {
            finishedDate = todoItem.getFinishedDate();
        }
        //SQL
        try {
            String sql = "update todo_item set "
                    + "name = ?,"
                    + "user = ?,"
                    + "expire_date = ? ";
            if(!StringUtils.isBlank(finishedDate)) {
                sql += ", finished_date = ? ";
            }
            sql += "where id = ?";
            SQLiteStatement stmt = db.compileStatement(sql);

            stmt.bindString(1, todoItem.getWorkName());
            stmt.bindString(2, todoItem.getUserId());
            if(!StringUtils.isBlank(finishedDate)) {
                stmt.bindString(3, todoItem.getExpireDate());
                stmt.bindString(4, finishedDate);
                stmt.bindLong(5, todoItem.getId());
            }else {
                stmt.bindString(3, todoItem.getExpireDate());
                stmt.bindLong(4, todoItem.getId());
            }

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

    //TODO完了日の更新
    public boolean finished(TodoItem todoItem) {
        db = helper.getReadableDatabase();
        //SQL
        try {
            String sql = "update todo_item set "
                    + "finished_date = ? "
                    + "where id = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, todoItem.getFinishedDate());
            stmt.bindLong(2, todoItem.getId());


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

    //TODOの削除
    public boolean delete(int todoId) {
        db = helper.getReadableDatabase();
        //SQL
        try {
            String sql = "delete from todo_item where id = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, todoId);

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

    //特定条件のTODOを取得
    public List<TodoItem> search(Search search){
        db = helper.getReadableDatabase();
        List<TodoItem> itemlist = new ArrayList<TodoItem>();
        //条件の要不要確認用フラグ
        boolean advancedSearch = false;
        boolean workNameSearch = false;
        boolean userNameSearch = false;
        boolean expireDateSearch = false;
        boolean finishedDateSearch = false;
        //条件数
        int advancedSearchNum = 1;

        //SQL
        try {
            String sql = "select todo_item.id,todo_item.name as workName,todo_item.user,todo_user.name as userName,todo_item.expire_date,todo_item.finished_date "
                    + "from todo_item "
                    + "join todo_user "
                    + "on todo_item.user = todo_user.id";
            //項目名
            if(!StringUtils.isBlank(search.getTodoItem().getWorkName())){
                if(!advancedSearch) {
                    sql += " where workName like ? ";
                    advancedSearch = true;
                    workNameSearch = true;
                }
            }
            //担当者
            if(!StringUtils.isBlank(search.getTodoItem().getUserName())){
                if(!advancedSearch) {
                    sql += " where userName like ? ";
                    advancedSearch = true;
                    userNameSearch = true;
                }else {
                    sql += " and userName like ? ";
                    userNameSearch = true;
                }
            }
                //期限
            if(!StringUtils.isBlank(search.getTodoItem().getExpireDate())) {
                //入力日以前の検索
                if(StringUtils.equals(search.getExpireType(),"1")) {
                    if(!advancedSearch) {
                        sql += " where todo_item.expire_date <= ? ";
                        advancedSearch = true;
                        expireDateSearch = true;
                    }else {
                        sql += " and todo_item.expire_date <= ? ";
                        expireDateSearch = true;
                    }
                //入力日以降の検索
                }else {
                    if(!advancedSearch) {
                        sql += " where todo_item.expire_date >= ? ";
                        advancedSearch = true;
                        expireDateSearch = true;
                    }else {
                        sql += " and todo_item.expire_date >= ? ";
                        expireDateSearch = true;
                    }
                }
            }
                //完了日
            if(!StringUtils.isBlank(search.getTodoItem().getFinishedDate())) {
                //入力日以前の検索
                if(StringUtils.equals(search.getFinishedType(),"1")) {
                    if(!advancedSearch) {
                        sql += " where todo_item.finished_date <= ? ";
                        advancedSearch = true;
                        finishedDateSearch = true;
                    }else {
                        sql += " and todo_item.finished_date <= ? ";
                        finishedDateSearch = true;
                    }
                //入力日以降の検索
                }else {
                    if(!advancedSearch) {
                        sql += " where todo_item.finished_date >= ? ";
                        advancedSearch = true;
                        finishedDateSearch = true;
                    }else {
                        sql += " and todo_item.finished_date >= ? ";
                        finishedDateSearch = true;
                    }
                }
            }
            List<String> whereWordList = new ArrayList<>();

            if(workNameSearch) {
                whereWordList.add("%" + search.getTodoItem().getWorkName() + "%");
            }
            if(userNameSearch) {
                whereWordList.add("%" + search.getTodoItem().getUserName() + "%");
            }
            if(expireDateSearch) {
                whereWordList.add(search.getTodoItem().getExpireDate());
            }
            if(finishedDateSearch) {
                whereWordList.add(search.getTodoItem().getFinishedDate());
            }

            String[] whereWord = (String[])whereWordList.toArray(new String[whereWordList.size()]);
            Cursor cursor = db.rawQuery(sql, whereWord);
            while(cursor.moveToNext()) {
                //TODOのIDを取得
                int indexId = cursor.getColumnIndex("todo_item.id");
                int id = cursor.getInt(indexId);
                //TODOの項目名を取得
                int indexWorkName = cursor.getColumnIndex("workName");
                String workName = cursor.getString(indexWorkName);
                //TODOの担当者のIDを取得
                int indexUserId = cursor.getColumnIndex("todo_item.user");
                String userId = cursor.getString(indexUserId);
                //TODOの担当者名を取得
                int indexUserName = cursor.getColumnIndex("userName");
                String userName = cursor.getString(indexUserName);
                //TODOの期限日を取得
                int indexExpireDate = cursor.getColumnIndex("todo_item.expire_date");
                String expireDate = cursor.getString(indexExpireDate);
                //TODOの完了日を取得
                int indexFinishedDate = cursor.getColumnIndex("todo_item.finished_date");
                String finishedDate = cursor.getString(indexFinishedDate);

                TodoItem todoItem = new TodoItem(id,workName,userId,userName,expireDate,finishedDate);
                itemlist.add(todoItem);
            }
        }catch(Exception e){
            e.printStackTrace();
            itemlist = null;
        }finally {
            db.close();
        }
        return itemlist;
    }
}