
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

public class HRT_SetExtAccValByEmail extends AsyncTask<Void, Void, Data> {


    Account sendingAccount;
    String emailOfReceiver;
    double value;
    Customer customer;

    public HRT_SetExtAccValByEmail(Account sendingAccount, String emailOfReceiver, double value, Customer customer) {
        this.sendingAccount = sendingAccount;
        this.emailOfReceiver = emailOfReceiver;
        this.value = value;
        this.customer = customer;
    }

    @Override
    protected Data doInBackground(Void... params) {
        Data userData = getUserData();

        if (userData == null) {
            return null;
        }

        putAccountValue(userData);
        return null;
    }

    Data getUserData() {
        try {
            String url = "http://10.0.2.2:8080/customers/search/findCustomerByEmail?email=" + emailOfReceiver;
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

    void putAccountValue(Data userData) {
        putReceiverAccountValue(userData);
        putSenderAccountValue(customer);
    }

    private void putReceiverAccountValue(Data receiverData) {
        for (Account account : receiverData.getAccounts()) {
            if (Account.AccountType.DEFAULT == account.getAccountType()) {
                account.setAmount(account.getAmount() + value);
            }
        }

        Customer updatedCustomerReceiver = new Customer(receiverData.getCustomerId(), receiverData.getFirstName(), receiverData.getLastName(), receiverData.getAge(), receiverData.getUsername(),
                receiverData.getPassword(), receiverData.getEmail(), Customer.Bank.valueOf(receiverData.getBank()), receiverData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + receiverData.getCustomerId(), updatedCustomerReceiver);
    }


    private void putSenderAccountValue(Customer senderData) {
        for (Account account : senderData.getAccounts()) {
            if (sendingAccount.getAccountType() == account.getAccountType()) {
                account.setAmount(account.getAmount() - value);
            }
        }

        Customer updatedCustomerSender = new Customer(senderData.getCustomerId(), senderData.getFirstName(), senderData.getLastName(), senderData.getAge(), senderData.getUsername(),
                senderData.getPassword(), senderData.getEmail(), senderData.getBank(), senderData.getAccounts());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + senderData.getCustomerId(), updatedCustomerSender);
    }

}