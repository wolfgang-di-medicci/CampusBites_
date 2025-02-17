package com.example.cafeticker;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    // Global Variables
    private View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // Welcome user
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
        String ticker = sharedPreferences.getString("name", null);
        TextView welcomeTv = view.findViewById(R.id.textViewWelcome);
        String welcome = "Welcome " + ticker;
        welcomeTv.setText(welcome);

        // Handle Download
        Button btnDownload = view.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(v1 -> handleDownload());

        // Tab
        TabLayout tabLayout = view.findViewById(R.id.tabHome);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerHome);
        StatPagerAdapter adapter = new StatPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Daily Stat");
                    break;
                case 1:
                    tab.setText("Weekly Stat");
                    break;
                case 2:
                    tab.setText("Monthly Stat");
                    break;
            }
        }).attach();

        return view;
    }

    public void handleDownload() {
        // Inputs
        int REQUEST_WRITE_PERMISSION = 100;
        String date = ((EditText)(view.findViewById(R.id.homeInputDatee))).getText().toString();
        SharedPreferences sharedPreferences = SecurePrefs.getEncryptedSharedPreferences(getContext());
        String ticker = sharedPreferences.getString("username", null);

        if (date.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Fire store with the date and ticker
        db.collection("Meals")
                .whereEqualTo("date", date)  // Ensure the date format is correct
                .whereEqualTo("ticker", ticker)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        StringBuilder data = new StringBuilder();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String tickerUsername = document.getString("ticker");
                            String tickedStudent = document.getString("username");
                            String meal = document.getString("meal");

                            data.append("Date: ").append(date)
                                    .append(", Ticker: ").append(tickerUsername)
                                    .append(", Ticked Student: ").append(tickedStudent)
                                    .append(", Meal: ").append(meal)
                                    .append("\n");
                        }

                        // Get the path to the Downloads folder
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        if (path == null) {
                            Toast.makeText(getContext(), "External storage not available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create the file in the Downloads directory
                        File file = new File(path, date + ".txt");

                        try {
                            FileWriter writer = new FileWriter(file);
                            writer.append(data.toString());
                            writer.flush();
                            writer.close();

                            Toast.makeText(getContext(), "File saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            Log.d("FileSave", "File saved at: " + file.getAbsolutePath());
                        } catch (IOException e) {
                            Log.e("FileSave", "Error saving file", e);
                        }

                    } else {
                        // If the query failed, log the error
                        Log.d("HandleDownload", "No data found for this date. Task result: " + task.getResult());
                        Toast.makeText(getContext(), "No data found for this date", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}