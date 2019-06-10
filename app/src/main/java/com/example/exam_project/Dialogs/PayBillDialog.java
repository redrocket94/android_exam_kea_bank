package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.MailHandler.SendMail;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

public class PayBillDialog extends DialogFragment {

    Bill bill;
    Customer customer;
    Long customerId;
    String email;
    double amount;

    Account defaultAccount;
    int generatedValue;


    public PayBillDialog() {
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customer = getArguments().getParcelable("customerObject");
        customerId = getArguments().getLong("customerId");
        bill = getArguments().getParcelable("billObject");

        email = bill.getBillCollectorEmail();
        amount = bill.getValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.pay_bill_dialog, null);
        builder.setView(mView);

        builder.setTitle(getString(R.string.billviewdia_title_txt));

        TextView dialog_pay_txt = mView.findViewById(R.id.dialog_pay_txt);
        dialog_pay_txt.setText(dialog_pay_txt.getText().toString() + bill.getValue() + "\n" + getString(R.string.billviewdia_infopartial01_txt) +
                "\t\t" + bill.getBillCollectorEmail() + "\n\n " + getString(R.string.billviewdia_infopartial02_txt) + " " +
                getString(R.string.billviewdia_infopartial03_txt) + "\n " + getString(R.string.billviewdia_infopartial04_txt) + " " + getString(R.string.billviewdia_infopartial05_txt));

        builder.setPositiveButton(getString(R.string.billviewdia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                defaultAccount = getUserDefaultAcc(customer);
                if (defaultAccount.getAmount() < bill.getValue()) {
                    Toast.makeText(getActivity(), getString(R.string.billviewdia_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                generatedValue = new NemID().getRandomValue();
                SendMail sendMail = new SendMail(getActivity(), SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                sendMail.execute();
                verifyNemId();
            }
        });
        builder.setNegativeButton(getString(R.string.billviewdia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    Account getUserDefaultAcc(Customer currCustomer) {
        for (Account account : currCustomer.getAccounts()) {
            if (account.getAccountType() == Account.AccountType.DEFAULT) {
                return account;
            }
        }
        return null;
    }

    private void verifyNemId() {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putParcelable("customerObject", customer);
        args.putString("email", email);
        args.putInt("generatedValue", generatedValue);
        args.putString("intOrExt", "ext");
        args.putDouble("amountToWithdraw", amount);
        args.putParcelable("accountObject", defaultAccount);
        args.putLong("billId", bill.getId());
        DialogFragment nemIdDialog = new NemIdDialog();
        nemIdDialog.setArguments(args);
        nemIdDialog.show(getActivity().getSupportFragmentManager(), "nemid_verify_dialog");
    }
}
