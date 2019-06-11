package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.Dialogs.PayBillDialog;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
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
        Bundle args = new Bundle();
        args.putParcelable("customerObject", customer);
        args.putLong("customerId", customerId);
        args.putParcelable("billObject", bill);
        DialogFragment payBillDialog = new PayBillDialog();
        payBillDialog.setArguments(args);
        payBillDialog.show(getSupportFragmentManager(), "pay_bill_dialog");
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
