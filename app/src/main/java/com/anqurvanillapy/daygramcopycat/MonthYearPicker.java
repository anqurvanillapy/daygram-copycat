package com.anqurvanillapy.daygramcopycat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Calendar;

/**
 * =================================================================================================
 * Custom NumberPicker: Month & Year
 * =================================================================================================
 */

public class MonthYearPicker extends DialogFragment {
    private static final int MIN_YEAR = 2010;
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.yearPicker);

        int month = cal.get(Calendar.MONTH) + 1;
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(month);
        monthPicker.setValue(month);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(year);
        yearPicker.setValue(year);

        builder.setView(dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPicker.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
