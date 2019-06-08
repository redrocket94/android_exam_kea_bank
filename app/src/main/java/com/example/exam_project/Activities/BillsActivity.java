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

import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.R;

public class BillsActivity extends AppCompatActivity {

    Customer customer;
    Long customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);

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

        // If customerId is 0, return to loginactivity
        if (customerId == 0) {
            Toast.makeText(this, "There was an error retrieving your account customerData, please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        // Make sure there are Bills to get
        if (customer.getBills() != null) {
            loadBills();
        }

    }

    private void loadBills() {
        TableLayout table = findViewById(R.id.billsTable);


        for (final Bill bill : customer.getBills()) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            table.addView(tableRow);

            TextView accountType = new TextView(this);
            accountType.setText(bill.getBillCollectorEmail());
            accountType.setTextSize(18);
            table.addView(accountType);

            Button button = new Button(this);
            button.setText("See details");

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(BillsActivity.this, BillViewActivity.class);

                    // Passes parcelable account object in intent.putExtra for next intent for use
                    intent.putExtra("customerId", customerId);
                    intent.putExtra("billObject", bill);

                    // Start new AccountViewActivity
                    startActivity(intent);
                }
            });

            table.addView(button);

        }
    }


    public void onClickOverview(View v) {
        super.onStart();
        startActivity(new Intent(BillsActivity.this, OverviewActivity.class).putExtra("customerId", customerId));
    }
}
