package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import Logic.TodoUnderWayRegisterLogic;
import model.TodoUnderWay;

public class TodoUnderWayRegisterActivity extends AppCompatActivity {
    private int todoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_under_way_register);

        //TODOIDの取得
        Intent intent = getIntent();
        todoId = intent.getIntExtra("todoId",-1);

        Button todoUnderWayRegisterButton = findViewById(R.id.tvTodoUnderWayRegisterExecute);
        todoUnderWayRegisterButton.setOnClickListener(new TodoUnderWayRegisterExecute());

        Button todoUnderWayRegisterCancelButton = findViewById(R.id.tvTodoUnderWayRegisterCancel);
        todoUnderWayRegisterCancelButton.setOnClickListener(new TodoUnderWayRegisterCancel());
    }

    private class TodoUnderWayRegisterCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    //登録ボタン押下時
    private class TodoUnderWayRegisterExecute implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //入力された題名を取得
            EditText inputUnderWayTitle = findViewById(R.id.tvUnderWayTitleInput);
            String underWayTitle = inputUnderWayTitle.getText().toString();
            //入力された題名を取得
            EditText inputUnderWay = findViewById(R.id.tvUnderWayInput);
            String underWay = inputUnderWay.getText().toString();

            ArrayList<String> inputErrorList = new ArrayList<String>();


            //入力値のチェック
            if (StringUtils.isBlank(underWayTitle)) {
                inputErrorList.add("※題名が入力されていません");
            }
            if (StringUtils.isBlank(underWay)) {
                inputErrorList.add("※進捗が入力されていません");
            }

            if (inputErrorList.size() == 0) {
                //進捗情報を登録する
                // 進捗インスタンスの生成
                TodoUnderWay todoUnderWay = new TodoUnderWay(todoId, underWayTitle, underWay);

                // 登録処理
                TodoUnderWayRegisterLogic todoUnderWayRegisterLogic = new TodoUnderWayRegisterLogic();
                boolean isRegister = todoUnderWayRegisterLogic.execute(todoUnderWay);

                // 登録成功時の処理
                if (isRegister) {
                    // 登録完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tv_todoUnderWayRegisterResultTitle));
                    bundle.putString("msg", getString(R.string.tv_todoUnderWayRegisterResult));
                    bundle.putString("button", getString(R.string.tv_backTodoUnderWayListMenu));
                    bundle.putBoolean("finishActivity",true);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                }else{
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("進捗の登録に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putString("fromClass", "TodoUnderWayRegisterActivity");
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
                }
            } else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putString("fromClass", "TodoUnderWayRegisterActivity");
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
