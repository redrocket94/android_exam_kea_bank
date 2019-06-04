package com.example.exam_project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    private Long transactionId;
    private Long senderId;
    private Account.AccountType senderAccountType;
    private Long recipientId;
    private double amountTransferred = 0;
    private Date transactionDate;

    public Transaction() {
    }

    public Transaction(Long senderId, Account.AccountType senderAccountType, Long recipientId, double amountTransferred) {
        this.senderId = senderId;
        this.senderAccountType = senderAccountType;
        this.recipientId = recipientId;
        this.amountTransferred = amountTransferred;
    }

    protected void onCreate() {
        transactionDate = new Date();
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public double getAmountTransferred() {
        return amountTransferred;
    }

    public void setAmountTransferred(double amountTransferred) {
        this.amountTransferred = amountTransferred;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Account.AccountType getSenderAccountType() {
        return senderAccountType;
    }

    public void setSenderAccountType(Account.AccountType senderAccountType) {
        this.senderAccountType = senderAccountType;
    }
}
