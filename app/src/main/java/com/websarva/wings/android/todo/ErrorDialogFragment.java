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
        String inputErrorMsg = "";
        //ダイアログのキャンセルを無効化
        this.setCancelable(false);
        //ダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのタイトルを設定
        builder.setTitle(R.string.tv_error);
        //ダイアログのメッセージを設定
        List<String> inputErrorList = getArguments().getStringArrayList("inputErrorList");
        onResumeFlag = getArguments().getBoolean("onResumeFlag");
        fromClass = getArguments().getInt("fromClass");
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
                    if(onResumeFlag) {
                        switch (fromClass){
                            case FromClass.TODO_LOGIN_ACTIVITY:
                                TodoLoginActivity todoLoginActivity = (TodoLoginActivity)getActivity();
                                todoLoginActivity.onResume();
                                break;
                            case FromClass.TODO_LIST_ACTIVITY:
                                TodoListActivity todoListActivity = (TodoListActivity)getActivity();
                                todoListActivity.onRestart();
                                break;
                            case FromClass.TODO_INFORMATION_ACTIVITY:
                                TodoInformationActivity todoInformationActivity = (TodoInformationActivity)getActivity();
                                todoInformationActivity.onRestart();
                                break;
                            case FromClass.TODO_UNDER_WAY_LIST_ACTIVITY:
                                TodoUnderWayListActivity todoUnderWayListActivity = (TodoUnderWayListActivity)getActivity();
                                todoUnderWayListActivity.onRestart();
                                break;
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
