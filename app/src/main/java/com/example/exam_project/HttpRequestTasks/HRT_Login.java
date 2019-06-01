package com.example.exam_project.HttpRequestTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Activities.OverviewActivity;
import com.example.exam_project.Customer;
import com.example.exam_project.Data;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class HRT_Login extends AsyncTask<Void, Void, Data> {

    EditText username;
    EditText password;
    Context context;
    CheckBox remember_box;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Customer customer;

    public HRT_Login(EditText username, EditText password, Context context, CheckBox remember_box, SharedPreferences sharedPreferences, SharedPreferences.Editor editor, Customer customer) {
        this.username = username;
        this.password = password;
        this.context = context;
        this.remember_box = remember_box;
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
        this.customer = customer;
    }

    @Override
    protected Data doInBackground(Void... params) {
        try {
            String url = "http://10.0.2.2:8080/customers/search/findCustomerByUsernameAndPassword?username=" +
                    username.getText().toString() +
                    "&password=" +
                    password.getText().toString();

            // Check for response code, returns null if 404 (not found)
            URL test_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)test_url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 404) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Data data = restTemplate.getForObject(url, Data.class);
            return data;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Data data) {

        // Check data username and data password are not null
        if (data == null) {
            Toast.makeText(context, "Make sure both username and password are filled out and try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Making variables to contain username and password input by user
        String username_input = username.getText().toString();
        String password_input = password.getText().toString();

        // Checks to see whether username and password in the returned data matches with the data input by user
        try {
            if (data.getUsername().equals(username_input) && data.getPassword().equals(password_input)) {

                //// ON SUCCESS:

                // Checks if "Remember me" box is  checked and if it is, store username in sharedpreferences for next time
                if (remember_box.isChecked()) {

                    // If sharedpreferences doesn't already contain the username in input, then store it, else do nothing
                    if (sharedPreferences.getString("stored_username", null) == null || !sharedPreferences.getString("stored_username", null).equals(username_input)) {
                        editor.putString("stored_username", username.getText().toString());
                        editor.commit();
                    }
                } else {
                    editor.remove("stored_username");
                    editor.commit();
                }

                Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT).show();

                // Sets intent to new intent from this activity to OverviewActivity through context of LoginActivity
                Intent intent = new Intent(context, OverviewActivity.class);

                // Passes parcelable customer object (with accounts) in intent.putExtra to next intent for use
                customer = new Customer(data.getUsername(), data.getPassword(), data.getEmail(), data.getFirstName(), data.getLastName(), data.getAge(), data.getAccounts());
                intent.putExtra("customerObject", customer);

                // Have to pass customerId in own putExtra due to bug
                intent.putExtra("customerId", data.getCustomerId());

                // Start new LoginActivity
                context.startActivity(intent);

            } else {
                // Inform user that username and/or password combination is incorrect
                Toast.makeText(context, "Invalid username and/or password!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
    }
}