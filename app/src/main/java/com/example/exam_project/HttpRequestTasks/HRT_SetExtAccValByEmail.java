
package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.exam_project.Account;
import com.example.exam_project.Bill;
import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.HttpURLConnection;
import java.net.URL;

public class HRT_SetExtAccValByEmail extends AsyncTask<Void, Void, CustomerData> {


    Account sendingAccount;
    String emailOfReceiver;
    double value;
    Customer customer;

    // For bill functionality
    SendType sendType;
    Long billId;

    public HRT_SetExtAccValByEmail(Account sendingAccount, String emailOfReceiver, double value, Customer customer, SendType sendType, Long billId) {
        this.sendingAccount = sendingAccount;
        this.emailOfReceiver = emailOfReceiver;
        this.value = value;
        this.customer = customer;
        this.sendType = sendType;
        this.billId = billId;
    }

    public HRT_SetExtAccValByEmail(Account sendingAccount, String emailOfReceiver, double value, Customer customer) {
        this.sendingAccount = sendingAccount;
        this.emailOfReceiver = emailOfReceiver;
        this.value = value;
        this.customer = customer;
    }

    private void putSenderAccountValue(Customer senderData) {
        for (Account account : senderData.getAccounts()) {
            if (sendingAccount.getAccountType() == account.getAccountType()) {
                account.setAmount(account.getAmount() - value);
            }
        }

        if (sendType == SendType.BILL) {
            for (Bill bill :
                    senderData.getBills()) {
                if (bill.getId() == billId) {
                    bill.setPaid(true);
                }
            }
        }

        Customer updatedCustomerSender = new Customer(senderData.getCustomerId(), senderData.getFirstName(), senderData.getLastName(), senderData.getAge(), senderData.getUsername(),
                senderData.getPassword(), senderData.getEmail(), senderData.getBank(), senderData.getAccounts(), senderData.getBills());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + senderData.getCustomerId(), updatedCustomerSender);

        new HRT_PostTransaction(value, getUserData().getCustomerId(), senderData.getCustomerId(), sendingAccount.getAccountType()).execute();

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
            CustomerData customerData = restTemplate.getForObject(url, CustomerData.class);
            return customerData;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return null;
    }

    void putAccountValue(CustomerData userCustomerData) {
        putReceiverAccountValue(userCustomerData);
        putSenderAccountValue(customer);
    }

    private void putReceiverAccountValue(CustomerData receiverCustomerData) {
        for (Account account : receiverCustomerData.getAccounts()) {
            if (Account.AccountType.DEFAULT == account.getAccountType()) {
                account.setAmount(account.getAmount() + value);
            }
        }

        Customer updatedCustomerReceiver = new Customer(receiverCustomerData.getCustomerId(), receiverCustomerData.getFirstName(), receiverCustomerData.getLastName(), receiverCustomerData.getAge(), receiverCustomerData.getUsername(),
                receiverCustomerData.getPassword(), receiverCustomerData.getEmail(), Customer.Bank.valueOf(receiverCustomerData.getBank()), receiverCustomerData.getAccounts(), receiverCustomerData.getBills());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        restTemplate.put("http://10.0.2.2:8080/customers/" + receiverCustomerData.getCustomerId(), updatedCustomerReceiver);
    }


    public enum SendType {
        BILL,
        DEPOSIT
    }


}