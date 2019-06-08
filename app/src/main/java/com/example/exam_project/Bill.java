package com.example.exam_project;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDate;

import java.util.Date;

public class Bill implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };
    Long id;
    Long payerId;
    Long billCollectorId;
    String billCollectorEmail;
    Date scheduledDate;
    double value;
    boolean active;
    boolean isPaid;
    boolean autoPay;

    public Bill() {
    }


    public Bill(Long id, Long payerId, Long billCollectorId, String billCollectorEmail, Date scheduledDate, double value, boolean active, boolean isPaid, boolean autoPay) {
        this.id = id;
        this.payerId = payerId;
        this.billCollectorId = billCollectorId;
        this.billCollectorEmail = billCollectorEmail;
        this.scheduledDate = scheduledDate;
        this.value = value;
        this.active = active;
        this.isPaid = isPaid;
        this.autoPay = autoPay;
    }

    protected Bill(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        payerId = in.readByte() == 0x00 ? null : in.readLong();
        billCollectorId = in.readByte() == 0x00 ? null : in.readLong();
        billCollectorEmail = in.readString();
        long tmpScheduledDate = in.readLong();
        scheduledDate = tmpScheduledDate != -1 ? new Date(tmpScheduledDate) : null;
        value = in.readDouble();
        active = in.readByte() != 0x00;
        isPaid = in.readByte() != 0x00;
        autoPay = in.readByte() != 0x00;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getBillCollectorId() {
        return billCollectorId;
    }

    public void setBillCollectorId(Long billCollectorId) {
        this.billCollectorId = billCollectorId;
    }

    public String getBillCollectorEmail() {
        return billCollectorEmail;
    }

    public void setBillCollectorEmail(String billCollectorEmail) {
        this.billCollectorEmail = billCollectorEmail;
    }

    public LocalDate getLocalDate() {
        return new LocalDate(scheduledDate);
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isAutoPay() {
        return autoPay;
    }

    public void setAutoPay(boolean autoPay) {
        this.autoPay = autoPay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        if (payerId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(payerId);
        }
        if (billCollectorId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(billCollectorId);
        }
        dest.writeString(billCollectorEmail);
        dest.writeLong(scheduledDate != null ? scheduledDate.getTime() : -1L);
        dest.writeDouble(value);
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeByte((byte) (isPaid ? 0x01 : 0x00));
        dest.writeByte((byte) (autoPay ? 0x01 : 0x00));
    }
}