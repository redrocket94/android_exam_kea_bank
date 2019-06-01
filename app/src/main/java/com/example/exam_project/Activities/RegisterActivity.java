package com.example.exam_project.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exam_project.HttpRequestTasks.HRT_Register;
import com.example.exam_project.R;

import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.internet.InternetAddress;

public class RegisterActivity extends AppCompatActivity {

    EditText reg_username;
    EditText reg_password;
    EditText reg_password_verify;

    EditText reg_email;
    EditText reg_firstName;
    EditText reg_lastName;
    EditText reg_age;

    ArrayList<EditText> regInputList = new ArrayList<>();

    LocationManager locationManager;
    LocationListener locationListener;
    Location userLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind all EditTexts to IDs
        reg_username = findViewById(R.id.reg_username);
        reg_password = findViewById(R.id.reg_password);
        reg_password_verify = findViewById(R.id.reg_password_verify);

        reg_email = findViewById(R.id.reg_email);
        reg_firstName = findViewById(R.id.reg_firstName);
        reg_lastName = findViewById(R.id.reg_lastname);
        reg_age = findViewById(R.id.reg_age);

        regInputList.addAll(Arrays.asList(reg_username, reg_password, reg_password_verify, reg_email, reg_firstName, reg_lastName, reg_age));


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission if no permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            // We have permission so do check
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);

        }
    }

    public void onRegisterClick(View v) {
        super.onStart();

        for (EditText input : regInputList) {
            if (input.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "You need to fill out all fields! Try again.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (Integer.parseInt(reg_age.getText().toString()) > 125) {
            Toast.makeText(getApplicationContext(), "You're the oldest person alive! Wow!", Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(reg_age.getText().toString()) < 18) {
            Toast.makeText(getApplicationContext(), "You're too young to register for a bank account!", Toast.LENGTH_SHORT).show();
            return;
        } else if (reg_username.getText().toString().length() < 8 || reg_username.getText().toString().length() > 16) {
            Toast.makeText(getApplicationContext(), "Invalid username, either too short or too long! Try again.", Toast.LENGTH_SHORT).show();
            return;
        } else if (reg_password.getText().toString().length() < 8 || reg_password.getText().toString().length() > 16) {
            Toast.makeText(getApplicationContext(), "Password too short or too long! Try again.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!reg_password.getText().toString().toLowerCase().equals(reg_password_verify.getText().toString().toLowerCase())) {
            Toast.makeText(getApplicationContext(), "Your password needs to match the password in password confirmation field! Try again.", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isEmailVerified(reg_email.getText().toString())) {
            Toast.makeText(getApplicationContext(), "You need to input a valid email address! Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        new HRT_Register(reg_firstName, reg_lastName, reg_age, reg_username, reg_password, reg_email, userLocation, this).execute();
    }

    public void onReturnToLoginClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private boolean isEmailVerified(String email) {
        boolean result = true;
        try {
            if (email.equals("user@[10.9.8.7]") || email.equals("user@localhost")) {
                result = false;
            }
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
