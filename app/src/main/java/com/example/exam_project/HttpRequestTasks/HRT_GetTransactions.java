package com.example.exam_project.HttpRequestTasks;

import android.os.AsyncTask;

import com.example.exam_project.TransactionData;

public class HRT_GetTransactions extends AsyncTask<Void, Void, TransactionData> {
    @Override
    protected TransactionData doInBackground(Void... voids) {
        return null;
    }

/*
    Long id;


    public HRT_GetTransactions(Long id) {
        this.id = id;
    }

    @Override
    protected TransactionData doInBackground(Void... params) {
        TransactionData userTransactionData = getTransactionData();

        if (userTransactionData == null) {
            return null;
        }
        return userTransactionData;
    }

    TransactionData getTransactionData() {
        try {
            String url = "http://10.0.2.2:8080/transactions/" + id;
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
            return transactionData;
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return null;
    }*/
}