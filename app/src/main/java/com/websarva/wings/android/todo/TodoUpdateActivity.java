package com.websarva.wings.android.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ActionType.FromClass;
import Logic.TodoUpdateLogic;
import Logic.UserListLogic;
import model.TodoItem;
import model.User;

public class TodoUpdateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private TodoItem todoItem;
    private EditText workNameView;
    private KeyValuePairAdapter adapter;
    private TextView expireDateView;
    private TextView finishedDateView;
    private int DateType;   //1:期限日、2:完了日


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_update);

        //表示するTODOのIDを取得
        Intent intent = getIntent();
        todoItem = (TodoItem) intent.getSerializableExtra("todoItem");

        //ユーザーリストを取得
        UserListLogic userListLogic = new UserListLogic();
        List<User> userlist = userListLogic.execute(TodoUpdateActivity.this);
        //ユーザーIDとユーザー名をスピナーへ登録
        ArrayList<Pair<String, String>> userItemList = new ArrayList<Pair<String, String>>();
        for(User user : userlist) {
            userItemList.add(new Pair<String, String>(user.getId(), user.getName()));
        }
        // スピナーにアダプターを設定
        adapter = new KeyValuePairAdapter(this, R.layout.spinner_item, userItemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.tvUserNameInput);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(new Pair<String, String>(todoItem.getUserId(),todoItem.getUserName())));

        //取得したTODOをEditTextにセット
        workNameView = findViewById(R.id.tvWorkNameInput);
        workNameView.setText(todoItem.getWorkName());
        expireDateView = findViewById(R.id.tvExpireDateInput);
        expireDateView.setText(todoItem.getExpireDate());
        finishedDateView = findViewById(R.id.tvFinishedDateInput);
        finishedDateView.setText(todoItem.getFinishedDate());

        //ボタンの設定
        Button finishedDateDeleteButton = findViewById(R.id.finishedDateDelete);
        finishedDateDeleteButton.setOnClickListener(new finishedDateDelete());
        Button todoUpdateExecuteButton = findViewById(R.id.tvTodoUpdateExecute);
        todoUpdateExecuteButton.setOnClickListener(new TodoUpdateExecute());
        Button todoUpdateCancelButton = findViewById(R.id.tvTodoUpdateCancel);
        todoUpdateCancelButton.setOnClickListener(new TodoUpdateActivity.TodoUpdateCancel());
    }

    //期限日のセット
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //日付を格納する変数
        String date = null;
        //エラーダイアログフラグメントオブジェクトを生成
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        //エラーメッセージを格納するリスト
        ArrayList<String> inputErrorList = new ArrayList<String>();

        try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year,monthOfYear,dayOfMonth);
            date = cal.get(Calendar.YEAR) + "-"  + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            if(DateType == 1) {
                expireDateView.setText(date);
            }else {
                finishedDateView.setText(date);
            }
        } catch (NumberFormatException e) {
            inputErrorList.add("※期限日の入力値が正しくありません");
        } catch (IllegalArgumentException e) {
            inputErrorList.add("※期限日の入力値が正しくありません");
        }
        if(inputErrorList.size() != 0){
            Bundle args = new Bundle();
            args.putStringArrayList("inputErrorList", inputErrorList);
            args.putBoolean("onResumeFlag",false);
            args.putInt("fromClass",FromClass.TODO_UPDATE_ACTIVITY);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
        }

    }

    //日付設定用ダイアログ
    public void showExpireDatePickerDialog(View v) {
        DateType = 1;
        DialogFragment expireDateFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("class", FromClass.TODO_UPDATE_ACTIVITY);
        bundle.putString("Date",expireDateView.getText().toString());

        expireDateFragment.setArguments(bundle);
        expireDateFragment.show(getSupportFragmentManager(), "ExpireDatePickerDialogFragment");
    }

    public void showFinishedDatePickerDialog(View v) {
        DateType = 2;
        DialogFragment finishedDateFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("class", FromClass.TODO_UPDATE_ACTIVITY);
        bundle.putString("Date",finishedDateView.getText().toString());
        finishedDateFragment.setArguments(bundle);
        finishedDateFragment.show(getSupportFragmentManager(), "FinishedDatePickerDialogFragment");
    }
    private class finishedDateDelete implements View.OnClickListener {
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finishedDateView.setText("");
        }
    }

    private class TodoUpdateCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    //登録ボタン押下時
    private class TodoUpdateExecute implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //入力された項目名を取得
            EditText inputWorkName = findViewById(R.id.tvWorkNameInput);
            String workName = inputWorkName.getText().toString();
            //選択されたユーザー名の内部値のユーザーIDを取得
            Spinner selectUser = findViewById(R.id.tvUserNameInput);
            Pair<String, String> userInfo = adapter.getItem(selectUser.getSelectedItemPosition());
            String userId = userInfo.first;
            //期限日の取得
            TextView inputExpireDate = findViewById(R.id.tvExpireDateInput);
            String expireDate = inputExpireDate.getText().toString();
            //完了日の取得
            TextView inputFinishedDate = findViewById(R.id.tvFinishedDateInput);
            String finishedDate = inputFinishedDate.getText().toString();

            ArrayList<String> inputErrorList = new ArrayList<String>();


            //入力値のチェック
            if (StringUtils.isBlank(workName)) {
                inputErrorList.add("※項目名が入力されていません");
            }
            if (StringUtils.isBlank(expireDate)) {
                inputErrorList.add("※期限日が入力されていません");
            }
            if(StringUtils.isBlank(finishedDate)){
                finishedDate = null;
            }

            if (inputErrorList.size() == 0) {
                //TODO情報を更新する
                // TODOインスタンスの生成
                TodoItem newtodoItem = new TodoItem(todoItem.getId(),workName, userId, expireDate,finishedDate);

                // 更新処理
                TodoUpdateLogic todoUpdateLogic = new TodoUpdateLogic();
                boolean isUpdate = todoUpdateLogic.execute(newtodoItem, TodoUpdateActivity.this);

                // 更新成功時の処理
                if (isUpdate) {
                    // 更新完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tv_todoUpdateResultTitle));
                    bundle.putString("msg", getString(R.string.tv_todoUpdateResult));
                    bundle.putString("button", getString(R.string.tv_backTodoInformation));
                    bundle.putBoolean("finishActivity",true);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                }else{
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("TODOの更新に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putInt("fromClass",FromClass.TODO_UPDATE_ACTIVITY);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
                }
            } else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_UPDATE_ACTIVITY);
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
