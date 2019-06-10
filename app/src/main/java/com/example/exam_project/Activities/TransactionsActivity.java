package com.example.exam_project.Activities;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.exam_project.R;
import com.example.exam_project.Transaction;

import java.util.List;


public class TransactionsActivity extends AppCompatActivity {

    Long customerId;
    List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        customerId = getIntent().getLongExtra("customerId", 0);

        transactionList = getTransactionsList(customerId);


        // If customerId is 0, return to loginactivity
        if (customerId == 0) {
            Toast.makeText(this, getString(R.string.transactions_msg01_toast), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        if (transactionList != null) {
            loadTransactions();
        }
    }

    private void loadTransactions() {
        TableLayout table = findViewById(R.id.transactionsTable);


        for (Transaction transaction : getTransactionsList(customerId)) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            table.addView(tableRow);

            TextView transReceiv = new TextView(this);
            transReceiv.setText(transaction.getRecipientId().toString());
            transReceiv.setTextSize(18);
            table.addView(transReceiv);

            Button button = new Button(this);
            button.setText("See details");
            table.addView(button);

        }
    }

    private List<Transaction> getTransactionsList(Long customerId) {

        return null;
    }

    public void onClickOverview(View v) {
        super.onStart();
        startActivity(new Intent(TransactionsActivity.this, OverviewActivity.class).putExtra("customerId", customerId));
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
