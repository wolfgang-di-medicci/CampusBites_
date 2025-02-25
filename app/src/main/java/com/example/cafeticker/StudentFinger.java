package com.example.cafeticker;
import android.content.Context;
import android.content.SharedPreferences;

public class StudentFinger {
    // Finger print trial
    private static final String PREFS_NAME = "StudentPrefs";
    private static final String KEY_STUDENT_USERNAME = "student_username";
    private SharedPreferences sharedPreferences;

    public StudentFinger(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public void linkStudentToFingerprint(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_STUDENT_USERNAME, username);
        editor.apply();
    }

    public String getStudentUsername() {
        return sharedPreferences.getString(KEY_STUDENT_USERNAME, null);
    }
}
