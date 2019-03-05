package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import model.DialogActionType;

public class ResultDialogFragment extends DialogFragment {
    //  分岐用private変数
    private boolean finishActivity;
    private int root = 0;
    private String fromClass;

    @Override
    public Dialog onCreateDialog(Bundle errorInstanceState){
        //ダイアログのキャンセルを無効化
        this.setCancelable(false);
        //ダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのタイトルを設定
        builder.setTitle(getArguments().getString("title"));
        //ダイアログのメッセージを設定
        builder.setMessage(getArguments().getString("msg"));
        //ログイン画面へ遷移するボタンを設定
        builder.setPositiveButton(getArguments().getString("button"), new DialogButtonClickListener());
        //アクティビティを終了させるかどうかを判定するフラグを取得
        finishActivity = getArguments().getBoolean("finishActivity");
        root = getArguments().getInt("root");
        fromClass = getArguments().getString("fromClass");
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int witch){
            switch(witch){
                case DialogInterface.BUTTON_POSITIVE:
                    if(finishActivity) {
                        getActivity().finish();
                    }else{
                        if(StringUtils.equals(fromClass,"TodoListActivity")){
                                TodoListActivity todoListActivity = (TodoListActivity) getActivity();
                                todoListActivity.onRestart();
                        }else if(StringUtils.equals(fromClass,"TodoInformationActivity")){
                                TodoInformationActivity todoInformationActivity = (TodoInformationActivity)getActivity();
                                todoInformationActivity.onRestart();
                        }else if(StringUtils.equals(fromClass,"TodoUnderWayListActivity")){
                                TodoUnderWayListActivity todoUnderWayListActivity = (TodoUnderWayListActivity) getActivity();
                                todoUnderWayListActivity.onRestart();
                        }else if(StringUtils.equals(fromClass,"TodoUnderWayInformationActivity")){
                                TodoUnderWayInformationActivity todoUnderWayInformationActivity = (TodoUnderWayInformationActivity)getActivity();
                                todoUnderWayInformationActivity.onRestart();
                        }
                    }
                    break;
            }
        }
    }
}
