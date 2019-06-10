package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Activities.OverviewActivity;
import com.example.exam_project.Customer;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateInternalAccValue;
import com.example.exam_project.R;

public class NemIdDialog extends DialogFragment {

    int generatedValue;
    Customer customer;
    Long customerId;
    Account account;
    double amountToDeposit;
    String intOrExt;
    String accountType;

    // Ext(ernal) transactions only vars
    String email;
    double amountToWithdraw;

    // Bill only
    Long billId;

    public NemIdDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        generatedValue = getArguments().getInt("generatedValue");
        customer = getArguments().getParcelable("customerObject");
        customerId = getArguments().getLong("customerId");
        account = getArguments().getParcelable("accountObject");
        amountToDeposit = getArguments().getDouble("amountToDeposit");
        intOrExt = getArguments().getString("intOrExt");
        if (intOrExt.equals("ext")) {
            accountType = getArguments().getString("accountType");
            email = getArguments().getString("email", email);
            amountToWithdraw = getArguments().getDouble("amountToWithdraw", amountToWithdraw);
            billId = getArguments().getLong("billId");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.nemid_verify_dialog, null);
        builder.setView(mView);
        builder.setTitle(getString(R.string.nemiddia_title_txt));

        final EditText nemIdNumber_input = mView.findViewById(R.id.nemid_field);

        builder.setPositiveButton(getString(R.string.nemiddia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int nemIdNumber = Integer.parseInt(nemIdNumber_input.getText().toString());

                if (nemIdNumber == generatedValue) {
                    if (intOrExt.equals("ext")) {
                        if (billId != null && billId != 0) {
                            System.out.println("bill entered");
                            new HRT_SetExtAccValByEmail(account, email, amountToWithdraw, customer, HRT_SetExtAccValByEmail.SendType.BILL, billId).execute();
                            startActivity(new Intent(getActivity(), OverviewActivity.class).putExtra("customerId", customerId));
                        } else if (billId == null || billId == 0) {
                            System.out.println("ext entered");
                            new HRT_SetExtAccValByEmail(account, email, amountToWithdraw, customer).execute();
                            startActivity(new Intent(getActivity(), OverviewActivity.class).putExtra("customerId", customerId));
                        }
                    } else if (intOrExt.equals("int")) {
                        System.out.println("sent as int");
                        new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.valueOf(accountType), amountToDeposit).execute();
                        startActivity(new Intent(getActivity(), OverviewActivity.class).putExtra("customerId", customerId));
                    }
                    Toast.makeText(getActivity(), getString(R.string.nemiddia_msg01_toast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.nemiddia_msg02_toast), Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.nemiddia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}


