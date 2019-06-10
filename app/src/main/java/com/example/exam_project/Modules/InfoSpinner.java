package com.example.exam_project.Modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.exam_project.Activities.BillsActivity;
import com.example.exam_project.Activities.MainActivity;
import com.example.exam_project.Activities.PassChangeActivity;
import com.example.exam_project.Activities.TransactionsActivity;
import com.example.exam_project.R;

public class InfoSpinner extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Activity activity;
    Context context;
    Long customerId;

    String[] list;
    Spinner infoSpinner;
    ArrayAdapter<String> adapter;

    public InfoSpinner(Activity activity, Context context, Long customerId) {
        this.activity = activity;
        this.context = context;
        this.customerId = customerId;

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        infoSpinner.setAdapter(adapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            // Transactions
            case 0:
                Intent transactionsActivity = new Intent(activity, TransactionsActivity.class);
                transactionsActivity.putExtra("customerId", customerId);
                activity.startActivity(transactionsActivity);
                break;
            // Bills
            case 2:
                Intent billsActivity = new Intent(activity, BillsActivity.class);
                billsActivity.putExtra("customerId", customerId);
                activity.startActivity(billsActivity);
                break;
            // Change Password
            case 3:
                Intent passChangeActivity = new Intent(activity, PassChangeActivity.class);
                passChangeActivity.putExtra("customerId", customerId);
                activity.startActivity(passChangeActivity);
                break;
            // Log out
            case 4:
                Toast.makeText(activity, activity.getString(R.string.infospinner_msg01_toast), Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, MainActivity.class));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
