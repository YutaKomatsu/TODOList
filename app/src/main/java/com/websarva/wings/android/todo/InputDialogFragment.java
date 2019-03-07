package com.websarva.wings.android.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import ActionType.DialogActionType;
import ActionType.FromClass;
import model.User;

public class InputDialogFragment extends DialogFragment {
    private User user;
    private int action;
    private View inputView;
    @Override
    public Dialog onCreateDialog(Bundle errorInstanceState){
        //レイアウトの呼び出し
        LayoutInflater factory = LayoutInflater.from(getActivity());
        inputView = factory.inflate(R.layout.input_dialog, null);
        //ダイアログビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //ダイアログのタイトルを設定
        builder.setTitle(getArguments().getString("title"));
        //ダイアログのメッセージを設定
        builder.setMessage(getArguments().getString("msg"));
        //ダイアログのViewを設定
        builder.setView(inputView);
        //ログイン画面へ遷移するボタンを設定
        builder.setPositiveButton(getArguments().getString("button"), new InputDialogFragment.DialogButtonClickListener());
        //ユーザー情報を取得
        user = (User)getArguments().getSerializable("user");
        //実行内容を取得
        action = getArguments().getInt("action");


        //キャンセルボタンを設定
        builder.setNegativeButton(R.string.tv_cancel, new InputDialogFragment.DialogButtonClickListener());
        AlertDialog dialog = builder.create();

        return dialog;
    }
    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int witch){
            switch(witch){
                case DialogInterface.BUTTON_POSITIVE:
                    EditText editText = inputView.findViewById(R.id.dialog_edittext);
                    //入力値とパスワードを比較
                    if(StringUtils.equals(editText.getText().toString(),user.getPass())){
                        if(action == DialogActionType.SHOW_PASSWORD) {
                            // UserInformationActivityのインスタンスを取得
                            UserInformationActivity userInformationActivity = (UserInformationActivity) getActivity();
                            userInformationActivity.showPassword();
                        }else{
                            //ユーザー情報更新画面へ遷移
                            Intent intent = new Intent(getContext(),UserUpdateActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                        }
                    }else{
                        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
                        ArrayList<String> inputErrorList = new ArrayList<>();
                        inputErrorList.add("パスワードが違います。");
                        Bundle args = new Bundle();
                        args.putStringArrayList("inputErrorList", inputErrorList);
                        args.putBoolean("onResumeFlag",false);
                        args.putInt("fromClass", FromClass.USER_INFORMATION_ACTIVITY);
                        dialogFragment.setArguments(args);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "ErrorDialogFragment");
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    }
}
