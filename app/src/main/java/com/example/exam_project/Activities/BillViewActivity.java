package com.example.exam_project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.MailHandler.SendMail;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

import java.text.DecimalFormat;


public class BillViewActivity extends AppCompatActivity {

    Bill bill;
    Customer customer;
    Long customerId;

    int generatedValue;
    Account defaultAccount;

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
            bill_activity_txt.setText(bill_activity_txt.getText().toString() + getString(R.string.billview_active_txt));
        } else {
            bill_activity_txt.setText(bill_activity_txt.getText().toString() + getString(R.string.billview_inactive_txt));
        }

        // Get & SetText Bill Date
        TextView bill_date_txt = findViewById(R.id.bill_date_txt);

        bill_date_txt.setText(bill_date_txt.getText().toString() + bill.getLocalDate());

        // Get paid status
        TextView bill_isPaid_txt = findViewById(R.id.bill_isPaid_txt);
        if (bill.isPaid()) {
            bill_isPaid_txt.setText(bill_isPaid_txt.getText().toString() + getString(R.string.billview_paid_txt));
        } else {
            bill_isPaid_txt.setText(bill_isPaid_txt.getText().toString() + getString(R.string.billview_unpaid_txt));
        }

        // Gets autopay status
        TextView bill_autopay_txt = findViewById(R.id.bill_autopay_txt);
        if (bill.isAutoPay()) {
            bill_autopay_txt.setText(bill_autopay_txt.getText().toString() + getString(R.string.billview_automatic_txt));
        } else {
            bill_autopay_txt.setText(bill_autopay_txt.getText().toString() + getString(R.string.billview_manual_txt));
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
                    Toast.makeText(BillViewActivity.this, getString(R.string.billviewdia_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                generatedValue = new NemID().getRandomValue();
                SendMail sendMail = new SendMail(BillViewActivity.this, SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                sendMail.execute();
                NemIDDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.billviewdia_negative_btn), new DialogInterface.OnClickListener() {
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

    void NemIDDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BillViewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.nemid_verify_dialog, null);
        builder.setTitle(getString(R.string.nemiddia_title_txt));

        final EditText nemIdNumber_input = mView.findViewById(R.id.nemid_field);

        builder.setPositiveButton(getString(R.string.nemiddia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int nemIdNumber = Integer.parseInt(nemIdNumber_input.getText().toString());
                String email = customer.getEmail();

                if (nemIdNumber == generatedValue) {
                    new HRT_SetExtAccValByEmail(defaultAccount, bill.getBillCollectorEmail(), bill.getValue(), customer, HRT_SetExtAccValByEmail.SendType.BILL, bill.getId()).execute();
                    startActivity(new Intent(BillViewActivity.this, BillsActivity.class).putExtra("customerId", customerId));
                    Toast.makeText(BillViewActivity.this, getString(R.string.nemiddia_success_toast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BillViewActivity.this, getString(R.string.nemiddia_msg02_toast), Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.nemiddia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(mView);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.kea_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Transactions
            case R.id.kea_menu_item01:
                Intent transactionsActivity = new Intent(this, TransactionsActivity.class);
                transactionsActivity.putExtra("customerId", customerId);
                this.startActivity(transactionsActivity);
                break;
            // Bills
            case R.id.kea_menu_item02:
                Intent billsActivity = new Intent(this, BillsActivity.class);
                billsActivity.putExtra("customerId", customerId);
                this.startActivity(billsActivity);
                break;
            // Change Password
            case R.id.kea_menu_item03:
                Intent passChangeActivity = new Intent(this, PassChangeActivity.class);
                passChangeActivity.putExtra("customerId", customerId);
                this.startActivity(passChangeActivity);
                break;
            // Log out
            case R.id.kea_menu_item04:
                Toast.makeText(this, getString(R.string.infospinner_msg01_toast), Toast.LENGTH_SHORT).show();
                this.startActivity(new Intent(this, MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
