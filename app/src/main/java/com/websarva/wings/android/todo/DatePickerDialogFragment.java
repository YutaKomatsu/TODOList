package com.websarva.wings.android.todo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

import ActionType.FromClass;

public class DatePickerDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String baseDate = getArguments().getString("Date");
        String[] date = null;
        int year;
        int month;
        int day;


        if (!StringUtils.isBlank(baseDate)) {
            date = baseDate.split("-");
            year = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]) - 1;
            day = Integer.parseInt(date[2]);
        } else {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }


        int callFromClass = getArguments().getInt("class");


        if (callFromClass == FromClass.TODO_REGISTER_ACTIVITY) {
            return new DatePickerDialog(getActivity(), (TodoRegisterActivity) getActivity(), year, month, day);
        } else if (callFromClass == FromClass.TODO_UPDATE_ACTIVITY) {
            return new DatePickerDialog(getActivity(), (TodoUpdateActivity) getActivity(), year, month, day);
        } else if (callFromClass == FromClass.TODO_SEARCH_ACTIVITY) {
            return new DatePickerDialog(getActivity(), (TodoSearchActivity) getActivity(), year, month, day);
        } else  {
            return null;
        }

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year,
                          int monthOfYear, int dayOfMonth) {
    }

}