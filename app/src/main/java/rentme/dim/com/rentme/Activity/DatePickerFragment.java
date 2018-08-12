package rentme.dim.com.rentme.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import rentme.dim.com.rentme.R;

public class DatePickerFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.YEAR;
        int month = calendar.MONTH;
        int day = calendar.DAY_OF_MONTH;

        DatePickerDialog da = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog, (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);

        calendar.add(Calendar.DATE, 0);
        Date currentDate = calendar.getTime();
        da.getDatePicker().setMinDate(currentDate.getTime());

        calendar.add(Calendar.DATE, 7);
        Date maxDate = calendar.getTime();
        da.getDatePicker().setMaxDate(maxDate.getTime());
        return da;
    }
}