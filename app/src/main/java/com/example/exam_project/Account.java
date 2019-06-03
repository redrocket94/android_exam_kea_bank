package com.example.exam_project;


import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements Parcelable {

    private int accountId;
    private Long customer_id;
    private AccountType accountType;
    private double amount = 0;
    private boolean approved;

    public Account() {
    }

    public Account(int accountId) {
        this.accountId = accountId;
    }

    public Account(int accountId, int amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public Account(int accountId, AccountType accountType, double amount) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.amount = amount;
    }

    public Account(Long customer_id, AccountType accountType, double amount, boolean approved) {
        this.customer_id = customer_id;
        this.accountType = accountType;
        this.amount = amount;
        this.approved = approved;
    }

    public Account(int accountId, Long customer_id, AccountType accountType, double amount, boolean approved) {
        this.accountId = accountId;
        this.customer_id = customer_id;
        this.accountType = accountType;
        this.amount = amount;
        this.approved = approved;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public enum AccountType {
        BUDGET,
        BUSINESS,
        DEFAULT,
        PENSION,
        SAVINGS
    }

    protected Account(Parcel in) {
        accountId = in.readInt();
        customer_id = in.readByte() == 0x00 ? null : in.readLong();
        accountType = (AccountType) in.readValue(AccountType.class.getClassLoader());
        amount = in.readDouble();
        approved = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(accountId);
        if (customer_id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(customer_id);
        }
        dest.writeValue(accountType);
        dest.writeDouble(amount);
        dest.writeByte((byte) (approved ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}