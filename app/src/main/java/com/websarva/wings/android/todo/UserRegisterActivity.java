package com.websarva.wings.android.todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ActionType.FromClass;
import Logic.UserListLogic;
import Logic.UserRegisterLogic;
import model.User;

public class UserRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        Button userRegisterButton = findViewById(R.id.tvUserRegisterExecute);
        userRegisterButton.setOnClickListener(new UserRegisterExecute());

        Button userRegisterCancelButton = findViewById(R.id.tvUserRegisterCancel);
        userRegisterCancelButton.setOnClickListener(new UserRegisterCancel());
    }

    //登録ボタン押下時
    private class UserRegisterExecute implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //入力されたユーザーIDを取得
            EditText inputUserID = findViewById(R.id.tvUserIdInput);
            String userId = inputUserID.getText().toString();
            //入力されたユーザー名を取得
            EditText inputUserName = findViewById(R.id.tvUserNameInput);
            String userName = inputUserName.getText().toString();
            //入力されたパスワードを取得
            EditText inputPassword = findViewById(R.id.tvPasswordInput);
            String password = inputPassword.getText().toString();
            //入力された確認用パスワードを取得
            EditText inputCheckPassword = findViewById(R.id.tvCheckPasswordInput);
            String checkPassword = inputCheckPassword.getText().toString();

            ArrayList<String> inputErrorList = new ArrayList<String>();


            //入力値のチェック
            if(StringUtils.isBlank(userId)) {
                inputErrorList.add("※ユーザーIDが入力されていません");
            }
            if(StringUtils.contains(userId, " ") || StringUtils.contains(userId, "　")) {
                inputErrorList.add("※ユーザーIDに空白文字が入力されています、空白文字は入力しないでください");
            }
            if(!StringUtils.isAsciiPrintable(userId)) {
                inputErrorList.add("※ユーザーIDに全角文字が入力されています、全角文字は入力しないでください");
            }
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


            //ユーザーリストの取得
            UserListLogic userListLogic = new UserListLogic();
            List<User> userList = userListLogic.execute(UserRegisterActivity.this);
            //ユーザーIDが重複していないか確認
            for(User user : userList) {
                if(StringUtils.equals(user.getId(),userId)) {
                    inputErrorList.add("※そのユーザーIDは既に使用されているため、登録できません");
                    break;
                }
            }

            if(inputErrorList.size() == 0) {
                //ユーザー情報を登録する
                User registerUser = new User(userId,userName,password,1);
                UserRegisterLogic userRegisterLogic = new UserRegisterLogic();
                boolean isUserRegister = userRegisterLogic.execute(registerUser, UserRegisterActivity.this);
                if(isUserRegister) {
                    // 登録完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title",getString(R.string.tv_userRegisterResultTitle));
                    bundle.putString("msg",getString(R.string.tv_userRegisterResult));
                    bundle.putString("button",getString(R.string.tv_backLoginMenu));
                    bundle.putBoolean("finishActivity",true);
                    bundle.putInt("fromClass", FromClass.USER_REGISTER_ACTIVITY);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                //登録失敗時の処理
                }else {
                    //エラーダイアログフラグメントオブジェクトを生成
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("ユーザーの登録に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putInt("fromClass", FromClass.USER_REGISTER_ACTIVITY);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
                }
            }else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass", FromClass.USER_REGISTER_ACTIVITY);
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
            }
        }
    }
    private class UserRegisterCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //ログイン画面に戻る
            finish();
        }
    }
}
