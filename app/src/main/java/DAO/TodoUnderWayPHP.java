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

import model.TodoUnderWay;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TodoUnderWayPHP extends AsyncTask<String, Void, String> {
    private TodoUnderWay setTodoUnderWay;
    private TodoUnderWay getTodoUnderWay;
    private List<TodoUnderWay> todoUnderWayList = new ArrayList<>();
    private String actionType;
    private boolean result;
    private CountDownLatch latch;
    final public static String GET_TODO_UNDER_WAY_LIST = "0";
    final public static String GET_TODO_UNDER_WAY_ITEM= "1";
    final public static String INSERT_TODO_UNDER_WAY = "2";
    final public static String UPDATE_TODO_UNDER_WAY = "3";
    final public static String DELETE_TODO_UNDER_WAY = "4";

    public TodoUnderWayPHP(TodoUnderWay setTodoUnderWay, CountDownLatch latch){
        this.setTodoUnderWay = setTodoUnderWay;
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
        OkHttpClient client = Okhttp3Client.getOkHttpClient();
        //JSON用変数
        StringBuilder sb = new StringBuilder();
        //リクエスト
        Request request = null;
        RequestBody body = null;

        if(StringUtils.equals(actionType,GET_TODO_UNDER_WAY_LIST)){
            //特定IDのTODOを取得
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoUnderWay.getTodoId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,GET_TODO_UNDER_WAY_ITEM)){
            //特定IDのTODOを取得
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoUnderWay.getTodoId() + "\",");
            sb.append("\"SUB_ID\":");
            sb.append("\"" + setTodoUnderWay.getSubId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,INSERT_TODO_UNDER_WAY)){
            //TODOを登録
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoUnderWay.getTodoId() + "\",");
            sb.append("\"TODO_UNDER_WAY_TITLE\":");
            sb.append("\"" + setTodoUnderWay.getUnderWayTitle() + "\",");
            sb.append("\"TODO_UNDER_WAY\":");
            sb.append("\"" + setTodoUnderWay.getUnderWay().replaceAll("\n","<brjson>") + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,UPDATE_TODO_UNDER_WAY)){
            //TODOを更新
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoUnderWay.getTodoId() + "\",");
            sb.append("\"SUB_ID\":");
            sb.append("\"" + setTodoUnderWay.getSubId() + "\",");
            sb.append("\"TODO_UNDER_WAY_TITLE\":");
            sb.append("\"" + setTodoUnderWay.getUnderWayTitle() + "\",");
            sb.append("\"TODO_UNDER_WAY\":");
            sb.append("\"" + setTodoUnderWay.getUnderWay().replaceAll("\n","<brjson>") + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,DELETE_TODO_UNDER_WAY)){
            //TODO完了日の更新
            //JSONの生成
            sb.append("{\"TODO_ID\":");
            sb.append("\"" + setTodoUnderWay.getTodoId() + "\",");
            sb.append("\"SUB_ID\":");
            sb.append("\"" + setTodoUnderWay.getSubId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }


        //リクエスト生成
        request = new Request.Builder()
                .header("Content-Type", "application/json; charset=utf-8")
                .url(url)
                .post(body)

                .build();

        //受信用オブジェクトを作成
        try (Response response = client.newCall(request).execute()) {
            json = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if(StringUtils.equals(actionType,GET_TODO_UNDER_WAY_LIST)
                || StringUtils.equals(actionType,GET_TODO_UNDER_WAY_ITEM)) {
            int todoId;
            int subId;
            String underWayTitle = null;
            String underWay = null;
            String updateDate = null;

            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject resultJson = jsonArray.getJSONObject(0);
                if(resultJson.getBoolean("result")) {
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        todoId = data.getInt("TODO_ID");
                        subId = data.getInt("SUB_ID");
                        underWayTitle = data.getString("TODO_UNDER_WAY_TITLE");
                        underWay = data.getString("TODO_UNDER_WAY").replaceAll("<brjson>","\n");
                        updateDate = data.getString("UPDATE_DATE");

                        if(StringUtils.equals(actionType,GET_TODO_UNDER_WAY_LIST)) {
                            todoUnderWayList.add(new TodoUnderWay(todoId, subId, underWayTitle, underWay, updateDate));
                        }else if(StringUtils.equals(actionType,GET_TODO_UNDER_WAY_ITEM)){
                            getTodoUnderWay = new TodoUnderWay(todoId, subId, underWayTitle, underWay, updateDate);
                        }
                    }
                }else{
                    todoUnderWayList = null;
                    getTodoUnderWay = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                todoUnderWayList = null;
                getTodoUnderWay = null;
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

    public List<TodoUnderWay> getTodoUnderWayList(){
        return  todoUnderWayList;
    }

    public TodoUnderWay getTodoUnderWayItem(){
        return getTodoUnderWay;
    }

    public boolean getResult(){
        return result;
    }
}