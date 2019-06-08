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

import java.util.ArrayList;
import java.util.List;

public class InfoSpinner extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Activity activity;
    Context context;
    Long customerId;

    public InfoSpinner(Activity activity, Context context, Long customerId) {
        this.activity = activity;
        this.context = context;
        this.customerId = customerId;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = parent.getItemAtPosition(position).toString();

        switch (selection) {
            case "Transactions":
                Intent transactionsActivity = new Intent(activity, TransactionsActivity.class);
                transactionsActivity.putExtra("customerId", customerId);
                activity.startActivity(transactionsActivity);
                break;
            case "Bills":
                Intent billsActivity = new Intent(activity, BillsActivity.class);
                billsActivity.putExtra("customerId", customerId);
                activity.startActivity(billsActivity);
                break;
            case "Change Password":
                Intent passChangeActivity = new Intent(activity, PassChangeActivity.class);
                passChangeActivity.putExtra("customerId", customerId);
                activity.startActivity(passChangeActivity);
                break;

            case "Log Out":
                Toast.makeText(activity, "Successfully logged out!", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, MainActivity.class));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void connectSpinner() {
        // Creating spinner
        Spinner info_spinner = activity.findViewById(R.id.info_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Transactions");
        list.add("Bills");
        list.add("Change Password");
        list.add("Log Out");
        list.add("[More]");
        final int listsize = list.size() - 1;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                return (listsize); // Truncate the list
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        info_spinner.setAdapter(adapter);
        info_spinner.setOnItemSelectedListener(this);
        info_spinner.setSelection(listsize);
    }
}
