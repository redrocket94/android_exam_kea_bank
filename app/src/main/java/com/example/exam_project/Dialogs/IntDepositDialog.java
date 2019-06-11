package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.exam_project.Account;
import com.example.exam_project.Activities.OverviewActivity;
import com.example.exam_project.Customer;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateInternalAccValue;
import com.example.exam_project.Modules.MailHandler.SendMail;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

import java.util.ArrayList;
import java.util.List;

public class IntDepositDialog extends DialogFragment {

    Account account;
    Customer customer;
    Long customerId;

    Spinner accTypeSpinner;
    double amountToDeposit;
    int generatedValue;


    public IntDepositDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        customerId = getArguments().getLong("customerId");
        customer = getArguments().getParcelable("customerObject");
        account = getArguments().getParcelable("accountObject");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.deposit_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.intdepositdia_titlepartial01_txt) + " " + account.getAccountType().toString() + " " + getString(R.string.intdepositdia_titlepartial02_txt));

        accTypeSpinner = mView.findViewById(R.id.internal_accs_spinner);
        final EditText amountToDeposit_input = mView.findViewById(R.id.amount_to_deposit);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getUserAccounts());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accTypeSpinner.setAdapter(adapter);

        builder.setPositiveButton(getString(R.string.intdepositdia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                amountToDeposit = Double.parseDouble(amountToDeposit_input.getText().toString());

                // Make sure there's enough money to transfer by getting current amount of money on account
                if (amountToDeposit > account.getAmount()) {
                    Toast.makeText(getActivity(), getString(R.string.intdepositdia_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                } else if (amountToDeposit < 0.5) {
                    Toast.makeText(getActivity(), getString(R.string.intdepositdia_msg02_toast), Toast.LENGTH_SHORT).show();
                    return;
                }

                // If PENSION type, get NemID verification
                if (accTypeSpinner.getSelectedItem().toString().equalsIgnoreCase("Pension")) {
                    generatedValue = new NemID().getRandomValue();
                    SendMail sendMail = new SendMail(getActivity(), SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                    sendMail.execute();

                    verifyNemId();
                } else {

                    new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.valueOf(accTypeSpinner.getSelectedItem().toString()), amountToDeposit).execute();
                    Toast.makeText(getActivity(), getString(R.string.intdepositdia_msg03_toast), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), OverviewActivity.class).putExtra("customerId", customerId));
                }


            }
        });
        builder.setNegativeButton(getString(R.string.intdepositdia_negative_btn), new DialogInterface.OnClickListener() {
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
        args.putString("accountType", accTypeSpinner.getSelectedItem().toString());
        args.putInt("generatedValue", generatedValue);
        args.putString("intOrExt", "int");
        args.putDouble("amountToDeposit", amountToDeposit);
        DialogFragment nemIdDialog = new NemIdDialog();
        nemIdDialog.setArguments(args);
        nemIdDialog.show(getActivity().getSupportFragmentManager(), "nemid_verify_dialog");
    }

    // Returns a list of the users accounts that are approved and NOT the current account
    List<String> getUserAccounts() {
        List<String> userAccountsList = new ArrayList<>();
        for (Account userAccount :
                customer.getAccounts()) {
            if (userAccount.getAccountType() != account.getAccountType() && userAccount.isApproved()) {
                userAccountsList.add(userAccount.getAccountType().toString());
            }
        }
        return userAccountsList;
    }

}
