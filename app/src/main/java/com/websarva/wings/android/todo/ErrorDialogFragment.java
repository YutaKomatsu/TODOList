package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import ActionType.FromClass;

public class ErrorDialogFragment extends DialogFragment {
    private boolean onResumeFlag;
    private int fromClass;

    @Override
    public Dialog onCreateDialog(Bundle errorInstanceState){
        //エラーメッセージ格納変数
        String inputErrorMsg = "";
        //ダイアログのキャンセルを無効化
        this.setCancelable(false);
        //ダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのタイトルを設定
        builder.setTitle(R.string.tv_error);
        //ダイアログのメッセージを取得
        List<String> inputErrorList = getArguments().getStringArrayList("inputErrorList");
        //画面更新フラグを取得
        onResumeFlag = getArguments().getBoolean("onResumeFlag");
        //遷移元クラスを取得
        fromClass = getArguments().getInt("fromClass");
        //エラーメッセージを設定
        for(String inputError : inputErrorList){
            inputErrorMsg += inputError + "\n";
        }
        builder.setMessage(inputErrorMsg);

        // 閉じるボタンを設定
        builder.setPositiveButton(R.string.tv_close, new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int witch){
            switch(witch){
                case DialogInterface.BUTTON_POSITIVE:
                    //更新する場合
                    if(onResumeFlag) {
                        switch (fromClass){
                            //ログイン画面から呼び出された場合、ログイン画面を更新する
                            case FromClass.TODO_LOGIN_ACTIVITY:
                                TodoLoginActivity todoLoginActivity = (TodoLoginActivity)getActivity();
                                todoLoginActivity.onResume();
                                break;
                            //TODOリスト画面から呼び出された場合、TODOリスト画面を更新する
                            case FromClass.TODO_LIST_ACTIVITY:
                                TodoListActivity todoListActivity = (TodoListActivity)getActivity();
                                todoListActivity.onRestart();
                                break;
                            //TODO詳細情報画面から呼び出された場合、TODO詳細情報画面を更新する
                            case FromClass.TODO_INFORMATION_ACTIVITY:
                                TodoInformationActivity todoInformationActivity = (TodoInformationActivity)getActivity();
                                todoInformationActivity.onRestart();
                                break;
                            //TODO進捗リスト画面から呼び出された場合、TODO進捗リスト画面を更新する
                            case FromClass.TODO_UNDER_WAY_LIST_ACTIVITY:
                                TodoUnderWayListActivity todoUnderWayListActivity = (TodoUnderWayListActivity)getActivity();
                                todoUnderWayListActivity.onRestart();
                                break;
                            //TODO進捗詳細情報画面から呼び出された場合、TODO進捗詳細情報画面を更新する
                            case FromClass.TODO_UNDER_WAY_INFORMATION_ACTIVITY:
                                TodoUnderWayInformationActivity todoUnderWayInformationActivity = (TodoUnderWayInformationActivity)getActivity();
                                todoUnderWayInformationActivity.onRestart();
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        }
    }
}
