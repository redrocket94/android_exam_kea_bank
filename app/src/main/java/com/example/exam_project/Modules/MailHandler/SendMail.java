package com.example.exam_project.Modules.MailHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.exam_project.R;

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
            message = "Verification number for new transaction in progress - KEA Bank\nVerification number: " + generatedValue;

        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (hadNoError) {
            if (mailType == MailType.PASSWORD_RESET) {
                Toast.makeText(context, context.getString(R.string.hrt_msg05_toast), Toast.LENGTH_SHORT).show();
            } else if (mailType == MailType.TRANSACTION_CONFIRMATION) {
                Toast.makeText(context, context.getString(R.string.hrt_msg06_toast), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mailType == MailType.PASSWORD_RESET) {
                Toast.makeText(context, context.getString(R.string.hrt_msg07_toast), Toast.LENGTH_SHORT).show();
            } else if (mailType == MailType.TRANSACTION_CONFIRMATION) {
                Toast.makeText(context, context.getString(R.string.hrt_msg06_toast), Toast.LENGTH_SHORT).show();
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
