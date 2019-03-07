package com.websarva.wings.android.todo;

import android.app.DatePickerDialog;
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
import Logic.TodoRegisterLogic;
import Logic.UserListLogic;
import model.TodoItem;
import model.User;

public class TodoRegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView ExpireDate;
    private KeyValuePairAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_register);

        //ユーザーリストを取得
        UserListLogic userListLogic = new UserListLogic();
        List<User> userlist = userListLogic.execute(TodoRegisterActivity.this);
        //ユーザーIDとユーザー名をスピナーへ登録
        ArrayList<Pair<String, String>> userItemList = new ArrayList<Pair<String, String>>();
        String ppp;
        for(User user : userlist) {
            userItemList.add(new Pair<String, String>(user.getId(), user.getName()));
        }
        // スピナーにアダプターを設定
        adapter = new KeyValuePairAdapter(this, R.layout.spinner_item, userItemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.tvUserNameInput);
        spinner.setAdapter(adapter);

        //期限日のTextViewにidをセット
        ExpireDate = findViewById(R.id.tvExpireDateInput);

        Button todoRegisterButton = findViewById(R.id.tvTodoRegisterExecute);
        todoRegisterButton.setOnClickListener(new TodoRegisterExecute());

        Button todoRegisterCancelButton = findViewById(R.id.tvTodoRegisterCancel);
        todoRegisterCancelButton.setOnClickListener(new TodoRegisterCancel());
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

        //期限日のチェック
        try {
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year,monthOfYear,dayOfMonth);
            date = cal.get(Calendar.YEAR) + "-"  + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            ExpireDate.setText( date );
            /*Calendar now = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);


            //現在日より前になっていないか確認
            int diff = cal.compareTo(now);
            if (diff < 0){
                inputErrorList.add("※期限日の入力値が現在より前になっています");
            }else{
                ExpireDate.setText( date );
            }*/
        } catch (NumberFormatException e) {
            inputErrorList.add("※期限日の入力値が正しくありません");
        } catch (IllegalArgumentException e) {
            inputErrorList.add("※期限日の入力値が正しくありません");
        }
        if(inputErrorList.size() != 0){
            Bundle args = new Bundle();
            args.putStringArrayList("inputErrorList", inputErrorList);
            args.putBoolean("onResumeFlag",false);
            args.putInt("fromClass",FromClass.TODO_REGISTER_ACTIVITY);
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(),"ErrorDialogFragment");
        }
    }

    //日付設定用ダイアログ
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("class","TodoRegisterActivity");
        bundle.putString("Date",ExpireDate.getText().toString());
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "DatePickerDialogFragment");
    }

    private class TodoRegisterCancel implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //TODOリスト画面に戻る
            finish();
        }
    }

    //登録ボタン押下時
    private class TodoRegisterExecute implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //入力された項目名を取得
            EditText inputWorkName = findViewById(R.id.tvWorkNameInput);
            String workName = inputWorkName.getText().toString();
            //選択されたユーザー名の内部値のユーザーIDを取得
            Spinner selectUser = findViewById(R.id.tvUserNameInput);
            Pair<String, String> userinfo = adapter.getItem(selectUser.getSelectedItemPosition());
            String userId = userinfo.first;
            //期限日の取得
            TextView inputExpireDate = findViewById(R.id.tvExpireDateInput);
            String expireDate = inputExpireDate.getText().toString();

            ArrayList<String> inputErrorList = new ArrayList<String>();


            //入力値のチェック
            if (StringUtils.isBlank(workName)) {
                inputErrorList.add("※項目名が入力されていません");
            }
            if (StringUtils.isBlank(expireDate)) {
                inputErrorList.add("※期限日が入力されていません");
            }

            if (inputErrorList.size() == 0) {
                //TODO情報を登録する
                // TODOインスタンスの生成
                TodoItem todoItem = new TodoItem(workName, userId, expireDate);

                // 登録処理
                TodoRegisterLogic todoRegisterLogic = new TodoRegisterLogic();
                boolean isRegister = todoRegisterLogic.execute(todoItem, TodoRegisterActivity.this);

                // 登録成功時の処理
                if (isRegister) {
                    // 登録完了ダイアログフラグメントオブジェクトを生成
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.tv_todoRegisterResultTitle));
                    bundle.putString("msg", getString(R.string.tv_todoRegisterResult));
                    bundle.putString("button", getString(R.string.tv_backTodoListMenu));
                    bundle.putBoolean("finishActivity",true);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(),"ResultDialogFragment");
                }else{
                    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                    inputErrorList.add("TODOの登録に失敗しました");
                    Bundle args = new Bundle();
                    args.putStringArrayList("inputErrorList", inputErrorList);
                    args.putBoolean("onResumeFlag",false);
                    args.putInt("fromClass",FromClass.TODO_REGISTER_ACTIVITY);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
                }
            } else {
                //エラーダイアログフラグメントオブジェクトを生成
                ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("inputErrorList", inputErrorList);
                args.putBoolean("onResumeFlag",false);
                args.putInt("fromClass",FromClass.TODO_REGISTER_ACTIVITY);
                dialogFragment.setArguments(args);
                dialogFragment.show(getSupportFragmentManager(), "ErrorDialogFragment");
            }
        }
    }
}
