package com.example.exam_project.MailHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


// Extends AsyncTask because class needs to perform a network operation
public class SendMail extends AsyncTask<Void, Void, Void> {

    private boolean hadNoError = true;

    // Declaring variables
    private Context context;
    private Session session;
    // Important email sending data
    private MailType mailType;
    private String email;
    private String subject;
    private String message;
    private int generatedValue;

    public SendMail(Context context, MailType mailType, String email, int generatedValue) {
        this.context = context;
        this.mailType = mailType;
        this.email = email;
        this.generatedValue = generatedValue;
    }

    public SendMail(Context context, MailType mailType, String email) {
        this.context = context;
        this.mailType = mailType;
        this.email = email;
    }

    // Set subject and message depending on type of mail (requesting transaction or new password)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Setting subject and message depending on mailType
        if (mailType == MailType.PASSWORD_RESET) {

            subject = "Password Reset - KEA Bank";
            message = "A new password has been requested for your account on KEA Bank\n\nNew password: " + generatedValue;

        } else if (mailType == MailType.TRANSACTION_CONFIRMATION) {

            subject = "Transaction Verification";
            message = "A new password has been requested for your account on KEA Bank\nNew password: " + generatedValue;

        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (hadNoError) {
            if (mailType == MailType.PASSWORD_RESET) {
                Toast.makeText(context, "Requested new password!\nCheck your email for information.", Toast.LENGTH_SHORT).show();
            } else if (mailType == MailType.TRANSACTION_CONFIRMATION) {
                Toast.makeText(context, "Transaction sent for verification!\nCheck your email for required pin.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mailType == MailType.PASSWORD_RESET) {
                Toast.makeText(context, "Failed to request new password!", Toast.LENGTH_SHORT).show();
            } else if (mailType == MailType.TRANSACTION_CONFIRMATION) {
                Toast.makeText(context, "Failed to make a transaction!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Properties properties = new Properties();

        // Configuring email properties
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        // Create new session
        session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    // Authenticating password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
                    }
                });

        try {
            // Create MimeMessage
            MimeMessage mimeMessage = new MimeMessage(session);

            // Setting email data
            mimeMessage.setFrom(new InternetAddress(Config.EMAIL));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            // Send email
            Transport.send(mimeMessage);

        } catch (MessagingException e) {
            hadNoError = false;
            e.printStackTrace();
        }
        return null;

    }

    public enum MailType {
        PASSWORD_RESET,
        TRANSACTION_CONFIRMATION
    }

}
