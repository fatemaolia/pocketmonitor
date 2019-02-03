package fats.com.pocketmonitor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener onDateSet;
    private boolean isModal = false;
    DatePickerDialog dp;

    /*----------Default constructor is needed so app doesn't crash on screen rotation----------*/
    public DatePickerFragment() {
    }

    /*---------------------------------Initialises stuff---------------------------------------*/
    public static DatePickerFragment newInstance()
    {
        DatePickerFragment frag = new DatePickerFragment();
        frag.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG
        return frag;
    }


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    /*------------------Get the date passed from the main fragment----------------------------*/
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        dp =  new DatePickerDialog(getActivity(), onDateSet , year, month, day) ;
        dp.getDatePicker().setMaxDate(c.getTimeInMillis());

    /*---------------- Return instance of DatePickerDialog -----------------------------------*/
        return  dp;

    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }

}
