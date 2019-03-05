package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import Logic.TodoUnderWayLogic;
import model.DialogActionType;
import model.TodoItem;
import model.TodoUnderWay;
import model.User;

public class TodoUnderWayInformationActivity extends AppCompatActivity {
    //private変数
    private TodoUnderWay todoUnderWay;
    private User user;
    private TodoItem todoItem;
    private TextView underWayTitleView;
    private TextView underWayView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_under_way_information);

        //表示する進捗のIDを取得
        Intent intent = getIntent();
        todoUnderWay = (TodoUnderWay) intent.getSerializableExtra("todoUnderWay");
        user = (User)intent.getSerializableExtra("user");
        todoItem = (TodoItem)intent.getSerializableExtra("todoItem");
        intent.removeExtra("todoUnderWay");
        intent.removeExtra("user");
        intent.removeExtra("todoItem");

        //IDを元にデータベースから進捗情報を取得
        TodoUnderWayLogic todoUnderWayLogic = new TodoUnderWayLogic();
        todoUnderWay = todoUnderWayLogic.execute(todoUnderWay);

        //取得した進捗情報をTextViewにセット
        underWayTitleView = findViewById(R.id.tvUnderWayTitleView);
        underWayTitleView.setText(todoUnderWay.getUnderWayTitle());
        underWayView = findViewById(R.id.tvUnderWayView);
        underWayView.setText(todoUnderWay.getUnderWay());
        underWayView.setMovementMethod(ScrollingMovementMethod.getInstance());

        //ボタンを設定
        Button todoUnderWayUpdateButton = findViewById(R.id.tvTodoUnderWayUpdateExecute);
        todoUnderWayUpdateButton.setOnClickListener(new TodoUnderWayUpdate());
        Button todoUnderWayDeleteButton = findViewById(R.id.tvTodoUnderWayDeleteExecute);
        todoUnderWayDeleteButton.setOnClickListener(new TodoUnderWayDelete());
        Button todoUnderWayInformationCloseButton = findViewById(R.id.tvTodoUnderWayInformationClose);
        todoUnderWayInformationCloseButton.setOnClickListener(new TodoUnderWayInformationClose());
        if(user.getPermissions() != 2 && !StringUtils.equals(user.getId(),todoItem.getUserId())){
            todoUnderWayUpdateButton.setEnabled(false);
            todoUnderWayDeleteButton.setEnabled(false);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();

        //IDを元にデータベースから進捗情報を取得
        TodoUnderWayLogic todoUnderWayLogic = new TodoUnderWayLogic();
        todoUnderWay = todoUnderWayLogic.execute(todoUnderWay);

        //取得した進捗情報をTextViewにセット
        underWayTitleView.setText(todoUnderWay.getUnderWayTitle());
        underWayView.setText(todoUnderWay.getUnderWay());
    }

    private class TodoUnderWayInformationClose implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    private class TodoUnderWayDelete implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // 削除確認ダイアログフラグメントオブジェクトを生成
            CheckDialogFragment dialogFragment = new CheckDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title",getString(R.string.tv_check));
            bundle.putString("msg",getString(R.string.tv_deleteCheck));
            bundle.putString("button",getString(R.string.tv_delete));
            bundle.putInt("root",DialogActionType.UNDER_WAY_DELETE);
            bundle.putString("fromClass","TodoUnderWayInformationActivity");
            bundle.putBoolean("finishActivity",true);
            //削除するTODOの情報をを渡す
            bundle.putSerializable("todoUnderWay",todoUnderWay);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(),"CheckDialogFragment");
        }
    }

    private class TodoUnderWayUpdate implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(TodoUnderWayInformationActivity.this,TodoUnderWayUpdateActivity.class);
            intent.putExtra("todoUnderWay",todoUnderWay);
            startActivity(intent);
        }
    }
}
