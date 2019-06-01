package com.example.exam_project.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.Data;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.Modules.CustomSpinner;
import com.example.exam_project.R;


public class OverviewActivity extends AppCompatActivity {

    Customer customer;

    Long customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        //customer = getIntent().getParcelableExtra("customerObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        // Connect Spinner in View to its functionality
        new CustomSpinner(this, getApplicationContext(), customerId).connectSpinner();

        if (customer == null) {
            try {
                Data data = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If customerId is 0, return to loginactivity
        if (customerId == 0) {
            Toast.makeText(this, "There was an error retrieving your account data, please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        // Set welcome text with users name
        TextView welcomeText = findViewById(R.id.welcomeText);
        if (customer.getFirstName().length() > 6) {
            welcomeText.setText("Welcome\n" + customer.getFirstName() + "!");
        } else {
            welcomeText.setText("Welcome " + customer.getFirstName() + "!");
        }

        loadAccounts();

        if (customer.getAccounts().size() == 0) {
            Toast.makeText(this, "No accounts, contact administration!", Toast.LENGTH_SHORT);
        } else {
            for (Account account :
                    customer.getAccounts()) {
                System.out.println(account.getAccountType().toString());
            }
        }

        System.out.println(customerId);
    }

    private void loadAccounts() {
        TableLayout table = findViewById(R.id.accountTable);


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
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(OverviewActivity.this, AccountViewActivity.class);

                    // Passes parcelable account object in intent.putExtra for next intent for use
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("accountObject", account);

                    // Start new AccountViewActivity
                    startActivity(intent);
                }
            });
            table.addView(button);
        }
    }
}
