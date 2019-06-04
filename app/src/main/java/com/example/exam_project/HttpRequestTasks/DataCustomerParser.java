package com.example.exam_project.HttpRequestTasks;

import com.example.exam_project.Customer;
import com.example.exam_project.CustomerData;

public class DataCustomerParser {

    public DataCustomerParser() {
    }

    public Customer dataToCustomer(CustomerData customerData) {
        if (customerData != null) {
            Customer customer = new Customer(customerData.getCustomerId(), customerData.getFirstName(), customerData.getLastName(),
                    customerData.getAge(), customerData.getUsername(), customerData.getPassword(), customerData.getEmail(),
                    Customer.Bank.valueOf(customerData.getBank()), customerData.getAccounts());

            return customer;
        }
        return null;
    }
}
