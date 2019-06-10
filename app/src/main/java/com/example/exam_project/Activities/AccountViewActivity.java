package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.Dialogs.ExtDepositDialog;
import com.example.exam_project.Dialogs.IntDepositDialog;
import com.example.exam_project.Dialogs.IntMonthlyPayDialog;
import com.example.exam_project.Dialogs.WithdrawDialog;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.R;

import java.text.DecimalFormat;


public class AccountViewActivity extends AppCompatActivity {

    Account account;
    Customer customer;
    Long customerId;
    Button withdraw_btn;
    Button deposit_btn;
    Button deposit_external_btn;
    Button monthlypay_btn;

    CustomerData customerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        account = getIntent().getParcelableExtra("accountObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        if (customer == null) {
            try {
                customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        withdraw_btn = findViewById(R.id.withdraw_btn);
        deposit_btn = findViewById(R.id.deposit_btn);
        deposit_external_btn = findViewById(R.id.deposit_external_btn);
        monthlypay_btn = findViewById(R.id.monthly_pay_btn);
        monthlypay_btn.setVisibility(View.INVISIBLE);

        // If account type is NOT Pension, hide Withdraw button OR if account is default (cant withdraw from default account to nothing)
        if ((account.getAccountType() == Account.AccountType.PENSION && customer.getAge() < 77)) {
            withdraw_btn.setVisibility(View.INVISIBLE);
            deposit_btn.setVisibility(View.INVISIBLE);
            deposit_external_btn.setVisibility(View.INVISIBLE);
        } else if (account.getAccountType() == Account.AccountType.DEFAULT) {
            withdraw_btn.setVisibility(View.INVISIBLE);
        }
        if (account.getAccountType() == Account.AccountType.BUDGET || account.getAccountType() == Account.AccountType.SAVINGS) {
            monthlypay_btn.setVisibility(View.VISIBLE);
        }

        // Change header text to show type of account
        TextView account_type = findViewById(R.id.account_type);
        account_type.setText(account.getAccountType().toString() + " " + account_type.getText());


        EditText account_amount = findViewById(R.id.account_amount);
        // Formatting double to avoid trailing 0's
        double held_amount = Double.parseDouble(new DecimalFormat("0.#####").format(account.getAmount()));
        account_amount.setText(account_amount.getText() + " " + held_amount);

    }

    public void onClickBack(View v) {
        super.onStart();
        startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
    }

    public void onClickIntDeposit(View v) {
        super.onStart();
        Bundle args = new Bundle();
        args.putParcelable("customerObject", customer);
        args.putParcelable("accountObject", account);
        args.putLong("customerId", customerId);
        DialogFragment depositDialog = new IntDepositDialog();
        depositDialog.setArguments(args);
        depositDialog.show(getSupportFragmentManager(), "deposit_dialog");
    }

    public void onClickWithdraw (View v) {
        super.onStart();
        Bundle args = new Bundle();
        args.putParcelable("accountObject", account);
        args.putLong("customerId", customerId);
        DialogFragment withdrawDialog = new WithdrawDialog();
        withdrawDialog.setArguments(args);
        withdrawDialog.show(getSupportFragmentManager(), "withdraw_dialog");
    }

    public void onClickUserDeposit(View v) {
        super.onStart();
        Bundle args = new Bundle();
        args.putParcelable("customerObject", customer);
        args.putParcelable("accountObject", account);
        args.putLong("customerId", customerId);
        DialogFragment extDepositDialog = new ExtDepositDialog();
        extDepositDialog.setArguments(args);
        extDepositDialog.show(getSupportFragmentManager(), "user_deposit_dialog");

    }

    public void onClickMonthlyBilling(View v) {
        super.onStart();
        Bundle args = new Bundle();
        args.putParcelable("accountObject", account);
        args.putParcelable("customerObject", customer);
        args.putLong("customerId", customerId);
        IntMonthlyPayDialog intMonthlyPayDialog = new IntMonthlyPayDialog();
        intMonthlyPayDialog.setArguments(args);
        intMonthlyPayDialog.show(getSupportFragmentManager(), "int_monthly_pay_dialog");
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
