package com.example.cafeticker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.util.Map;

public class AddStudentFragment extends Fragment {

    public AddStudentFragment() {}

    // Global Variables
    private View view;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_student, container, false);

        // Year Spinner
        Spinner yearSpinner = view.findViewById(R.id.spinnerAddYear);
        ArrayAdapter<CharSequence> yAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.year_options,
                android.R.layout.simple_spinner_item
        );
        yAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yAdapter);

        // Department Spinner
        Spinner departmentSpinner = view.findViewById(R.id.spinnerAddDepartment);
        ArrayAdapter<CharSequence> dAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.department_options,
                android.R.layout.simple_spinner_item
        );
        dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(dAdapter);

        // Image Selection
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        imageView = view.findViewById(R.id.imageView2);
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Submit & Send to Fire store
        Button btnSubmit = view.findViewById(R.id.btnSubmitAddStudent);
        btnSubmit.setOnClickListener(view1 -> handleAddStudent());

        return view;
    }

    // Handle Add Student (Including Image Upload)
    public void handleAddStudent() {
        // Input Fields
        String name = ((EditText)(view.findViewById(R.id.addName))).getText().toString();
        String username = ((EditText)(view.findViewById(R.id.addUsername))).getText().toString();
        String yearSelected = ((Spinner)(view.findViewById(R.id.spinnerAddYear))).getSelectedItem().toString();
        String departmentSelected = ((Spinner)(view.findViewById(R.id.spinnerAddDepartment))).getSelectedItem().toString();

        // Input Validation
        if (name.isEmpty() || username.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Progress bar
        ProgressBar progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        // Create student object with image URL as null initially
        Student student = new Student(name, username, yearSelected, departmentSelected, null);

        // Upload Image if exists
        if (imageUri != null) {
            File imageFile = new File(getRealPathFromURI(imageUri));
            uploadImageToCloudinary(imageFile, student, progressBar);
        } else {
            // No image selected, proceed to add student without image
            addStudentToFirestore(student, null, progressBar);
        }

        FingerPrintUse fingerprint = new FingerPrintUse(getActivity());
        fingerprint.linkStudent(username); // Replace with the actual username
        fingerprint.authenticate();

    }

    // Upload Image to Cloudinary
    private void uploadImageToCloudinary(File imageFile, Student student, ProgressBar progressBar) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dbonieig3",
                "api_key", "928551176771689",
                "api_secret", "h0Wb60PDPGeuc-Ouh3Q81ZV3FJI"));

        new Thread(() -> {
            try {
                // Upload image to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("secure_url").toString();

                // Store URL in Firebase along with student data
                addStudentToFirestore(student, imageUrl, progressBar);
            } catch (Exception e) {
                e.printStackTrace();
                // Hide progress bar on failure
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Image upload failed.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Add student to Fire store (with or without image URL)
    private void addStudentToFirestore(Student student, String imageUrl, ProgressBar progressBar) {
        // If image URL is available, set it on the student object
        if (imageUrl != null) {
            student.setImage(imageUrl);
        }

        // Save student data in Fire store
        db.collection("Students")
                .document(student.getUsername()) // Use username as document ID
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    // Hide progress bar on success
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Student Added Successfully!", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Hide progress bar on failure
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Error Adding Student", Toast.LENGTH_SHORT).show();
                    });
                });
    }

    // Open File Chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle result of image selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);  // Display selected image in ImageView
        }
    }

    // Get real path from URI (needed for Cloudinary upload)
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }
}