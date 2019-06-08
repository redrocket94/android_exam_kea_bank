package com.example.exam_project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateBill;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateUserAccountsById;
import com.example.exam_project.Modules.InfoSpinner;
import com.example.exam_project.R;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class OverviewActivity extends AppCompatActivity {

    Customer customer;

    Long customerId;
    Button newAcc_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        customerId = getIntent().getLongExtra("customerId", 0);

        newAcc_btn = findViewById(R.id.new_acc_btn);

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, OverviewActivity.this, customerId).connectSpinner();

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

        // Set welcome text with users name
        TextView welcomeText = findViewById(R.id.welcomeText);
        if (customer.getFirstName().length() > 6) {
            welcomeText.setText("Welcome\n" + customer.getFirstName() + "!");
        } else {
            welcomeText.setText("Welcome " + customer.getFirstName() + "!");
        }

        // Load all customers accounts for activity
        loadAccounts();

        if (customer.getAccounts().size() == 0) {
            Toast.makeText(this, "No accounts, contact administration!", Toast.LENGTH_SHORT);
        }

        // Pay any automatic bills
        payBillsAuto();

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

            // Test if you have approval for accounts, if not then disable and set button text
            if (!account.isApproved()) {
                button.setClickable(false);
                button.setText("Approval Pending...");
            } else {
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
            }
            table.addView(button);

        }
    }

    List<String> addNewAccounts(Customer currCustomer) {
        List<String> list = new ArrayList<>();
        list.add("DEFAULT");
        list.add("BUSINESS");
        list.add("BUDGET");
        list.add("PENSION");
        list.add("SAVINGS");

        for (Account account : currCustomer.getAccounts()) {
            if (account.getAccountType() == Account.AccountType.DEFAULT) {
                list.remove("DEFAULT");
            } else if (account.getAccountType() == Account.AccountType.BUSINESS) {
                list.remove("BUSINESS");
            } else if (account.getAccountType() == Account.AccountType.BUDGET) {
                list.remove("BUDGET");
            } else if (account.getAccountType() == Account.AccountType.PENSION) {
                list.remove("PENSION");
            } else if (account.getAccountType() == Account.AccountType.SAVINGS) {
                list.remove("SAVINGS");
            }

        }
        return list;
    }

    public void createNewAcc(View v) {
        super.onStart();
        // Checks if user already has all accounts possible and returns, before a dialog can be built
        List<String> list = addNewAccounts(customer);
        if (list.size() == 0) {
            Toast.makeText(OverviewActivity.this, "Sorry, you already have all possible accounts!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(OverviewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.new_acc_dialog, null);
        builder.setTitle("Opening a new account...");
        final Spinner accTypeSpinner = mView.findViewById(R.id.new_acc_spinner);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OverviewActivity.this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accTypeSpinner.setAdapter(adapter);

        builder.setPositiveButton("Open account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = accTypeSpinner.getSelectedItem().toString();

                new HRT_UpdateUserAccountsById(customerId, selection).execute();

                // Refreshing page to display new account
                finish();
                startActivity(getIntent());
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

    private void payBillsAuto() {

        List<Bill> billList = customer.getBills();
        Account defaultAccount = getUserDefaultAcc(customer);

        for (Bill bill : billList) {
            if (bill.getLocalDate().equals(new LocalDate())) {
                payBill(bill, defaultAccount);
            }
        }

    }

    Account getUserDefaultAcc(Customer currCustomer) {
        for (Account account : currCustomer.getAccounts()) {
            if (account.getAccountType() == Account.AccountType.DEFAULT) {
                return account;
            }
        }
        return null;
    }

    void payBill(Bill bill, Account defaultAccount) {

        if (bill.isAutoPay() && !bill.isPaid() && bill.getValue() <= defaultAccount.getAmount()) {
            new HRT_UpdateBill(bill.getId(), customerId, true).execute();
            new HRT_SetExtAccValByEmail(defaultAccount, bill.getBillCollectorEmail(), bill.getValue(), customer).execute();
            Toast.makeText(this, "Made an AUTOMATIC payment to user of email: " + bill.getBillCollectorEmail(), Toast.LENGTH_SHORT).show();
        } else if (bill.getValue() <= defaultAccount.getAmount()) {
            Toast.makeText(this, "Your funds in the DEFAULT account are too low!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
