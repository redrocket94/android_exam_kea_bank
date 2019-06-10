package com.example.exam_project.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.Dialogs.NewAccDialog;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateInternalAccValue;
import com.example.exam_project.R;

import org.joda.time.LocalDate;

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

        // If customerId is 0, return to loginactivity
        if (customerId == 0) {
            Toast.makeText(this, getString(R.string.overview_msg01_toast), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        // Test if customer is null, if yes then update
        if (customer == null) {
            try {
                CustomerData customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        getBillingBudget();
        getBillingSavings();

        // Set welcome text to users name
        TextView welcomeText = findViewById(R.id.welcomeText);
        if (customer.getFirstName().length() > 6) {
            welcomeText.setText(welcomeText.getText().toString() + "\n" + customer.getFirstName() + "!");
        } else {
            welcomeText.setText(welcomeText.getText().toString() + customer.getFirstName() + "!");
        }

        // Load all customers accounts for activity
        loadAccounts();

        if (customer.getAccounts().size() == 0) {
            Toast.makeText(this, getString(R.string.overview_msg02_toast), Toast.LENGTH_SHORT);
        }

        // Pay any automatic bills
        payBillsAuto();

    }

    public void createNewAcc(View v) {
        Bundle args = new Bundle();
        args.putLong("customerId", customerId);
        args.putParcelable("customerObject", customer);
        DialogFragment newAccDialog = new NewAccDialog();
        newAccDialog.setArguments(args);
        newAccDialog.show(getSupportFragmentManager(), "new_acc_dialog");

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
            button.setText(getString(R.string.bills_btntxt_btn));

            // Test if you have approval for accounts, if not then disable and set button text
            if (!account.isApproved()) {
                button.setClickable(false);
                button.setText(getString(R.string.overview_newaccinfo_btn));
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

    private void payBillsAuto() {

        List<Bill> billList = customer.getBills();
        Account defaultAccount = getUserDefaultAcc(customer);

        for (Bill bill : billList) {
            if (bill.getLocalDate().equals(new LocalDate()) && bill.isAutoPay()) {
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
            new HRT_SetExtAccValByEmail(defaultAccount, bill.getBillCollectorEmail(), bill.getValue(), customer, HRT_SetExtAccValByEmail.SendType.BILL, bill.getId()).execute();
            Toast.makeText(this, getString(R.string.overview_msg03partial01_toast) + " " + bill.getValue() + " " +
                    getString(R.string.overview_msg03partial02_toast) + " " + bill.getBillCollectorEmail(), Toast.LENGTH_SHORT).show();
        } else if (bill.getValue() > defaultAccount.getAmount()) {
            Toast.makeText(this, getString(R.string.overview_msg04_toast), Toast.LENGTH_SHORT).show();
            return;
        }
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

    void getBillingBudget() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", 0);

        String budgetBill = sharedPreferences.getString("monthlybill_BUDGET_" + customerId, null);
        if (budgetBill != null) {
            String dataDate = budgetBill.substring(0, budgetBill.indexOf(" "));
            double amountToDeposit = Double.parseDouble(budgetBill.substring(budgetBill.indexOf(" ") + 1));
            String currDate = new LocalDate().getMonthOfYear() + "";
            if (currDate.equals(dataDate)) {
                if (getUserDefaultAcc(customer).getAmount() >= amountToDeposit) {
                    new HRT_UpdateInternalAccValue(customerId, Account.AccountType.DEFAULT, Account.AccountType.BUDGET, amountToDeposit).execute();
                    Toast.makeText(this, getString(R.string.monthpaydia_msg02_toast), Toast.LENGTH_SHORT).show();
                    setBillingMonth(dataDate, "BUDGET", amountToDeposit);
                } else {
                    Toast.makeText(this, "Tried to deposit to Billing Account, using your payment billing plan, but you have too little funds! Canceling plan.", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().remove("monthlybill_BUDGET_" + customerId).apply();
                }
            }
        }
    }

    void getBillingSavings() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", 0);

        String savingsBill = sharedPreferences.getString("monthlybill_SAVINGS_" + customerId, null);
        if (savingsBill != null) {
            String dataDate = savingsBill.substring(0, savingsBill.indexOf(" "));
            double amountToDeposit = Double.parseDouble(savingsBill.substring(savingsBill.indexOf(" ") + 1));
            String currDate = new LocalDate().getMonthOfYear() + "";
            if (currDate.equals(dataDate)) {
                if (getUserDefaultAcc(customer).getAmount() >= amountToDeposit) {
                    new HRT_UpdateInternalAccValue(customerId, Account.AccountType.DEFAULT, Account.AccountType.SAVINGS, amountToDeposit).execute();
                    Toast.makeText(this, getString(R.string.monthpaydia_msg02_toast), Toast.LENGTH_SHORT).show();
                    setBillingMonth(dataDate, "SAVINGS", amountToDeposit);
                } else {
                    Toast.makeText(this, "Tried to deposit to Savings Account, using your payment billing plan, but you have too little funds! Canceling plan.", Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().remove("monthlybill_SAVINGS_" + customerId).apply();

                }
            }
        }
    }

    void setBillingMonth(String loadedDate, String AccountTypeStr, double amount) {
        int myDate = Integer.parseInt(loadedDate);
        if (myDate == 12) {
            myDate = 1;
        } else {
            myDate++;
        }

        SharedPreferences.Editor editor = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();

        editor.putString("monthlybill_" + AccountTypeStr + "_" + customerId,
                myDate + " " + amount);
        editor.apply();
    }
}
