package com.example.exam_project.HttpRequestTasks;

import com.example.exam_project.Customer;
import com.example.exam_project.Data;

public class DataCustomerParser {

    public DataCustomerParser() {
    }

    public Customer dataToCustomer(Data data) {
        if (data != null) {
            Customer customer = new Customer(data.getCustomerId(), data.getFirstName(), data.getLastName(),
                    data.getAge(), data.getUsername(), data.getPassword(), data.getEmail(),
                    Customer.Bank.valueOf(data.getBank()), data.getAccounts());

            return customer;
        }
        return null;
    }
}
