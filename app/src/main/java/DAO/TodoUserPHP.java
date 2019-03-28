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
import model.User;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TodoUserPHP extends AsyncTask<String, Void, String> {
    private User setUser;
    private User getUser;
    private List<User> userList = new ArrayList<>();
    private String actionType;
    private boolean result;
    private CountDownLatch latch;
    final public static String GET_USER_LIST = "0";
    final public static String GET_USER_ITEM= "1";
    final public static String INSERT_USER = "2";
    final public static String UPDATE_USER = "3";

    public TodoUserPHP(User setUser, CountDownLatch latch){
        this.setUser = setUser;
        this.latch = latch;
    }

    @Override
    protected String doInBackground(String... params) {
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

        if(StringUtils.equals(actionType,GET_USER_ITEM)){
            //特定IDのTODOを取得
            //JSONの生成
            sb.append("{\"USER_ID\":");
            sb.append("\"" + setUser.getId() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,INSERT_USER)){
            //TODOを登録
            //JSONの生成
            sb.append("{\"USER_ID\":");
            sb.append("\"" + setUser.getId() + "\",");
            sb.append("\"USER_NAME\":");
            sb.append("\"" + setUser.getName() + "\",");
            sb.append("\"PASSWORD\":");
            sb.append("\"" + setUser.getPass() + "\",");
            sb.append("\"PERMISSIONS\":");
            sb.append("\"" + setUser.getPermissions() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }else if(StringUtils.equals(actionType,UPDATE_USER)){
            //TODOを更新
            //JSONの生成
            sb.append("{\"USER_ID\":");
            sb.append("\"" + setUser.getId() + "\",");
            sb.append("\"USER_NAME\":");
            sb.append("\"" + setUser.getName() + "\",");
            sb.append("\"PASSWORD\":");
            sb.append("\"" + setUser.getPass() + "\",");
            sb.append("\"PERMISSIONS\":");
            sb.append("\"" + setUser.getPermissions() + "\"}");
            //送信するJSONの生成
            body = RequestBody.create(JSON, sb.toString());
        }
        if(StringUtils.equals(actionType,GET_USER_LIST)) {
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
        String json = null;
        //受信用オブジェクトを作成
        try (Response response = client.newCall(request).execute()) {
            json =  response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            latch.countDown();
            return "";
        }
        //受信したJSONを整形
        if(StringUtils.equals(actionType,GET_USER_LIST) || StringUtils.equals(actionType,GET_USER_ITEM)) {
            String userId = null;
            String userName = null;
            String password = null;
            int permissions;
            //String loginStatus = null;

            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject resultJson = jsonArray.getJSONObject(0);
                if(resultJson.getBoolean("result")) {
                    for (int i = 1; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        userId = data.getString("USER_ID");
                        userName = data.getString("USER_NAME");
                        password = data.getString("PASSWORD");
                        permissions = data.getInt("PERMISSIONS");
                        if(StringUtils.equals(actionType,GET_USER_LIST)) {
                            userList.add(new User(userId, userName, password, permissions));
                        }else if(StringUtils.equals(actionType,GET_USER_ITEM)){
                            getUser = new User(userId, userName, password, permissions);
                        }
                    }
                }else {
                    if(StringUtils.equals(actionType,GET_USER_LIST)) {
                        userList = null;
                    }else if(StringUtils.equals(actionType,GET_USER_ITEM)){
                        getUser = null;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if(StringUtils.equals(actionType,GET_USER_LIST)) {
                    userList = null;
                }else if(StringUtils.equals(actionType,GET_USER_ITEM)){
                    getUser = null;
                }
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

    public boolean getResult(){
        return result;
    }
    public User getUser(){
        return getUser;
    }
    public List<User> getUserList(){
        return userList;
    }
}