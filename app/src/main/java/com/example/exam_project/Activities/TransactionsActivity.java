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

import com.example.exam_project.Modules.InfoSpinner;
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

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, TransactionsActivity.this, customerId).connectSpinner();

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
}
