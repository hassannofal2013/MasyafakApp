package com.tirefiesama.masifk;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

         Calendar c =Calendar.getInstance();
        DatePickerDialog datePickerDialog;

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        /*
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                c.set(Calendar.YEAR,i);
                c.set(Calendar.MONTH,i1);
                c.set(Calendar.DAY_OF_MONTH,i2);

            }
        },year,month,day);



         */


       // c.getActdualMinimum(c.get(Calendar.DAY_OF_YEAR));

      //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 1000);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ 518400000);
       //datePickerDialog.show();

       //return  datePickerDialog;



        datePickerDialog =new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ 518400000);
       return datePickerDialog;


    }
}
