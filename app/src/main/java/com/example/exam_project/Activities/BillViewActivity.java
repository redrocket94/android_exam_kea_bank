package com.example.exam_project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.R;

import java.text.DecimalFormat;


public class BillViewActivity extends AppCompatActivity {

    Bill bill;
    Customer customer;
    Long customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);

        bill = getIntent().getParcelableExtra("billObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        // Ensure updated customer is pulled from server
        if (customer == null) {
            try {
                CustomerData customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Connect Spinner in View to its functionality
        //new InfoSpinner(this, getApplicationContext(), customerId).connectSpinner();

        // Change header text to show type of account
        TextView bill_welcome_txt = findViewById(R.id.bill_welcome_txt);
        bill_welcome_txt.setText(bill_welcome_txt.getText().toString() + "\n" + bill.getBillCollectorEmail());


        TextView bill_value_txt = findViewById(R.id.bill_value_txt);
        // Formatting double to avoid trailing 0's
        double bill_value_amount = Double.parseDouble(new DecimalFormat("0.#####").format(bill.getValue()));
        bill_value_txt.setText(bill_value_txt.getText().toString() + bill_value_amount);

        // Get & SetText Bill activity
        TextView bill_activity_txt = findViewById(R.id.bill_activity_txt);
        if (bill.isActive()) {
            bill_activity_txt.setText(bill_activity_txt.getText().toString() + "Active");
        } else {
            bill_activity_txt.setText(bill_activity_txt.getText().toString() + "Inactive");
        }

        // Get & SetText Bill Date
        TextView bill_date_txt = findViewById(R.id.bill_date_txt);

        bill_date_txt.setText(bill_date_txt.getText().toString() + bill.getLocalDate());

        // Get paid status
        TextView bill_isPaid_txt = findViewById(R.id.bill_isPaid_txt);
        if (bill.isPaid()) {
            bill_isPaid_txt.setText(bill_isPaid_txt.getText().toString() + "Paid");
        } else {
            bill_isPaid_txt.setText(bill_isPaid_txt.getText().toString() + "Unpaid");
        }

        // Gets autopay status
        TextView bill_autopay_txt = findViewById(R.id.bill_autopay_txt);
        if (bill.isAutoPay()) {
            bill_autopay_txt.setText(bill_autopay_txt.getText().toString() + "Automatic");
        } else {
            bill_autopay_txt.setText(bill_autopay_txt.getText().toString() + "Manual");
        }

        // Hides manual bill pay button if status is set to auto
        if (bill.isAutoPay() || bill.isPaid()) {
            Button pay_bill_btn = findViewById(R.id.pay_bill_btn);
            pay_bill_btn.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickBack(View v) {
        super.onStart();
        startActivity(new Intent(this, BillsActivity.class).putExtra("customerId", customerId));
    }


    public void onClickPayBill(View v) {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(BillViewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.pay_bill_dialog, null);

        builder.setTitle("Paying bill manually...");

        TextView dialog_pay_txt = mView.findViewById(R.id.dialog_pay_txt);
        dialog_pay_txt.setText(dialog_pay_txt.getText().toString() + bill.getValue() + "\nTo\t\t" + bill.getBillCollectorEmail() + "?\n\n If yes, hit \"PAY BILL\"\n If no, hit \"CANCEL\"");

        builder.setPositiveButton("Pay bill", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Account defaultAccount = getUserDefaultAcc(customer);
                if (defaultAccount.getAmount() < bill.getValue()) {
                    Toast.makeText(BillViewActivity.this, "You don't have enough funds to pay this bill!", Toast.LENGTH_SHORT).show();
                    return;
                }
                new HRT_SetExtAccValByEmail(defaultAccount, bill.getBillCollectorEmail(), bill.getValue(), customer, HRT_SetExtAccValByEmail.SendType.BILL, bill.getId()).execute();
                Toast.makeText(BillViewActivity.this, "Successfully paid bill!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BillViewActivity.this, BillsActivity.class).putExtra("customerId", customerId));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(mView);
        builder.show();
    }

    Account getUserDefaultAcc(Customer currCustomer) {
        for (Account account : currCustomer.getAccounts()) {
            if (account.getAccountType() == Account.AccountType.DEFAULT) {
                return account;
            }
        }
        return null;
    }
}
