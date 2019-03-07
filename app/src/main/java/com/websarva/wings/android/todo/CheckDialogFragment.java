package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import ActionType.FromClass;
import Logic.TodoDeleteLogic;
import Logic.TodoFinishedLogic;
import Logic.TodoUnderWayDeleteLogic;
import ActionType.DialogActionType;
import model.TodoItem;
import model.TodoUnderWay;

public class CheckDialogFragment extends DialogFragment {
    // 実行分岐用の変数
    private int root = 0;
    //アクティビティの終了分岐
    private boolean finishActivity;
    //操作するTODOのID
    private TodoItem todoItem;
    //操作する進捗のID
    private TodoUnderWay todoUnderWay;
    //遷移元クラス変数
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
        //実行分岐の取得
        root = getArguments().getInt("root");
        //遷移元クラスの取得
        fromClass = getArguments().getInt("fromClass");
        //アクティビティ終了分岐の取得
        finishActivity = getArguments().getBoolean("finishActivity");
        //TODOを取得
        todoItem = (TodoItem)getArguments().getSerializable("todoItem");
        //進捗を取得
        todoUnderWay = (TodoUnderWay) getArguments().getSerializable("todoUnderWay");


        //キャンセルボタンを設定
        builder.setNegativeButton(R.string.tv_cancel, new DialogButtonClickListener());
        AlertDialog dialog = builder.create();

        return dialog;
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int witch){
            switch(witch){
                case DialogInterface.BUTTON_POSITIVE:
                    ResultDialogFragment dialogFragment = new ResultDialogFragment();
                    Bundle bundle = new Bundle();
                    switch (root) {
                        //ログアウト
                        case DialogActionType.TODO_LOGOUT:
                            // ログアウト完了ダイアログフラグメントオブジェクトを生成
                            bundle.putString("title",getString(R.string.tv_logout) + getString(R.string.tv_finish));
                            bundle.putString("msg", getString(R.string.tv_logoutResult));
                            bundle.putString("button", getString(R.string.tv_close));
                            bundle.putInt("fromClass", fromClass);
                            bundle.putBoolean("finishActivity",finishActivity);
                            dialogFragment.setArguments(bundle);
                            dialogFragment.show(getActivity().getSupportFragmentManager(), "ResultDialogFragment");
                            break;
                        //TODO削除
                        case DialogActionType.TODO_DELETE:
                            //TODOを削除する
                            TodoDeleteLogic todoDeleteLogic = new TodoDeleteLogic();
                            //削除結果の成否で分岐する
                            if(todoDeleteLogic.execute(todoItem.getId(),getActivity())) {
                                // 削除完了ダイアログフラグメントオブジェクトを生成
                                bundle.putString("title",  getString(R.string.tv_delete) + getString(R.string.tv_finish));
                                bundle.putString("msg", getString(R.string.tv_deleteResult));
                                bundle.putString("button", getString(R.string.tv_close));
                                bundle.putBoolean("finishActivity",finishActivity);
                                bundle.putInt("root",root);
                                bundle.putInt("fromClass", fromClass);
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getActivity().getSupportFragmentManager(), "ResultDialogFragment");
                            }else{
                                //エラーダイアログフラグメントオブジェクトを生成
                                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                                ArrayList<String> inputErrorList = new ArrayList<String>();
                                inputErrorList.add("TODOの削除に失敗しました");
                                Bundle args = new Bundle();
                                args.putStringArrayList("inputErrorList", inputErrorList);
                                args.putBoolean("onResumeFlag",false);
                                args.putInt("fromClass", fromClass);
                                errorDialogFragment.setArguments(args);
                                errorDialogFragment.show(getActivity().getSupportFragmentManager(), "ErrorDialogFragment");
                            }
                            break;
                        //TODOの完了
                        case DialogActionType.TODO_FINISHED:
                            //TODOを完了させる
                            TodoFinishedLogic todoFinishedLogic = new TodoFinishedLogic();
                            if(todoFinishedLogic.execute(todoItem,getActivity())) {
                                // TODO完了ダイアログフラグメントオブジェクトを生成
                                bundle.putString("title",getString(R.string.tv_todo) + getString(R.string.tv_finish));
                                bundle.putString("msg", getString(R.string.tv_finishedResult));
                                bundle.putString("button", getString(R.string.tv_close));
                                bundle.putBoolean("finishActivity",finishActivity);
                                bundle.putInt("fromClass", fromClass);
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getActivity().getSupportFragmentManager(), "ResultDialogFragment");
                            }else{
                                //エラーダイアログフラグメントオブジェクトを生成
                                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                                ArrayList<String> inputErrorList = new ArrayList<String>();
                                inputErrorList.add("TODOの完了に失敗しました");
                                Bundle args = new Bundle();
                                args.putStringArrayList("inputErrorList", inputErrorList);
                                args.putBoolean("onResumeFlag",false);
                                args.putInt("fromClass", fromClass);
                                errorDialogFragment.setArguments(args);
                                errorDialogFragment.show(getActivity().getSupportFragmentManager(), "ErrorDialogFragment");
                            }
                            break;
                        //進捗の削除
                        case DialogActionType.UNDER_WAY_DELETE:
                            //進捗を削除する
                            TodoUnderWayDeleteLogic todoUnderWayDeleteLogic = new TodoUnderWayDeleteLogic();
                            //削除結果の成否で分岐する
                            if(todoUnderWayDeleteLogic.execute(todoUnderWay)) {
                                // 削除完了ダイアログフラグメントオブジェクトを生成
                                bundle.putString("title",  getString(R.string.tv_delete) + getString(R.string.tv_finish));
                                bundle.putString("msg", getString(R.string.tv_underWayDeleteResult));
                                bundle.putString("button", getString(R.string.tv_close));
                                bundle.putBoolean("finishActivity",finishActivity);
                                bundle.putInt("root",root);
                                bundle.putInt("fromClass", fromClass);
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getActivity().getSupportFragmentManager(), "ResultDialogFragment");
                            }else{
                                //エラーダイアログフラグメントオブジェクトを生成
                                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                                ArrayList<String> inputErrorList = new ArrayList<String>();
                                inputErrorList.add("進捗の削除に失敗しました");
                                Bundle args = new Bundle();
                                args.putStringArrayList("inputErrorList", inputErrorList);
                                args.putBoolean("onResumeFlag",false);
                                args.putInt("fromClass", fromClass);
                                errorDialogFragment.setArguments(args);
                                errorDialogFragment.show(getActivity().getSupportFragmentManager(), "ErrorDialogFragment");
                            }
                            break;
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    switch (fromClass){
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
                    break;
            }
        }
    }
}
