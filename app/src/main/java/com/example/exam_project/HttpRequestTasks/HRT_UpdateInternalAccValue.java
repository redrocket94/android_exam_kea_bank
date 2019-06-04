package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.exam_project.Account;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class HRT_UpdateInternalAccValue extends AsyncTask<Void, Void, CustomerData> {


    Long id;
    Account.AccountType accToSend;
    Account.AccountType accToReceive;
    Double value;

    public HRT_UpdateInternalAccValue(Long id, Account.AccountType accToSend, Account.AccountType accToReceive, Double value) {
        this.id = id;
        this.accToSend = accToSend;
        this.accToReceive = accToReceive;
        this.value = value;
    }

    @Override
    protected CustomerData doInBackground(Void... params) {
        CustomerData userCustomerData = getUserData();

        if (userCustomerData == null) {
            return null;
        }
        putAccountValue(userCustomerData);
        return null;
    }

    CustomerData getUserData() {
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
            CustomerData customerData = restTemplate.getForObject(url, CustomerData.class);
            return customerData;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return null;
    }

    void putAccountValue(CustomerData userCustomerData) {

        for (Account account : userCustomerData.getAccounts()) {
            if (accToReceive == account.getAccountType()) {
                account.setAmount( account.getAmount() + value);
            }
            if (accToSend == account.getAccountType()) {
                account.setAmount( account.getAmount() - value);
            }
        }

        Customer updatedCustomer = new Customer(userCustomerData.getCustomerId(), userCustomerData.getFirstName(), userCustomerData.getLastName(), userCustomerData.getAge(), userCustomerData.getUsername(),
                userCustomerData.getPassword(), userCustomerData.getEmail(), Customer.Bank.valueOf(userCustomerData.getBank()), userCustomerData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + userCustomerData.getCustomerId(), updatedCustomer);
    }
}