package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.exam_project.Account;
import com.example.exam_project.Activities.OverviewActivity;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateInternalAccValue;
import com.example.exam_project.R;

public class WithdrawDialog extends DialogFragment {

    Account account;
    Long customerId;

    public WithdrawDialog() {
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = getArguments().getParcelable("accountObject");
        customerId = getArguments().getLong("customerId");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.withdraw_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.withdrawdialog_titlepartial01_txt) + account.getAccountType().toString());

        final EditText amountToWithdraw_input = mView.findViewById(R.id.amount_to_withdraw);

        builder.setPositiveButton(getString(R.string.withdrawdialog_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double amountToWithdraw = Double.parseDouble(amountToWithdraw_input.getText().toString());

                // Make sure there's enough money to transfer by getting current amount of money on account
                if (amountToWithdraw > account.getAmount()) {
                    Toast.makeText(getActivity(), getString(R.string.withdrawdialog_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.DEFAULT, amountToWithdraw).execute();
                Toast.makeText(getActivity(), getString(R.string.withdrawdialog_msg02_toast), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), OverviewActivity.class).putExtra("customerId", customerId));

            }
        });
        builder.setNegativeButton(getString(R.string.withdrawdialog_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}