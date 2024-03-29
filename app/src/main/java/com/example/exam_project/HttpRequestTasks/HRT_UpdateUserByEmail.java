package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HRT_UpdateUserByEmail extends AsyncTask<Void, Void, CustomerData> {


    String email;
    int numericPass;

    String passStr;


    public HRT_UpdateUserByEmail(String email, int numericPass) {
        this.email = email;
        this.numericPass = numericPass;
    }

    public HRT_UpdateUserByEmail(String email, String passStr) {
        this.email = email;
        this.passStr = passStr;
    }

    @Override
    protected CustomerData doInBackground(Void... params) {

        // Test if numeric pass is greater than 0, which means constructor for numericPass has been used
        if (numericPass > 0) {
            // Get userdata from email identifier
            CustomerData userCustomerData = getUserData();

            if (userCustomerData != null) {
                putUserDataNumeric(userCustomerData);
            }

        } else if (passStr != null) {
            CustomerData userCustomerData = getUserData();

            if (userCustomerData != null) {
                putUserDataStr(userCustomerData);
            }

        }
        return null;
    }

    CustomerData getUserData() {
        try {
            String url = "http://10.0.2.2:8080/customers/search/findCustomerByEmail?email=" + email;
            // Check for response code, returns null if 404 (not found)
            URL test_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) test_url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 404) {
                System.out.println("404, could not find: " + test_url.toString());
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            CustomerData customerData = restTemplate.getForObject(url, CustomerData.class);
            return customerData;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return null;
    }

    void putUserDataNumeric(CustomerData userCustomerData) {
        String passStr = Integer.toString(numericPass);
        Map<String, String> putParams = new HashMap<String, String>();
        putParams.put("password", passStr);

        Customer updatedCustomer = new Customer(userCustomerData.getCustomerId(), userCustomerData.getFirstName(), userCustomerData.getLastName(), userCustomerData.getAge(), userCustomerData.getUsername(),
                passStr, userCustomerData.getEmail(), Customer.Bank.valueOf(userCustomerData.getBank()), userCustomerData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + userCustomerData.getCustomerId(), updatedCustomer, putParams);
    }

    void putUserDataStr(CustomerData userCustomerData) {
        Map<String, String> putParams = new HashMap<String, String>();
        putParams.put("password", passStr);

        Customer updatedCustomer = new Customer(userCustomerData.getCustomerId(), userCustomerData.getFirstName(), userCustomerData.getLastName(), userCustomerData.getAge(), userCustomerData.getUsername(),
                passStr, userCustomerData.getEmail(), Customer.Bank.valueOf(userCustomerData.getBank()), userCustomerData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + userCustomerData.getCustomerId(), updatedCustomer, putParams);
    }

}