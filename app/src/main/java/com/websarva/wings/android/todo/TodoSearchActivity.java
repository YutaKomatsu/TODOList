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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ActionType.FromClass;
import Logic.UserListLogic;
import model.Search;
import model.TodoItem;
import model.User;

public class TodoSearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private Search search;
    private KeyValuePairAdapter adapter;
    private TextView expireDateView;
    private TextView finishedDateView;
    private int DateType;   //1:期限日、2:完了日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_search);

        //ユーザーリストを取得
        UserListLogic userListLogic = new UserListLogic();
        List<User> userlist = userListLogic.execute(TodoSearchActivity.this);
        //ユーザーIDとユーザー名をスピナーへ登録
        ArrayList<Pair<String, String>> userItemList = new ArrayList<Pair<String, String>>();
        userItemList.add(new Pair<String, String>("",""));
        for(User user : userlist) {
            userItemList.add(new Pair<String, String>(user.getId(), user.getName()));
        }
        // スピナーにアダプターを設定
        adapter = new KeyValuePairAdapter(this, R.layout.spinner_item, userItemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.tvUserNameInput);
        spinner.setAdapter(adapter);

        //TextViewのIDを取得
        expireDateView = findViewById(R.id.tvExpireDateInput);
        finishedDateView = findViewById(R.id.tvFinishedDateInput);

        //ボタンの設定
        Button todoUpdateExecuteButton = findViewById(R.id.tvTodoSearchExecute);
        todoUpdateExecuteButton.setOnClickListener(new TodoSearchExecute());
        Button todoUpdateCancelButton = findViewById(R.id.tvTodoSearchCancel);
        todoUpdateCancelButton.setOnClickListener(new TodoSearchCancel());

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
            args.putInt("fromClass",FromClass.TODO_SEARCH_ACTIVITY);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
        }

    }

    //日付設定用ダイアログ
    public void showExpireDatePickerDialog(View v) {
        DateType = 1;
        DialogFragment expireDateFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("class", "TodoSearchActivity");
        expireDateFragment.setArguments(bundle);
        expireDateFragment.show(getSupportFragmentManager(), "ExpireDatePickerDialogFragment");
    }

    public void showFinishedDatePickerDialog(View v) {
        DateType = 2;
        DialogFragment finishedDateFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("class", "TodoSearchActivity");
        finishedDateFragment.setArguments(bundle);
        finishedDateFragment.show(getSupportFragmentManager(), "FinishedDatePickerDialogFragment");
    }

    private class TodoSearchCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    //登録ボタン押下時
    private class TodoSearchExecute implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //エラーリスト
            ArrayList<String> inputErrorList = new ArrayList<String>();
            //入力された項目名を取得
            EditText inputWorkName = findViewById(R.id.tvWorkNameInput);
            String workName = inputWorkName.getText().toString();
            //選択されたユーザー名の内部値のユーザーIDを取得
            Spinner selectUser = findViewById(R.id.tvUserNameInput);
            Pair<String, String> userinfo = adapter.getItem(selectUser.getSelectedItemPosition());
            String userName = userinfo.second;
            //期限日の取得
            TextView inputExpireDate = findViewById(R.id.tvExpireDateInput);
            String expireDate = inputExpireDate.getText().toString();
            //期限日の検索範囲の設定
            String expireType = "";
            RadioGroup radioExpireType = findViewById(R.id.tvExpireSearchType);
            int checkedId = radioExpireType.getCheckedRadioButtonId();
            if (checkedId != -1) {
                RadioButton selectedExpireType = findViewById(checkedId);
                if (StringUtils.equals(selectedExpireType.getText().toString(), getString(R.string.tv_until))) {
                    //以前
                    expireType = "1";
                } else {
                    //以降
                    expireType = "2";
                }
            }else{
                if(!StringUtils.isBlank(expireDate)){
                    inputErrorList.add("期限日の検索範囲を選択してください");
                }
            }
            //完了日の取得
            TextView inputFinishedDate = findViewById(R.id.tvFinishedDateInput);
            String finishedDate = inputFinishedDate.getText().toString();
            String finishedType = "";
            RadioGroup radioFinishedType = findViewById(R.id.tvFinishedSearchType);
            checkedId = radioFinishedType.getCheckedRadioButtonId();
            if (checkedId != -1) {
                RadioButton selectedFinishedType = findViewById(checkedId);
                if (StringUtils.equals(selectedFinishedType.getText().toString(), getString(R.string.tv_until))) {
                    //以前
                    finishedType = "1";
                } else {
                    //以降
                    finishedType = "2";
                }
            }else{
                if(!StringUtils.isBlank(finishedDate)){
                    inputErrorList.add("完了日の検索範囲を選択してください");
                }
            }

            if(inputErrorList.size() == 0) {
                //検索用TODO情報をセット
                TodoItem searchTodoItem = new TodoItem(workName, userName, expireDate, finishedDate);
                Search search = new Search(true, searchTodoItem, expireType, finishedType);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("search", search);
                setResult(RESULT_OK, resultIntent);
                finish();
            }else{
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_SEARCH_ACTIVITY);
                errorDialogFragment.setArguments(args);
                errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
