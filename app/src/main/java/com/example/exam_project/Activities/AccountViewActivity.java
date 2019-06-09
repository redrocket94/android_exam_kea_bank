package com.example.exam_project.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.HttpRequestTasks.DataCustomerParser;
import com.example.exam_project.HttpRequestTasks.HRT_GetUserById;
import com.example.exam_project.HttpRequestTasks.HRT_SetExtAccValByEmail;
import com.example.exam_project.HttpRequestTasks.HRT_UpdateInternalAccValue;
import com.example.exam_project.MailHandler.SendMail;
import com.example.exam_project.Modules.InfoSpinner;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AccountViewActivity extends AppCompatActivity {

    Account account;
    Customer customer;
    Long customerId;
    Button withdraw_btn;
    Button deposit_btn;
    Button deposit_external_btn;
    EditText amountToWithdraw_input;
    EditText email_input;
    Spinner accTypeSpinner;

    double amountToWithdraw;
    int generatedValue;
    double amountToDeposit;

    CustomerData customerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        account = getIntent().getParcelableExtra("accountObject");
        customerId = getIntent().getLongExtra("customerId", 0);

        if (customer == null) {
            try {
                customerData = new HRT_GetUserById(customerId).execute().get();
                customer = new DataCustomerParser().dataToCustomer(customerData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Connect Spinner in View to its functionality
        new InfoSpinner(this, getApplicationContext(), customerId).connectSpinner();

        withdraw_btn = findViewById(R.id.withdraw_btn);
        deposit_btn = findViewById(R.id.deposit_btn);
        deposit_external_btn = findViewById(R.id.deposit_external_btn);

        // If account type is NOT Pension, hide Withdraw button OR if account is default (cant withdraw from default account to nothing)
        if ((account.getAccountType() == Account.AccountType.PENSION && customer.getAge() < 77)) {
            withdraw_btn.setVisibility(View.INVISIBLE);
            deposit_btn.setVisibility(View.INVISIBLE);
            deposit_external_btn.setVisibility(View.INVISIBLE);
        } else if (account.getAccountType() == Account.AccountType.DEFAULT) {
            withdraw_btn.setVisibility(View.INVISIBLE);
        }

        // Change header text to show type of account
        TextView account_type = findViewById(R.id.account_type);
        account_type.setText(account.getAccountType().toString() + " " + account_type.getText());


        EditText account_amount = findViewById(R.id.account_amount);
        // Formatting double to avoid trailing 0's
        double held_amount = Double.parseDouble(new DecimalFormat("0.#####").format(account.getAmount()));
        account_amount.setText(account_amount.getText() + " " + held_amount);

        deposit_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.deposit_dialog, null);
                builder.setTitle(getString(R.string.intdepositdia_titlepartial01_txt) + account.getAccountType().toString() + getString(R.string.intdepositdia_titlepartial02_txt));

                accTypeSpinner = mView.findViewById(R.id.internal_accs_spinner);
                final EditText amountToDeposit_input = mView.findViewById(R.id.amount_to_deposit);


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AccountViewActivity.this, android.R.layout.simple_spinner_item, getUserAccounts());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accTypeSpinner.setAdapter(adapter);

                builder.setPositiveButton(getString(R.string.intdepositdia_positive_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        amountToDeposit = Double.parseDouble(amountToDeposit_input.getText().toString());
                        
                        // Make sure there's enough money to transfer by getting current amount of money on account
                        if (amountToDeposit > account.getAmount()) {
                            Toast.makeText(AccountViewActivity.this, getString(R.string.intdepositdia_msg01_toast), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (amountToDeposit < 0.5) {
                            Toast.makeText(AccountViewActivity.this, getString(R.string.intdepositdia_msg02_toast), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // If PENSION type, get NemID verification
                        if (accTypeSpinner.getSelectedItemId() == Arrays.asList(getResources().getStringArray(R.array.accountTypes)).indexOf("PENSION")) {
                            generatedValue = new NemID().getRandomValue();
                            SendMail sendMail = new SendMail(AccountViewActivity.this, SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                            sendMail.execute();
                            NemIDDialog("int");
                        } else {
                            new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.valueOf(accTypeSpinner.getSelectedItem().toString()), amountToDeposit).execute();
                            Toast.makeText(AccountViewActivity.this, getString(R.string.intdepositdia_msg03_toast), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AccountViewActivity.this, OverviewActivity.class).putExtra("customerId", customerId));
                        }


                    }
                });
                builder.setNegativeButton(getString(R.string.intdepositdia_negative_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setView(mView);
                builder.show();
            }

        });
    }

    public void onClickBack(View v) {
        super.onStart();
        startActivity(new Intent(this, OverviewActivity.class).putExtra("customerId", customerId));
    }

    // Returns a list of the users accounts that are approved and NOT the current account
    List<String> getUserAccounts() {
        List<String> userAccountsList = new ArrayList<>();
        for (Account userAccount :
                customer.getAccounts()) {
            if (userAccount.getAccountType() != account.getAccountType() && userAccount.isApproved()) {
                userAccountsList.add(userAccount.getAccountType().toString());
            }
        }
        return userAccountsList;
    }

    public void onClickWithdraw (View v) {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.withdraw_dialog, null);
        builder.setTitle(getString(R.string.withdrawdialog_titlepartial01_txt) + account.getAccountType().toString());

        final EditText amountToWithdraw_input = mView.findViewById(R.id.amount_to_withdraw);

        builder.setPositiveButton(getString(R.string.withdrawdialog_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double amountToWithdraw = Double.parseDouble(amountToWithdraw_input.getText().toString());

                // Make sure there's enough money to transfer by getting current amount of money on account
                if (amountToWithdraw > account.getAmount()) {
                    Toast.makeText(AccountViewActivity.this, getString(R.string.withdrawdialog_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.DEFAULT, amountToWithdraw).execute();
                startActivity(new Intent(AccountViewActivity.this, OverviewActivity.class).putExtra("customerId", customerId));

            }
        });
        builder.setNegativeButton(getString(R.string.withdrawdialog_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(mView);
        builder.show();
    }

    public void onClickUserDeposit(View v) {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.user_deposit_dialog, null);
        builder.setTitle(getString(R.string.extdepositdia_titlepartial01_txt) + account.getAccountType().toString());

        final EditText amountToWithdraw_input = mView.findViewById(R.id.amount_to_withdraw);
        final EditText email_input = mView.findViewById(R.id.email_input);

        builder.setPositiveButton(getString(R.string.extdepositdia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                amountToWithdraw = Double.parseDouble(amountToWithdraw_input.getText().toString());
                String email = email_input.getText().toString();

                // Make sure there's enough money to transfer by getting current amount of money on account
                if (amountToWithdraw > account.getAmount()) {
                    Toast.makeText(AccountViewActivity.this, getString(R.string.extdepositdia_msg01_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                generatedValue = new NemID().getRandomValue();
                SendMail sendMail = new SendMail(AccountViewActivity.this, SendMail.MailType.TRANSACTION_CONFIRMATION, customer.getEmail(), generatedValue);
                sendMail.execute();
                NemIDDialog("ext");

            }
        });
        builder.setNegativeButton(getString(R.string.extdepositdia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(mView);
        builder.show();
    }

    void NemIDDialog(final String intOrExt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViewActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.nemid_verify_dialog, null);
        builder.setTitle(getString(R.string.nemiddia_title_txt));

        final EditText nemIdNumber_input = mView.findViewById(R.id.nemid_field);

        builder.setPositiveButton(getString(R.string.nemiddia_positive_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int nemIdNumber = Integer.parseInt(nemIdNumber_input.getText().toString());
                String email = customer.getEmail();

                if (nemIdNumber == generatedValue) {
                    if (intOrExt.equals("ext")) {
                        new HRT_SetExtAccValByEmail(account, email, amountToWithdraw, customer).execute();
                        startActivity(new Intent(AccountViewActivity.this, OverviewActivity.class).putExtra("customerId", customerId));
                    } else if (intOrExt.equals("int")) {
                        new HRT_UpdateInternalAccValue(customerId, account.getAccountType(), Account.AccountType.valueOf(accTypeSpinner.getSelectedItem().toString()), amountToDeposit).execute();
                        startActivity(new Intent(AccountViewActivity.this, OverviewActivity.class).putExtra("customerId", customerId));
                    }
                    Toast.makeText(AccountViewActivity.this, getString(R.string.nemiddia_msg01_toast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountViewActivity.this, getString(R.string.nemiddia_msg02_toast), Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.nemiddia_negative_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setView(mView);
        builder.show();
    }
}
