package com.example.exam_project.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exam_project.Customer;
import com.example.exam_project.Dialogs.ForgotPassDialog;
import com.example.exam_project.HttpRequestTasks.HRT_Login;
import com.example.exam_project.R;

public class MainActivity extends AppCompatActivity {

    Customer customer;
    Button login_btn;
    EditText username;
    EditText password;
    CheckBox remember_box;
    TextView forgot_pass;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hiding actionbar for loginscreen
        getSupportActionBar().hide();

        login_btn = findViewById(R.id.login_btn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        remember_box = findViewById(R.id.remember_box);

        // Set OnClickListener to forgot_pass TextView
        forgot_pass = findViewById(R.id.forgot_pass);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ForgotPassDialog forgotPassDialog = new ForgotPassDialog();
                forgotPassDialog.show(getSupportFragmentManager(), "forgot_pass_dialog");
            }
        });

        // Initialization to get sharedpreferences, "0" for private mode
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", 0);
        editor = sharedPreferences.edit();

        // Attempts to get saved username from sharedpreferences
        username.setText(sharedPreferences.getString("stored_username", null));

        // Set remember me checkbox to true if username field has text before user inputs himself (must have been got from sharedpreferences)
        if (username.getText().toString().equals("")) {
            remember_box.setChecked(false);
        } else {
            remember_box.setChecked(true);
        }

    }

    public void onLoginClick(View v) {
        super.onStart();
        new HRT_Login(username, password, MainActivity.this, remember_box, sharedPreferences, editor, customer).execute();
    }


    public void onRegisterClick(View v) {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }


}
