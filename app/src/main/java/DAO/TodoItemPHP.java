package DAO;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import model.Search;
import model.TodoItem;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TodoItemPHP extends AsyncTask<String, Void, String> {
    TodoItem setTodoItem;
    TodoItem getTodoItem;
    List<TodoItem> todoList = new ArrayList<>();
    Search search;
    String actionType;
    boolean result;
    CountDownLatch latch;
    final public static String GET_TODO_LIST = "0";
    final public static String GET_TODO_ITEM= "1";
    final public static String INSERT_TODO = "2";
    final public static String UPDATE_TODO = "3";
    final public static String FINISHED_TODO = "4";
    final public static String DELETE_TODO = "5";
    final public static String SEARCH_TODO = "6";

    public TodoItemPHP(TodoItem setTodoItem, Search search, CountDownLatch latch){
        this.setTodoItem = setTodoItem;
        this.search = search;
        this.latch = latch;
    }

    @Override
    protected String doInBackground(String... params) {
        String json = null;
        //JSON変換用
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //URL作成
        String url = params[0];
        //実行アクションを取得
        actionType = params[1];
        //HTTP処理用オプジェクト作成
        OkHttpClient client = new OkHttpClient();
        //JSON用変数
        StringBuilder sb = new StringBuilder();
        //リクエスト
        Request request = null;
        RequestBody body = null;

        if(StringUtils.equals(actionType,GET_TODO_ITEM)){
            //特定IDのTODOを取得
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoItem.getId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,INSERT_TODO)){
            //TODOを登録
            //JSONの生成
            sb.append("{\"WORK_NAME\":");
            sb.append("\"" + setTodoItem.getWorkName() + "\",");
            sb.append("\"USER_ID\":");
            sb.append("\"" + setTodoItem.getUserId() + "\",");
            sb.append("\"EXPIRE_DATE\":");
            sb.append("\"" + setTodoItem.getExpireDate() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,UPDATE_TODO)){
            //TODOを更新
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoItem.getId() + "\",");
            sb.append("\"WORK_NAME\":");
            sb.append("\"" + setTodoItem.getWorkName() + "\",");
            sb.append("\"USER_ID\":");
            sb.append("\"" + setTodoItem.getUserId() + "\",");
            sb.append("\"EXPIRE_DATE\":");
            sb.append("\"" + setTodoItem.getExpireDate() + "\",");
            sb.append("\"FINISHED_DATE\":");
            sb.append("\"" + setTodoItem.getFinishedDate() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,FINISHED_TODO)){
            //TODO完了日の更新
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoItem.getId() + "\",");
            sb.append("\"FINISHED_DATE\":");
            sb.append("\"" + setTodoItem.getFinishedDate() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,DELETE_TODO)){
            //TODOの削除
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoItem.getId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,SEARCH_TODO)){
            //特定条件のTODOを取得
            //JSONの生成
            sb.append("{\"WORK_NAME\":");
            sb.append("\"" + search.getTodoItem().getWorkName() + "\",");
            sb.append("\"USER_NAME\":");
            sb.append("\"" + search.getTodoItem().getUserName() + "\",");
            sb.append("\"EXPIRE_DATE\":");
            sb.append("\"" + search.getTodoItem().getExpireDate() + "\",");
            sb.append("\"FINISHED_DATE\":");
            sb.append("\"" + search.getTodoItem().getFinishedDate() + "\",");
            sb.append("\"EXPIRE_TYPE\":");
            sb.append("\"" + search.getExpireType() + "\",");
            sb.append("\"FINISHED_TYPE\":");
            sb.append("\"" + search.getFinishedType() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }

        if(StringUtils.equals(actionType,GET_TODO_LIST)) {
            //リクエスト生成
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
        }else{
            //リクエスト生成
            request = new Request.Builder()
                    .header("Content-Type", "application/json; charset=utf-8")
                    .url(url)
                    .post(body)
                    .build();
        }
        //受信用オブジェクトを作成
        try (Response response = client.newCall(request).execute()) {
            json = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if(StringUtils.equals(actionType,GET_TODO_LIST)
                || StringUtils.equals(actionType,GET_TODO_ITEM)
                || StringUtils.equals(actionType,SEARCH_TODO)) {
            int todoId;
            String workName = null;
            String userId = null;
            String userName = null;
            String expireDate = null;
            String finishedDate = null;

            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject resultJson = jsonArray.getJSONObject(0);
                if(resultJson.getBoolean("result")) {
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        todoId = data.getInt("TODO_ID");
                        workName = data.getString("TODO_NAME");
                        userId = data.getString("USER_ID");
                        userName = data.getString("USER_NAME");
                        expireDate = data.getString("EXPIRE_DATE");
                        finishedDate = data.getString("FINISHED_DATE");
                        if(StringUtils.equals(finishedDate, "null")){
                            finishedDate = "";
                        }
                        if(StringUtils.equals(actionType,GET_TODO_LIST) || StringUtils.equals(actionType,SEARCH_TODO)) {
                            todoList.add(new TodoItem(todoId, workName, userId, userName, expireDate, finishedDate));
                        }else if(StringUtils.equals(actionType,GET_TODO_ITEM)){
                            getTodoItem = new TodoItem(todoId, workName, userId, userName, expireDate, finishedDate);
                        }
                    }
                }else{
                    todoList = null;
                    getTodoItem = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                todoList = null;
                getTodoItem = null;
            }
        }else{
            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject resultJson = jsonArray.getJSONObject(0);
                result = resultJson.getBoolean("result");
            }catch (JSONException e) {
                e.printStackTrace();
                result = false;
            }
        }
        latch.countDown();

        return "";
    }

    public List<TodoItem> getTodoList(){
        return  todoList;
    }

    public TodoItem getTodoItem(){
        return getTodoItem;
    }

    public boolean getResult(){
        return result;
    }
}