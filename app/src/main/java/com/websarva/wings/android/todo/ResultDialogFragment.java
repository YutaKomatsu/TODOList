package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import ActionType.FromClass;

public class ResultDialogFragment extends DialogFragment {
    //  分岐用private変数
    private boolean finishActivity;
    private int root = 0;
    private int fromClass;

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
        //アクティビティ終了分岐の取得
        finishActivity = getArguments().getBoolean("finishActivity");
        //実行分岐の取得
        root = getArguments().getInt("root");
        //遷移元クラスの取得
        fromClass = getArguments().getInt("fromClass");

        AlertDialog dialog = builder.create();
        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int witch){
            switch(witch){
                case DialogInterface.BUTTON_POSITIVE:
                    //アクティビティ終了フラグが正の場合、アクティビティを終了する
                    if(finishActivity) {
                        getActivity().finish();
                    }else{
                        switch (fromClass){
                            //TODOリスト画面から呼び出された場合、TODOリスト画面を更新する
                            case FromClass.TODO_LIST_ACTIVITY:
                                TodoListActivity todoListActivity = (TodoListActivity) getActivity();
                                todoListActivity.onRestart();
                                break;
                            //TODO詳細情報画面から呼び出された場合、TODO詳細情報画面を更新する
                            case FromClass.TODO_INFORMATION_ACTIVITY:
                                TodoInformationActivity todoInformationActivity = (TodoInformationActivity)getActivity();
                                todoInformationActivity.onRestart();
                                break;
                            //TODO進捗リスト画面から呼び出された場合、TODO進捗リスト画面を更新する
                            case FromClass.TODO_UNDER_WAY_LIST_ACTIVITY:
                                TodoUnderWayListActivity todoUnderWayListActivity = (TodoUnderWayListActivity) getActivity();
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
