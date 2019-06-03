package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.Data;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HRT_UpdateUserAccountsById extends AsyncTask<Void, Void, Data> {


    Long id;
    String selection;

    public HRT_UpdateUserAccountsById(Long id, String selection) {
        this.id = id;
        this.selection = selection;
    }

    @Override
    protected Data doInBackground(Void... params) {
        Data userData = getUserData();

        if (userData == null) {
            return null;
        }
        putUserDataAccounts(userData);
        return null;
    }

    Data getUserData() {
        try {
            String url = "http://10.0.2.2:8080/customers/" + id;
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
            Data data = restTemplate.getForObject(url, Data.class);
            return data;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return null;
    }

    void putUserDataAccounts(Data userData) {
        Map<String, String> putParams = new HashMap<String, String>();
        putParams.put("accounts", selection);

        userData.addAccount(new Account(userData.getCustomerId(), Account.AccountType.valueOf(selection), 0.0));

        Customer updatedCustomer = new Customer(userData.getCustomerId(), userData.getFirstName(), userData.getLastName(), userData.getAge(), userData.getUsername(),
                userData.getPassword(), userData.getEmail(), Customer.Bank.valueOf(userData.getBank()), userData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + userData.getCustomerId(), updatedCustomer, putParams);
    }
}