package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateUserAccountsById;
import com.example.exam_project.R;

import java.util.ArrayList;
import java.util.List;

public class NewAccDialog extends DialogFragment {

    Long customerId;
    Customer customer;

    public NewAccDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        customerId = getArguments().getLong("customerId");
        customer = getArguments().getParcelable("customerObject");


        // Checks if user already has all accounts possible and returns, before a dialog can be built
        List<String> list = addNewAccounts(customer);
        if (list.size() == 0) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.overview_msg05_toast), Toast.LENGTH_SHORT).show();
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.new_acc_dialog, null);
        builder.setView(mView);
        builder.setTitle(getActivity().getString(R.string.overview_newacctitle_txt));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner accTypeSpinner = mView.findViewById(R.id.new_acc_spinner);
        accTypeSpinner.setAdapter(adapter);

        builder.setPositiveButton(getActivity().getString(R.string.overview_newaccdiapositive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = accTypeSpinner.getSelectedItem().toString();

                new HRT_UpdateUserAccountsById(customerId, selection).execute();

                // Refreshing page to display new account
                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.passch_cancel_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }


    List<String> addNewAccounts(Customer currCustomer) {
        List<String> list = new ArrayList<>();
        list.add("DEFAULT");
        list.add("BUSINESS");
        list.add("BUDGET");
        list.add("PENSION");
        list.add("SAVINGS");

        for (Account account : currCustomer.getAccounts()) {
            if (account.getAccountType() == Account.AccountType.DEFAULT) {
                list.remove("DEFAULT");
            } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
                list.remove("BUSINESS");
            } else if (account.getAccountType() == Account.AccountType.BUDGET) {
                list.remove("BUDGET");
            } else if (account.getAccountType() == Account.AccountType.PENSION) {
                list.remove("PENSION");
            } else if (account.getAccountType() == Account.AccountType.SAVINGS) {
                list.remove("SAVINGS");
            }

        }
        return list;
    }
}
