package com.example.cafeticker;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class MonthlyStatFragment extends Fragment {

    public MonthlyStatFragment() {}

    // Global Variables
    private View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.O)
    private final LocalDate today = LocalDate.now();
    @RequiresApi(api = Build.VERSION_CODES.O)
    private final LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
    @RequiresApi(api = Build.VERSION_CODES.O)
    private final LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
    private final SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
    private final String ticker = sharedPreferences.getString("username", null);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_monthly_stat, container, false);

        // Query Total ticks by a ticker in a month
        handleTotalTicks();

        // Query Reports by a ticker in a month
        handleTotalReported();

        // Query Breakfast usage in a month
        TextView valueBreakfast = view.findViewById(R.id.valueBreakfast);
        handleTotalMeal("breakfast", valueBreakfast);

        // Query Dinner usage in a month
        TextView valueDinner = view.findViewById(R.id.valueDinner);
        handleTotalMeal("dinner", valueDinner);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleTotalTicks () {
        TextView valueTickedStudents = view.findViewById(R.id.valueTickedStudents);

        db.collection("Ticks")
                .whereGreaterThanOrEqualTo("date", startOfMonth.toString()) // From Monday
                .whereLessThanOrEqualTo("date", endOfMonth.toString()) // Until Sunday
                .whereEqualTo("username", ticker) // Filter by ticker username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int tickedStudentsCount = task.getResult().size(); // Count number of documents
                        valueTickedStudents.setText(String.valueOf(tickedStudentsCount));
                    } else {
                        Log.e("Fire store", "Error fetching monthly ticks", task.getException());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleTotalReported () {
        TextView valueReportedStudents = view.findViewById(R.id.valueReportedStudents);

        db.collection("Reports")
                .whereGreaterThanOrEqualTo("date", startOfMonth.toString()) // From Monday
                .whereLessThanOrEqualTo("date", endOfMonth.toString()) // Until Sunday
                .whereEqualTo("ticker", ticker)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int reportedStudentsCount = task.getResult().size(); // Count number of documents
                        valueReportedStudents.setText(String.valueOf(reportedStudentsCount));
                    } else {
                        Log.e("Fire store", "Error fetching monthly reports", task.getException());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleTotalMeal (String meal, TextView tv) {
        db.collection("Meals")
                .whereGreaterThanOrEqualTo("date", startOfMonth.toString()) // From Monday
                .whereLessThanOrEqualTo("date", endOfMonth.toString()) // Until Sunday
                .whereEqualTo("ticker", ticker)
                .whereEqualTo("meal", meal)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int studentsCount = task.getResult().size(); // Count number of documents
                        tv.setText(String.valueOf(studentsCount));
                    } else {
                        Log.e("Fire store", "Error fetching info", task.getException());
                    }
                });
    }

}