package com.example.exam_project;

import java.util.Date;

public class TransactionData {

    private Long transaction_id;
    private Long sender_id;
    private Account.AccountType senderAccountType;
    private Long recipient_id;
    private double amount_transferred = 0;
    private Date transaction_date;

    public TransactionData(Long sender_id, Account.AccountType senderAccountType, Long recipient_id, double amount_transferred) {
        this.sender_id = sender_id;
        this.senderAccountType = senderAccountType;
        this.recipient_id = recipient_id;
        this.amount_transferred = amount_transferred;
    }

    public Long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Long getSender_id() {
        return sender_id;
    }

    public void setSender_id(Long sender_id) {
        this.sender_id = sender_id;
    }

    public Account.AccountType getSenderAccountType() {
        return senderAccountType;
    }

    public void setSenderAccountType(Account.AccountType senderAccountType) {
        this.senderAccountType = senderAccountType;
    }

    public Long getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(Long recipient_id) {
        this.recipient_id = recipient_id;
    }

    public double getAmount_transferred() {
        return amount_transferred;
    }

    public void setAmount_transferred(double amount_transferred) {
        this.amount_transferred = amount_transferred;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }
}
