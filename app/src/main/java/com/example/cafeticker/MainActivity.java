package com.example.cafeticker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Encrypted shared preference to display either login or signup button
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(this);
        String name = sharedPreferences.getString("name", null);
        if (name == null || name.isEmpty()) {
            Button btnSignUp = findViewById(R.id.btnSignUp);
            btnSignUp.setVisibility(View.VISIBLE);
            Button btnLogIn = findViewById(R.id.btnLogIn);
            btnLogIn.setVisibility(View.INVISIBLE);
        }
    }

    public void launchLogIn(View v) {
        // Launch login
        Intent i = new Intent(this, LogInActivity.class);
        startActivity(i);
    }

    public void launchSignUp(View v) {
        // Launch Sign up
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
}