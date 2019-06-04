package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.Modules.InfoSpinner;
import com.example.exam_project.R;
import com.example.exam_project.Transaction;

import java.util.List;


public class TransactionsActivity extends AppCompatActivity {

    Customer customer;

    Long customerId;
    List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        customerId = getIntent().getLongExtra("customerId", 0);

        transactionList = getTransactionsList(customerId);

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, TransactionsActivity.this, customerId).connectSpinner();

        if (customer == null) {
            try {
                CustomerData customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If customerId is 0, return to loginactivity
        if (customerId == 0) {
            Toast.makeText(this, "There was an error retrieving your account customerData, please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        loadTransactions();

    }

    private List<Transaction> getTransactionsList(Long customerId) {

        return null;
    }

    private void loadTransactions() {
        TableLayout table = findViewById(R.id.transactionsTable);


        for (final Account account : customer.getAccounts()) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            table.addView(tableRow);

            TextView accountType = new TextView(this);
            accountType.setText(account.getAccountType().toString());
            accountType.setTextSize(18);
            table.addView(accountType);

            Button button = new Button(this);
            button.setText("See details");

            // Test if you have approval for accounts, if not then disable and set button text
            if (!account.isApproved()) {
                button.setClickable(false);
                button.setText("Approval Pending...");
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent(TransactionsActivity.this, AccountViewActivity.class);

                        // Passes parcelable account object in intent.putExtra for next intent for use
                        intent.putExtra("customerId", customerId);
                        intent.putExtra("accountObject", account);

                        // Start new AccountViewActivity
                        startActivity(intent);
                    }
                });
            }
            table.addView(button);

        }
    }
}
