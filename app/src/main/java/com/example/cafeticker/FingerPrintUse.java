package com.example.cafeticker;

import android.content.Context;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class FingerPrintUse {
    private FragmentActivity activity;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private StudentFinger student;

    public FingerPrintUse(FragmentActivity activity) {
        this.activity = activity;
        this.student = new StudentFinger(activity);
        executor = ContextCompat.getMainExecutor(activity);

        biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Handle error
                Toast.makeText(activity, "Authentication Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Retrieve the student's username
                String username = student.getStudentUsername();
                if (username != null) {
                    // Tick the meal usage for the student
                    tickMealUsage(username);
                } else {
                    // No student linked to this fingerprint
                    Toast.makeText(activity, "No student linked to this fingerprint", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Handle failed authentication
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")
                .build();
    }

    public void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }

    private void tickMealUsage(String username) {
        // Logic to tick the meal usage for the authenticated student
        System.out.println("Meal ticked for: " + username);
        Toast.makeText(activity, "Username: " + username, Toast.LENGTH_SHORT).show();
    }

    public void linkStudent(String username) {
        // Link the student's username to the fingerprint
        student.linkStudentToFingerprint(username);
    }

    public boolean isFingerprintAvailable() {
        BiometricManager biometricManager = BiometricManager.from(activity);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
            default:
                return false;
        }
    }

}
