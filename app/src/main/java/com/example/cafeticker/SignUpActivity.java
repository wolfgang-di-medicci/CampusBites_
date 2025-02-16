package com.example.cafeticker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Questions spinner
        Spinner spinner = findViewById(R.id.spinnerSignUpQuestion);
        // Get the values from strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_questions,  // Use the string-array name
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void handleSignUp(View v) {
        // Input Fields
        String name = ((EditText)findViewById(R.id.signUpName)).getText().toString();
        String userName = ((EditText)findViewById(R.id.signUpUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.signUpNumberPassword)).getText().toString();
        String question = ((Spinner)findViewById(R.id.spinnerSignUpQuestion)).getSelectedItem().toString();
        String answer = ((EditText)findViewById(R.id.signUpAnswer)).getText().toString();

        // handle Sign up using Encrypted shared preference
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(this); //Context was error check it out again
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("username", userName);
        editor.putString("password", password);
        editor.putString("question", question);
        editor.putString("answer", answer);
        editor.apply();

        Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();

        // Launch Log in
        Intent i = new Intent(this, LogInActivity.class);
        startActivity(i);
    }
}