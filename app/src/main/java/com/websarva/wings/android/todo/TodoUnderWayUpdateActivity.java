package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import ActionType.FromClass;
import Logic.TodoUnderWayUpdateLogic;
import model.TodoUnderWay;

public class TodoUnderWayUpdateActivity extends AppCompatActivity {
    //private変数
    private TodoUnderWay todoUnderWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_under_way_update);

        //TODOIDの取得
        Intent intent = getIntent();
        todoUnderWay = (TodoUnderWay)intent.getSerializableExtra("todoUnderWay");

        EditText underWayTitle = findViewById(R.id.tvUnderWayTitleInput);
        underWayTitle.setText(todoUnderWay.getUnderWayTitle());
        EditText underWay = findViewById(R.id.tvUnderWayInput);
        underWay.setText(todoUnderWay.getUnderWay());

        Button todoUnderWayUpdateButton = findViewById(R.id.tvTodoUnderWayUpdateExecute);
        todoUnderWayUpdateButton.setOnClickListener(new TodoUnderWayUpdateExecute());

        Button todoUnderWayUpdateCancelButton = findViewById(R.id.tvTodoUnderWayUpdateCancel);
        todoUnderWayUpdateCancelButton.setOnClickListener(new TodoUnderWayUpdateCancel());
    }

    private class TodoUnderWayUpdateCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    //登録ボタン押下時
    private class TodoUnderWayUpdateExecute implements View.OnClickListener {
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
                //進捗情報を更新する
                // 進捗インスタンスの生成
                TodoUnderWay updateTodoUnderWay = new TodoUnderWay(todoUnderWay.getTodoId(),todoUnderWay.getSubId(), underWayTitle, underWay);

                // 更新処理
                TodoUnderWayUpdateLogic todoUnderWayUpdateLogic = new TodoUnderWayUpdateLogic();
                boolean isRegister = todoUnderWayUpdateLogic.execute(updateTodoUnderWay);

                // 更新成功時の処理
                if (isRegister) {
                    // 更新完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tv_todoUnderWayUpdateResultTitle));
                    bundle.putString("msg", getString(R.string.tv_todoUnderWayUpdateResult));
                    bundle.putString("button", getString(R.string.tv_backTodoUnderWayInformationMenu));
                    bundle.putBoolean("finishActivity",true);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                }else{
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("進捗の更新に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putInt("fromClass",FromClass.TODO_UNDER_WAY_UPDATE_ACTIVITY);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
                }
            } else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_UNDER_WAY_UPDATE_ACTIVITY);
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
