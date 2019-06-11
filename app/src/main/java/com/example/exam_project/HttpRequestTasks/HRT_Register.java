package com.example.exam_project.HttpRequestTasks;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.Activities.MainActivity;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;
import com.example.exam_project.R;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class HRT_Register extends AsyncTask<Void, Void, CustomerData> {

    EditText firstName;
    EditText lastName;
    EditText age;

    EditText username;
    EditText password;
    EditText email;

    Location userLocation;

    int code;
    Context context;

    CustomerData emailCustomerData;

    public HRT_Register(EditText firstName, EditText lastName, EditText age, EditText username, EditText password, EditText email, Location userlocation, Context context) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userLocation = userlocation;
        this.context = context;
    }

    @Override
    protected CustomerData doInBackground(Void... params) {
        try {

            // Get data by using username and password to test if already taken
            CustomerData loginCustomerData = data_userPass();
            // Get data by using email to test if already taken
            emailCustomerData = data_email();
            // Test if both are null, if yes then register new account with the information given
            if (loginCustomerData == null && emailCustomerData == null) {
                // Send POST request to API
                requestPOST();
                return null;
            } else {
                return loginCustomerData;
            }

        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(CustomerData loginCustomerData) {

        // Check data username and data password are not null
        if (loginCustomerData != null) {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.hrt_msg03_toast), Toast.LENGTH_SHORT).show();
            return;
        } else if (emailCustomerData != null) {
            Toast.makeText(context.getApplicationContext(), "Email already exists! Try again.", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(context.getApplicationContext(), context.getString(R.string.hrt_msg04_toast), Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, MainActivity.class));

    }

    private CustomerData data_userPass() {
        try {
            String url_userpass = "http://10.0.2.2:8080/customers/search/findCustomerByUsername?username=" +
                    username.getText().toString();

            // Check for response code, returns null if 200 (found)
            URL test_url = new URL(url_userpass);
            HttpURLConnection connection = (HttpURLConnection) test_url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            code = connection.getResponseCode();

            if (code == 200) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                CustomerData customerData = restTemplate.getForObject(url_userpass, CustomerData.class);
                return customerData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private CustomerData data_email() {
        try {
            String url_userpass = "http://10.0.2.2:8080/customers/search/findCustomerByEmail?email=" +
                    email.getText().toString();

            // Check for response code, returns null if 200 (found)
            URL test_url = new URL(url_userpass);
            HttpURLConnection connection = (HttpURLConnection) test_url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            code = connection.getResponseCode();

            if (code == 200) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                CustomerData customerData = restTemplate.getForObject(url_userpass, CustomerData.class);
                return customerData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void requestPOST() {
        try {
            // Making variables to contain username and password input by user
            String username_input = username.getText().toString();
            String password_input = password.getText().toString();
            String email_input = email.getText().toString();
            String firstName_input = firstName.getText().toString();
            String lastName_input = lastName.getText().toString();
            int age_input = Integer.parseInt(age.getText().toString());

            Location copenhagen = new Location("");
            copenhagen.setLatitude(55.676098);
            copenhagen.setLongitude(12.568337);

            Location odense = new Location("");
            odense.setLatitude(55.39594);
            odense.setLongitude(10.38831);

            // Send post request to API
            String url = ("http://10.0.2.2:8080/customers/");

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            Customer customer = new Customer(firstName_input, lastName_input, age_input, username_input, password_input, email_input, null, null);

            if (userLocation.distanceTo(copenhagen) < userLocation.distanceTo(odense)) {
                customer.setBank(Customer.Bank.COPENHAGEN);
            } else {
                customer.setBank(Customer.Bank.ODENSE);
            }

            restTemplate.postForObject(url, customer, Customer.class);

        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
    }


}