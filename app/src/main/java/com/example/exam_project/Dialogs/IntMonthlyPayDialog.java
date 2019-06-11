package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.R;

import org.joda.time.LocalDate;

public class IntMonthlyPayDialog extends DialogFragment {

    Account account;
    Customer customer;
    Long customerId;

    Button cancel_billing_btn;
    EditText amountToBill_input;
    SharedPreferences sharedPreferences;

    public IntMonthlyPayDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        customerId = getArguments().getLong("customerId");
        customer = getArguments().getParcelable("customerObject");
        account = getArguments().getParcelable("accountObject");

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MyPrefs", 0);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View mView = getActivity().getLayoutInflater().inflate(R.layout.int_monthly_pay_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.monthpaydia_title_txt));

        cancel_billing_btn = mView.findViewById(R.id.cancel_billing_btn);

        cancel_billing_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("monthlybill_" + account.getAccountType().toString() + "_" + customerId).apply();
                amountToBill_input.setText("");
                Toast.makeText(getActivity(), "Successfully removed your Billing Plan!", Toast.LENGTH_SHORT).show();
            }
        });
        amountToBill_input = mView.findViewById(R.id.amount_to_bill);

        String monthlyBilling = sharedPreferences.getString("monthlybill_" + account.getAccountType().toString() + "_" + customerId, null);
        if (monthlyBilling != null) {
            amountToBill_input.setText(monthlyBilling.substring(monthlyBilling.indexOf(" ") + 1));
        }

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
