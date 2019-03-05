package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ErrorDialogFragment extends DialogFragment {
    boolean onResumeFlag;
    private String fromClass;

    @Override
    public Dialog onCreateDialog(Bundle errorInstanceState){
        String inputErrorMsg = "";

        //ダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのタイトルを設定
        builder.setTitle(R.string.tv_error);
        //ダイアログのメッセージを設定
        List<String> inputErrorList = getArguments().getStringArrayList("inputErrorList");
        onResumeFlag = getArguments().getBoolean("onResumeFlag");
        fromClass = getArguments().getString("fromClass");
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
                        if(StringUtils.equals(fromClass,"TodoLoginActivity")){
                            TodoLoginActivity todoLoginActivity = (TodoLoginActivity)getActivity();
                            todoLoginActivity.onResume();
                        }else if(StringUtils.equals(fromClass,"TodoListActivity")){
                            TodoListActivity todoListActivity = (TodoListActivity)getActivity();
                            todoListActivity.onRestart();
                        }else if(StringUtils.equals(fromClass,"TodoInformationActivity")){
                            TodoInformationActivity todoInformationActivity = (TodoInformationActivity)getActivity();
                            todoInformationActivity.onRestart();
                        }else if(StringUtils.equals(fromClass,"TodoUnderWayListActivity")){
                            TodoUnderWayListActivity todoUnderWayListActivity = (TodoUnderWayListActivity)getActivity();
                            todoUnderWayListActivity.onRestart();
                        } else if(StringUtils.equals(fromClass,"TodoUnderWayInformationActivity")){
                            TodoUnderWayInformationActivity todoUnderWayInformationActivity = (TodoUnderWayInformationActivity)getActivity();
                            todoUnderWayInformationActivity.onRestart();
                        }
                    }
                    break;
            }
        }
    }
}
