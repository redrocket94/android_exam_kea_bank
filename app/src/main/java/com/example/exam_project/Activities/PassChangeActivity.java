package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateUserByEmail;
import com.example.exam_project.R;

public class PassChangeActivity extends AppCompatActivity {
    EditText curr_pass_input;
    EditText new_pass_input;
    EditText new_pass_verify_input;

    String curr_pass, new_pass, new_pass_verify;

    Button cancel_btn;
    Button submit_btn;

    Customer customer;
    Long customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_change);

        submit_btn = findViewById(R.id.submit_btn);
        cancel_btn = findViewById(R.id.cancel_btn);

        curr_pass_input = findViewById(R.id.curr_pass);
        new_pass_input = findViewById(R.id.new_pass);
        new_pass_verify_input = findViewById(R.id.new_pass_verify);

        customerId = getIntent().getLongExtra("customerId", 0);


        if (customer == null) {
            try {
                CustomerData customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    void onClickSubmit(View v) {
        String passToSend = passwordVerifier();
        if (passToSend != null) {
            new HRT_UpdateUserByEmail(customer.getEmail(), passToSend).execute();
            Toast.makeText(this, getString(R.string.passch_msg01_toast), Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
        }
    }

    void onClickCancel(View v) {
        startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
    }

    private String passwordVerifier() {

        // Get strings from inputs
        curr_pass = curr_pass_input.getText().toString();
        new_pass = new_pass_input.getText().toString();
        new_pass_verify = new_pass_verify_input.getText().toString();


        // Check if inputs are valid
        if (!curr_pass.equals(customer.getPassword())) {
            Toast.makeText(this, getString(R.string.passch_msg02_toast), Toast.LENGTH_SHORT).show();
            return null;
        } else if (!new_pass.equals(new_pass_verify)) {
            Toast.makeText(this, getString(R.string.passch_msg03_toast), Toast.LENGTH_SHORT).show();
            return null;
        } else if (new_pass.length() < 8) {
            Toast.makeText(this, getString(R.string.passch_msg04_toast), Toast.LENGTH_SHORT).show();
            return null;
        }

        return new_pass;
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
