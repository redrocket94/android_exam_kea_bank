package com.example.exam_project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.Data;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateAccountValue;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateUserAccountsById;
import com.example.exam_project.Modules.InfoSpinner;
import com.example.exam_project.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class AccountViewActivity extends AppCompatActivity {

    Account account;
    Customer customer;
    Long customerId;
    Button withdraw_btn;
    Button deposit_btn;

    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        account = getIntent().getParcelableExtra("accountObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        if (customer == null) {
            try {
                data = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, getApplicationContext(), customerId).connectSpinner();

        withdraw_btn = findViewById(R.id.withdraw_btn);
        deposit_btn = findViewById(R.id.deposit_btn);

        // If account type is NOT Pension, hide Withdraw button OR if account is default (cant withdraw from default account to nothing)
        if ((account.getAccountType() == Account.AccountType.PENSION && customer.getAge() < 77) || (account.getAccountType() == Account.AccountType.DEFAULT)) {
            withdraw_btn.setVisibility(View.INVISIBLE);
        }

        // Change header text to show type of account
        TextView account_type = findViewById(R.id.account_type);
        account_type.setText(account.getAccountType().toString() + " " + account_type.getText());


        EditText account_amount = findViewById(R.id.account_amount);
        // Formatting double to avoid trailing 0's
        double held_amount = Double.parseDouble(new DecimalFormat("0.#####").format(account.getAmount()));
        account_amount.setText(account_amount.getText() + " " + held_amount);

        deposit_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.deposit_dialog, null);
                builder.setTitle("Making deposit from: " + account.getAccountType().toString() + " account");

                final Spinner accTypeSpinner = mView.findViewById(R.id.internal_accs_spinner);
                final EditText amountToDeposit_input = mView.findViewById(R.id.amount_to_deposit);


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountViewActivity.this, android.R.layout.simple_spinner_item, getUserAccounts());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accTypeSpinner.setAdapter(adapter);

                builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double amountToDeposit = Double.parseDouble(amountToDeposit_input.getText().toString());
                        
                        // Make sure there's enough money to transfer by getting current amount of money on account
                        if (amountToDeposit > account.getAmount()) {
                            Toast.makeText(AccountViewActivity.this, "You cannot deposit more money than you have on your account!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (amountToDeposit < 0.5) {
                            Toast.makeText(AccountViewActivity.this, "You need to deposit more than 0.5!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new HRT_UpdateAccountValue(customerId, account.getAccountType(), Account.AccountType.valueOf(accTypeSpinner.getSelectedItem().toString()), amountToDeposit).execute();
                        startActivity(new Intent(AccountViewActivity.this, OverviewActivity.class).putExtra("customerId", customerId));

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

        });
    }

    public void onClickBack(View v) {
        super.onStart();
        startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
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
