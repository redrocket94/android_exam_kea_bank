package com.example.exam_project;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
    private int id;
    // Ignore this
    private Long customerId;
    private String firstName;
    private String lastName;
    private int age;
    private String username;
    private String password;
    private String email;
    private Bank bank;
    private List<Account> accounts;

    public Customer(String username, String password, String email, String firstName, String lastName, int age, List<Account> accounts) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.accounts = accounts;
    }

    public Customer() {
    }

    public Customer(String firstName, String lastName, int age, List<Account> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.accounts = accounts;
    }


    public Customer(String firstName, String lastName, int age, String username, String password, String email, Bank bank, List<Account> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bank = bank;
        this.accounts = accounts;
    }

    public Customer(Long customerId, String firstName, String lastName, int age, String username, String password, String email, Bank bank, List<Account> accounts) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bank = bank;
        this.accounts = accounts;
    }

    public Customer(int id, Long customerId, String firstName, String lastName, int age, String username, String password, String email, Bank bank, List<Account> accounts) {
        this.id = id;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.email = email;
        this.bank = bank;
        this.accounts = accounts;
    }

    protected Customer(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        age = in.readInt();
        username = in.readString();
        password = in.readString();
        email = in.readString();
        if (in.readByte() == 0x01) {
            accounts = new ArrayList<Account>();
            in.readList(accounts, Account.class.getClassLoader());
        } else {
            accounts = null;
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(age);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        if (accounts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(accounts);
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }

    public enum Bank {
        COPENHAGEN,
        ODENSE
    }

}