package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.R;

import org.joda.time.LocalDate;

public class IntMonthlyPayDialog extends DialogFragment {

    Account account;
    Customer customer;
    Long customerId;


    public IntMonthlyPayDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        customerId = getArguments().getLong("customerId");
        customer = getArguments().getParcelable("customerObject");
        account = getArguments().getParcelable("accountObject");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View mView = getActivity().getLayoutInflater().inflate(R.layout.int_monthly_pay_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.monthpaydia_title_txt));

        final EditText amountToBill_input = mView.findViewById(R.id.amount_to_bill);

        builder.setPositiveButton(getString(R.string.intdepositdia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double amountToBill = Double.parseDouble(amountToBill_input.getText().toString());
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();

                editor.putString("monthlybill_" + account.getAccountType().toString() + "_" + customerId,
                        (new LocalDate().getMonthOfYear() + 1) +
                                " " +
                                amountToBill);
                editor.apply();
                Toast.makeText(getActivity(), getString(R.string.monthpaydia_msg01_toast), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.intdepositdia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
