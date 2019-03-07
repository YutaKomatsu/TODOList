package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ActionType.DialogActionType;
import Logic.UserInfoLogic;
import model.User;

public class UserInformationActivity extends AppCompatActivity {
    //private変数
    private User user;
    private TextView userIdView;
    private TextView userNameView;
    private TextView passwordView;
    private boolean userChangeFlag = false;
    private boolean showPasswordFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        //表示するユーザー情報を取得
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("user");

        //取得したユーザー情報をTextViewにセット
        userIdView = findViewById(R.id.tvUserIdView);
        userIdView.setText(user.getId());
        userNameView = findViewById(R.id.tvUserNameView);
        userNameView.setText(user.getName());
        passwordView = findViewById(R.id.tvPasswordView);
        passwordView.setText(user.getPass());

        //ボタンをセット
        Button showPasswordButton = findViewById(R.id.tvShowPassword);
        showPasswordButton.setOnClickListener(new ShowPasswordButton());
        Button userUpdateButton = findViewById(R.id.tvUserUpdateExecute);
        userUpdateButton.setOnClickListener(new UserUpdate());
        Button userInformationCloseButton = findViewById(R.id.tvUserInformationClose);
        userInformationCloseButton.setOnClickListener(new UserInformationClose());
    }

    @Override
    public void onRestart(){
        super.onRestart();
        //更新後のユーザー情報を取得
        UserInfoLogic userInfoLogic = new UserInfoLogic();
        user = userInfoLogic.execute(user.getId(),UserInformationActivity.this);
        userNameView.setText(user.getName());
        passwordView.setText(user.getPass());
        if(showPasswordFlag){
            showPasswordFlag = false;
        }else{
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }


    //パスワード表示ボタン押下時
    private class ShowPasswordButton implements View.OnClickListener {
        @Override
        public void onClick(View view){
            // パスワード入力ダイアログフラグメントオブジェクトを生成
            InputDialogFragment dialogFragment = new InputDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tv_inputPassword));
            bundle.putString("msg", getString(R.string.tv_formPassword));
            bundle.putString("button", getString(R.string.tv_passwordShow));
            bundle.putInt("action",DialogActionType.SHOW_PASSWORD);
            bundle.putSerializable("user",user);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(),"InputDialogFragment");
        }
    }

    //ユーザー情報更新ボタン押下時
    private class UserUpdate implements View.OnClickListener {
        @Override
        public void onClick(View view){
            // パスワード入力ダイアログフラグメントオブジェクトを生成
            InputDialogFragment dialogFragment = new InputDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tv_inputPassword));
            bundle.putString("msg", getString(R.string.tv_formPassword));
            bundle.putString("button", getString(R.string.tv_userUpdateTransition));
            bundle.putInt("action",DialogActionType.USER_UPDATE);
            bundle.putSerializable("user",user);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(),"InputDialogFragment");
        }
    }

    private class UserInformationClose implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            if(userChangeFlag) {
                setResult(RESULT_OK);
            }else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }

    public void showPassword(){
        passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        showPasswordFlag = true;
        onRestart();
    }
}
