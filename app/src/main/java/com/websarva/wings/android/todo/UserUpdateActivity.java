package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import Logic.UserUpdateLogic;
import model.User;

public class UserUpdateActivity extends AppCompatActivity {
    //private変数
    TextView inputUserID;
    EditText inputUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);

        //ユーザー情報を取得
        Intent intent = getIntent();
        User user = (User)intent.getSerializableExtra("user");

        //ユーザーID、ユーザー名をセット
        inputUserID = findViewById(R.id.tvUserIdInput);
        inputUserID.setText(user.getId());
        inputUserName = findViewById(R.id.tvUserNameInput);
        inputUserName.setText(user.getName());

        //ユーザー情報を削除
        user = null;
        intent.removeExtra("user");

        //ボタンをセット
        Button userRegisterButton = findViewById(R.id.tvUserRegisterExecute);
        userRegisterButton.setOnClickListener(new UserUpdateExecute());

        Button userRegisterCancelButton = findViewById(R.id.tvUserRegisterCancel);
        userRegisterCancelButton.setOnClickListener(new UserUpdateCancel());
    }

    //登録ボタン押下時
    private class UserUpdateExecute implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //ユーザーIDを取得
            String userId = inputUserID.getText().toString();
            //入力されたユーザー名を取得
            String userName = inputUserName.getText().toString();
            //入力されたパスワードを取得
            EditText inputPassword = findViewById(R.id.tvPasswordInput);
            String password = inputPassword.getText().toString();
            //入力された確認用パスワードを取得
            EditText inputCheckPassword = findViewById(R.id.tvCheckPasswordInput);
            String checkPassword = inputCheckPassword.getText().toString();

            ArrayList<String> inputErrorList = new ArrayList<String>();


            //入力値のチェック
            if(StringUtils.isBlank(userName)) {
                inputErrorList.add("※ユーザー名が入力されていません");
            }
            if(StringUtils.isBlank(password)) {
                inputErrorList.add("※パスワードが入力されていません");
            }
            if(StringUtils.contains(password, " ")) {
                inputErrorList.add("※パスワードに空白文字が入力されています、空白文字は入力しないでください");
            }
            if(!StringUtils.isAsciiPrintable(password)) {
                inputErrorList.add("※パスワードに全角文字が入力されています、全角文字は入力しないでください");
            }
            if(!StringUtils.equals(password,checkPassword)) {
                inputErrorList.add("※パスワードと確認用パスワードの内容が一致していません");
            }


            if(inputErrorList.size() == 0) {
                //ユーザー情報を登録する
                User updateUser = new User(userId,userName,password,1);
                UserUpdateLogic userUpdateLogic = new UserUpdateLogic();
                boolean isUserUpdate = userUpdateLogic.execute(updateUser,UserUpdateActivity.this);
                if(isUserUpdate) {
                    // 更新完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title",getString(R.string.tv_userUpdateResultTitle));
                    bundle.putString("msg",getString(R.string.tv_userUpdateResult));
                    bundle.putString("button",getString(R.string.tv_backUserInformationMenu));
                    bundle.putBoolean("finishActivity",true);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                //更新失敗時の処理
                }else {
                    //エラーダイアログフラグメントオブジェクトを生成
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("ユーザーの更新に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putString("fromClass", "UserUpdateActivity");
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
                }
            }else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putString("fromClass", "UserUpdateActivity");
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
            }
        }
    }
    private class UserUpdateCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //ユーザー情報画面に戻る
            finish();
        }
    }
}
