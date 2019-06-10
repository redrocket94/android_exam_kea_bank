package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.MailHandler.SendMail;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

public class ExtDepositDialog extends DialogFragment {

    Customer customer;
    Account account;
    Long customerId;

    double amountToWithdraw;
    int generatedValue;

    String email;

    public ExtDepositDialog() {
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customer = getArguments().getParcelable("customerObject");
        account = getArguments().getParcelable("accountObject");
        customerId = getArguments().getLong("customerId");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.user_deposit_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.extdepositdia_titlepartial01_txt) + account.getAccountType().toString());

        final EditText amountToWithdraw_input = mView.findViewById(R.id.amount_to_withdraw);
        final EditText email_input = mView.findViewById(R.id.email_input);

        builder.setPositiveButton(getString(R.string.extdepositdia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                amountToWithdraw = Double.parseDouble(amountToWithdraw_input.getText().toString());
                email = email_input.getText().toString();

                // Make sure there's enough money to transfer by getting current amount of money on account
                if (amountToWithdraw > account.getAmount()) {
                    Toast.makeText(getActivity(), getString(R.string.extdepositdia_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                generatedValue = new NemID().getRandomValue();
                SendMail sendMail = new SendMail(getActivity(), SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                sendMail.execute();
                verifyNemId();

            }
        });
        builder.setNegativeButton(getString(R.string.extdepositdia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    private void verifyNemId() {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putParcelable("customerObject", customer);
        args.putParcelable("accountObject", account);
        args.putString("email", email);
        args.putInt("generatedValue", generatedValue);
        args.putString("intOrExt", "ext");
        args.putDouble("amountToWithdraw", amountToWithdraw);
        args.putString("accountType", account.getAccountType().toString());
        DialogFragment nemIdDialog = new NemIdDialog();
        nemIdDialog.setArguments(args);
        nemIdDialog.show(getActivity().getSupportFragmentManager(), "nemid_verify_dialog");
    }
}
