package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;

import com.example.exam_project.Account;
import com.example.exam_project.Transaction;
import com.example.exam_project.TransactionData;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HRT_PostTransaction extends AsyncTask<Void, Void, TransactionData> {

    double amount_transferred;
    Long recipient_id;
    Long sender_id;
    Account.AccountType senderAccountType;

    public HRT_PostTransaction(double amount_transferred, Long recipient_id, Long sender_id, Account.AccountType senderAccountType) {
        this.amount_transferred = amount_transferred;
        this.recipient_id = recipient_id;
        this.sender_id = sender_id;
        this.senderAccountType = senderAccountType;
    }


    @Override
    protected TransactionData doInBackground(Void... voids) {

        String url = ("http://10.0.2.2:8080/transactions/");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        Transaction transaction = new Transaction(sender_id, senderAccountType, recipient_id, amount_transferred);
        System.out.println(transaction.getSenderId() + " " + transaction.getRecipientId());

        restTemplate.postForObject(url, transaction, Transaction.class);

        return null;
    }
}
