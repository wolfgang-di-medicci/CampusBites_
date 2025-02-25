package com.example.cafeticker;

import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.Executor;

public class TickFragment extends Fragment {

    public TickFragment() {}

    // Global Variables
    private View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.O)
    LocalDate today = LocalDate.now();
    @RequiresApi(api = Build.VERSION_CODES.O)
    String date = today.toString(); // Format: YYYY-MM-DD

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tick, container, false);

        // Search by Finger print
        Button btnFinger = view.findViewById(R.id.btnFinger);
        btnFinger.setOnClickListener(view1 -> handleSearchByFingerPrint());

        // Search & Retrieve from Fire store
        Button btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(view1 -> handleSearch());




        // Meal Buttons
        Button btnBreakfast = view.findViewById(R.id.btnBreakfast);
        Button btnLunch = view.findViewById(R.id.btnLunch);
        Button btnDinner = view.findViewById(R.id.btnDinner);

        // Button visibility
        setButtonVisibility(btnBreakfast, btnLunch, btnDinner);

        // Send Meals to Fire store
        btnBreakfast.setOnClickListener(v1 -> handleMeal("breakfast", btnBreakfast));
        btnLunch.setOnClickListener(v2 -> handleMeal("lunch", btnLunch));
        btnDinner.setOnClickListener(v3 -> handleMeal("dinner", btnDinner));

        // Send Report to firestore
        Button btnReport = view.findViewById(R.id.btnReport);
        btnReport.setOnClickListener(v4 -> handleReport(view));

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleSearch(){
        // Input Fields
        String username = ((EditText)(view.findViewById(R.id.tickUsername))).getText().toString();
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (username.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Output Fields
        TextView nameOutput = view.findViewById(R.id.textViewNameOutput);
        TextView departmentOutput = view.findViewById(R.id.textViewDepartmentOutput);
        TextView yearOutput = view.findViewById(R.id.textViewYearOutput);

        // Handle Search
        db.collection("Students")
                .document(username)  // Searching by document ID (username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            // Display Elements
                            ConstraintLayout afterFound = view.findViewById(R.id.containerAfterSearch);
                            afterFound.setVisibility(View.VISIBLE);

                            // Extract student details
                            String nameOut = doc.getString("name");
                            String departmentOut = doc.getString("department");
                            String yearOut = doc.getString("year");
                            String image = doc.getString("image");

                            // Display data in TextViews
                            nameOutput.setText(nameOut);
                            departmentOutput.setText(departmentOut);
                            yearOutput.setText(yearOut);
                            displayUserImage(image);
                            handleTodayMeals(username);
                        } else {
                            Toast.makeText(getContext(), "Student not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    // Finger print trial
    public void handleSearchByFingerPrint() {
        FingerPrintUse fingerprint = new FingerPrintUse(getActivity());
        fingerprint.authenticate();
    }

    // Display user image after search using glide
    public void displayUserImage(String imageUrl) {
        ImageView imageView = getView().findViewById(R.id.imageView3);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // Optional placeholder image
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleTodayMeals(String username) {
        // Query Fire store with the date and meal
        db.collection("Meals")
                .whereEqualTo("date", date)
                .whereEqualTo("meal", "breakfast")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        view.findViewById(R.id.textViewBreakfast).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.textViewBreakfast).setVisibility(View.INVISIBLE);
                    }
                });
        db.collection("Meals")
                .whereEqualTo("date", date)
                .whereEqualTo("meal", "lunch")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        view.findViewById(R.id.textViewLunch).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.textViewLunch).setVisibility(View.INVISIBLE);
                    }
                });
        db.collection("Meals")
                .whereEqualTo("date", date)
                .whereEqualTo("meal", "dinner")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        view.findViewById(R.id.textViewDinner).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.textViewDinner).setVisibility(View.INVISIBLE);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleMeal(String meal, Button btn) {
        // Input Fields
        String username = ((EditText)(view.findViewById(R.id.tickUsername))).getText().toString();
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
        String ticker = sharedPreferences.getString("username", null);

        Meal m = new Meal(username, date, meal, ticker);
        String id = UUID.randomUUID().toString();

        // Handle add meal
        db.collection("Meals")
                .document(id)
                .set(m)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Meal Added Successfully!", Toast.LENGTH_SHORT).show();
                    handleTicker();
                    btn.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error Adding Meal", Toast.LENGTH_SHORT).show()
                );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleTicker() {
        // Inputs
        String ticked = ((EditText)(view.findViewById(R.id.tickUsername))).getText().toString();
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
        String ticker = sharedPreferences.getString("username", null);

        Ticker t = new Ticker(ticker, date, ticked);
        String id = UUID.randomUUID().toString();

        // Handle add Ticker
        db.collection("Ticks")
                .document(id)
                .set(t)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Ticker Added Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error Adding Ticker", Toast.LENGTH_SHORT).show()
                );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleReport(View view) {
        //Handle Report
        String username = ((EditText)(view.findViewById(R.id.tickUsername))).getText().toString();
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
        String ticker = sharedPreferences.getString("username", null);

        Report r = new Report(ticker, date, username);
        String id = UUID.randomUUID().toString();

        // Handle add meal
        db.collection("Reports")
                .document(id)
                .set(r)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Report Added Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error Adding Report", Toast.LENGTH_SHORT).show()
                );
    }

    public void setButtonVisibility(Button breakfast, Button lunch, Button dinner) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Breakfast: 6:00 AM - 9:00 AM
        if (hour >= 6 && (hour < 9 || (hour == 9 && minute == 0))) {
            breakfast.setVisibility(View.VISIBLE);
        } else {
            breakfast.setEnabled(false);
        }

        // Lunch: 11:00 AM - 1:00 PM
        if (hour >= 11 && (hour < 13 || (hour == 13 && minute == 0))) {
            lunch.setVisibility(View.VISIBLE);
        } else {
            lunch.setEnabled(false);
        }

        // Dinner: 5:00 PM - 7:00 PM
        if (hour >= 17 && (hour < 19 || (hour == 19 && minute == 0))) {
            dinner.setVisibility(View.VISIBLE);
        } else {
            dinner.setEnabled(false);
        }
    }

}