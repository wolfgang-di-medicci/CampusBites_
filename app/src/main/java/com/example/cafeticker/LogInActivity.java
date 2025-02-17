package com.example.cafeticker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Spinner component
        Spinner spinner = findViewById(R.id.spinnerLogInQuestion);
        String[] questions = {"Age", "Birth place", "Key word"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void handleLogIn(View v) {
        String username = ((EditText)findViewById(R.id.logInUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.logInNumberPassword)).getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle Log in
        // Handle Log in using encrypted shared preference
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(this);
        String sharedUsername = sharedPreferences.getString("username", null);
        String sharedPassword = sharedPreferences.getString("password", null);

        if (username.equals(sharedUsername) && password.equals(sharedPassword))
        {
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();

            // Launch Ticker Dashboard
            Intent i = new Intent(this, TickerActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleForgotPassword(View v) {
        // Display 5 components
        findViewById(R.id.forgotPasswordText).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewQuestion).setVisibility(View.VISIBLE);
        findViewById(R.id.spinnerLogInQuestion).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewAnswer).setVisibility(View.VISIBLE);
        findViewById(R.id.logInAnswer).setVisibility(View.VISIBLE);
        findViewById(R.id.btnForgotPasswordSubmit).setVisibility(View.VISIBLE);
    }

    public void handleSubmit(View v) {
        String spinnerSelected = ((Spinner)findViewById(R.id.spinnerLogInQuestion)).getSelectedItem().toString();
        String logInAnswer = ((EditText)findViewById(R.id.logInAnswer)).getText().toString();

        if (logInAnswer.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle Log in using encrypted shared preference
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(this);
        String sharedQuestion = sharedPreferences.getString("question", null);
        String sharedAnswer = sharedPreferences.getString("answer", null);
        String sharedPassword = sharedPreferences.getString("password", null);

        if (spinnerSelected.equals(sharedQuestion) && logInAnswer.equals(sharedAnswer))
        {
            TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
            forgotPasswordText.setVisibility(View.VISIBLE);
            forgotPasswordText.setText(sharedPassword);
        } else {
            Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}