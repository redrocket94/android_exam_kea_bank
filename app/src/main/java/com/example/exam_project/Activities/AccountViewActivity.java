package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.Data;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.Modules.InfoSpinner;
import com.example.exam_project.R;


public class AccountViewActivity extends AppCompatActivity {

    Account account;
    Customer customer;
    Long customerId;
    Button withdraw_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        account = getIntent().getParcelableExtra("accountObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        if (customer == null) {
            try {
                Data data = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, getApplicationContext(), customerId).connectSpinner();

        withdraw_btn = findViewById(R.id.withdraw_btn);

        // If account type is NOT Pension, hide Withdraw button
        if ((account.getAccountType() == Account.AccountType.PENSION && customer.getAge() < 77)) {
            withdraw_btn.setVisibility(View.INVISIBLE);
        }

        // Change header text to show type of account
        TextView account_type = findViewById(R.id.account_type);
        account_type.setText(account.getAccountType().toString() + " " + account_type.getText());

        EditText account_amount = findViewById(R.id.account_amount);
        account_amount.setText(account_amount.getText() + " " + account.getAmount());
    }

    public void onClickBack(View v) {
        super.onStart();
        startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
    }
}
