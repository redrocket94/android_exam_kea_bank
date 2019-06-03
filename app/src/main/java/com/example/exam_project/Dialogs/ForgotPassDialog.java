package com.example.exam_project.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.HttpRequestTasks.HRT_UpdateUserByEmail;
import com.example.exam_project.MailHandler.SendMail;
import com.example.exam_project.Modules.NemID;
import com.example.exam_project.R;

public class ForgotPassDialog extends DialogFragment {

    int generatedValue = new NemID().getRandomValue();
    EditText req_email;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.forgot_pass_dialog, null));

        builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                req_email = getDialog().findViewById(R.id.curr_pass);
                String email = req_email.getText().toString();
                sendMail(email);
                new HRT_UpdateUserByEmail(email, generatedValue).execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                req_email = getDialog().findViewById(R.id.curr_pass);
                String email = req_email.getText().toString();
                System.out.println(email);
            }
        });

        return builder.create();
    }

    private void sendMail(String email) {

        // Get email string from view


        if (!email.equals("") || email != null) {

            SendMail sendMail = new SendMail(getContext(), SendMail.MailType.PASSWORD_RESET, email, generatedValue);
            sendMail.execute();
        } else {
            Toast.makeText(getContext(), "You need to fill out the form or cancel!", Toast.LENGTH_SHORT).show();
        }
    }
}
