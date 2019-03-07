package com.websarva.wings.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

import ActionType.FromClass;
import Logic.TodoItemLogic;
import ActionType.DialogActionType;
import model.TodoItem;
import model.User;

public class TodoInformationActivity extends AppCompatActivity {
    //private変数
    private int todoId;
    private User user;
    private TodoItem todoItem;
    private TextView workNameView;
    private TextView userNameView;
    private TextView expireDateView;
    private TextView finishedDateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_information);

        //表示するTODOのIDを取得
        Intent intent = getIntent();
        todoId = (int) intent.getSerializableExtra("todoId");
        user = (User) intent.getSerializableExtra("user");
        intent.removeExtra("todoId");
        intent.removeExtra("user");

        //IDを元にデータベースからTODOを取得
        TodoItemLogic todoItemLogic = new TodoItemLogic();
        todoItem = todoItemLogic.execute(todoId, TodoInformationActivity.this);

        //取得したTODOをTextViewにセット
        workNameView = findViewById(R.id.tvWorkNameView);
        workNameView.setText(todoItem.getWorkName());
        userNameView = findViewById(R.id.tvUserNameView);
        userNameView.setText(todoItem.getUserName());
        expireDateView = findViewById(R.id.tvExpireDateView);
        expireDateView.setText(todoItem.getExpireDate());
        finishedDateView = findViewById(R.id.tvFinishedDateView);
        if (StringUtils.equals(todoItem.getFinishedDate(), "0000-00-00")) {
            finishedDateView.setText(null);
        }else{
            finishedDateView.setText(todoItem.getFinishedDate());
        }

        //ボタンを設定
        Button todoUpdateButton = findViewById(R.id.tvTodoUpdateExecute);
        todoUpdateButton.setOnClickListener(new TodoUpdate());
        Button todoDeleteButton = findViewById(R.id.tvTodoDeleteExecute);
        todoDeleteButton.setOnClickListener(new TodoDelete());
        Button todoFinishedButton = findViewById(R.id.tvTodoFinishedExecute);
        todoFinishedButton.setOnClickListener(new TodoFinished());
        Button todoUnderWayButton = findViewById(R.id.tvTodoUnderWayExecute);
        todoUnderWayButton.setOnClickListener(new TodoUnderWay());
        Button todoInformationCloseButton = findViewById(R.id.tvTodoInformationClose);
        todoInformationCloseButton.setOnClickListener(new TodoInformationClose());
        //権限によってボタンの活性非活性を設定する
        if (!StringUtils.equals(user.getId(), todoItem.getUserId()) && user.getPermissions() != 2){
            todoUpdateButton.setEnabled(false);
            todoDeleteButton.setEnabled(false);
            todoFinishedButton.setEnabled(false);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //IDを元にデータベースからTODOを取得
        TodoItemLogic todoItemLogic = new TodoItemLogic();
        todoItem = todoItemLogic.execute(todoId,TodoInformationActivity.this);

        //取得したTODOをTextViewにセット
        workNameView.setText(todoItem.getWorkName());
        userNameView.setText(todoItem.getUserName());
        expireDateView.setText(todoItem.getExpireDate());
        if (StringUtils.equals(todoItem.getFinishedDate(), "0000-00-00")) {
            finishedDateView.setText(null);
        }else{
            finishedDateView.setText(todoItem.getFinishedDate());
        }
    }

    private class TodoInformationClose implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    private class TodoDelete implements View.OnClickListener{
        @Override
        public void onClick(View view){
            // 削除確認ダイアログフラグメントオブジェクトを生成
            CheckDialogFragment dialogFragment = new CheckDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString("title",getString(R.string.tv_check));
            bundle.putString("msg",getString(R.string.tv_deleteCheck));
            bundle.putString("button",getString(R.string.tv_delete));
            bundle.putInt("root",DialogActionType.TODO_DELETE);
            bundle.putInt("fromClass",FromClass.TODO_INFORMATION_ACTIVITY);
            bundle.putBoolean("finishActivity",true);
            //削除するTODOの情報をを渡す
            bundle.putSerializable("todoItem",todoItem);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(getSupportFragmentManager(),"CheckDialogFragment");
        }
    }

    private class TodoFinished implements View.OnClickListener{
        @Override
        public void onClick(View view){
            TodoItemLogic todoItemLogic = new TodoItemLogic();
            TodoItem oldItem = todoItemLogic.execute(todoItem.getId(),TodoInformationActivity.this);
            if(StringUtils.isBlank(oldItem.getFinishedDate())
                    || StringUtils.equals(oldItem.getFinishedDate(), "null")
                    || StringUtils.equals(oldItem.getFinishedDate(), "0000-00-00")){
                // 項目完了確認ダイアログフラグメントオブジェクトを生成
                CheckDialogFragment dialogFragment = new CheckDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.tv_check));
                bundle.putString("msg", getString(R.string.tv_finishedCheck));
                bundle.putString("button", getString(R.string.tv_finish));
                bundle.putInt("root", DialogActionType.TODO_FINISHED);
                bundle.putInt("fromClass",FromClass.TODO_INFORMATION_ACTIVITY);
                bundle.putBoolean("finishActivity", false);
                //完了させるTODOの情報を生成
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MONTH, 1);
                String finishedDate = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DAY_OF_MONTH);
                TodoItem finishedTodoItem = new TodoItem(todoItem.getId(), finishedDate);
                //完了処理用のTODOをセット
                bundle.putSerializable("todoItem", finishedTodoItem);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "CheckDialogFragment");
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                ArrayList<String> inputErrorList = new ArrayList<String>();
                inputErrorList.add("対象のTODOは既に完了しています");
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_INFORMATION_ACTIVITY);
                errorDialogFragment.setArguments(args);
                errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }

    private class TodoUpdate implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(TodoInformationActivity.this,TodoUpdateActivity.class);
            intent.putExtra("todoItem",todoItem);
            startActivity(intent);
        }
    }

    private class TodoUnderWay implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(TodoInformationActivity.this,TodoUnderWayListActivity.class);
            intent.putExtra("todoItem",todoItem);
            intent.putExtra("user",user);
            startActivity(intent);
        }
    }
}
