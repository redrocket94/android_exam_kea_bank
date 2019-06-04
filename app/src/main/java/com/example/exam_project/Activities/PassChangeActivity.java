package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
            Toast.makeText(this, "Successfully changed password!", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, "Your password input does NOT match your current password! Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        } else if (!new_pass.equals(new_pass_verify)) {
            Toast.makeText(this, "Your new password does NOT match your the new password confirmation! Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        } else if (new_pass.length() < 8) {
            Toast.makeText(this, "Your password is too short! Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new_pass;
    }
}
