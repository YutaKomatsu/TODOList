package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import ActionType.FromClass;
import Logic.LoginLogic;
import Logic.PasswordHashLogic;
import model.User;

public class TodoLoginActivity extends AppCompatActivity {
    private EditText inputUserID;
    private EditText inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_login);

        //データベースヘルパークラスを呼び出し、テーブルを作成
        DatabaseHelper helper = new DatabaseHelper(TodoLoginActivity.this);
        //ログインボタンのリスナーを生成
        Button loginButton = findViewById(R.id.tvExecuteLogin);
        loginButton.setOnClickListener(new ExecuteLogin());
        //新規登録ボタンのリスナーを生成
        Button userRegisterButton = findViewById(R.id.tvUserRegister);
        userRegisterButton.setOnClickListener(new UserRegister());

        //ユーザーID欄のIDを取得
        inputUserID = findViewById(R.id.tvUserIdInput);
        //パスワード欄のIDを取得
        inputPassword = findViewById(R.id.tvPasswordInput);

    }

    @Override
    public void onResume(){
        super.onResume();
        inputUserID.setText("");
        inputPassword.setText("");
    }

    //ログインボタン押下時
    private class ExecuteLogin implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //入力されたユーザーID、パスワードを取得
            String userId = inputUserID.getText().toString();
            String password = inputPassword.getText().toString();
            //パスワードをハッシュ化する
            PasswordHashLogic passwordHashLogic = new PasswordHashLogic();
            String hashPassword = passwordHashLogic.encryptStr(password);
            passwordHashLogic = null;
            password = null;
            // 、比較用のユーザー情報インスタンスを生成
            User user = new User(userId, hashPassword);

            //ユーザーリスト取得
            LoginLogic loginLogic = new LoginLogic();
            boolean isLogin = loginLogic.execute(user, TodoLoginActivity.this);

            if(isLogin){
                // TODOリスト画面に遷移
                Intent intent = new Intent(TodoLoginActivity.this, TodoListActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                ArrayList<String> inputErrorList = new ArrayList<>();
                inputErrorList.add("ログインに失敗しました");
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_LOGIN_ACTIVITY);
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
                inputUserID.setText("");
                inputPassword.setText("");
            }
        }
    }

    //新規登録ボタン押下時
    private class UserRegister implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //ユーザー登録画面へのインテント
            Intent intent = new Intent(TodoLoginActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        }
    }

    //戻るボタン無効化
    @Override
    public void onBackPressed() {
    }
}
