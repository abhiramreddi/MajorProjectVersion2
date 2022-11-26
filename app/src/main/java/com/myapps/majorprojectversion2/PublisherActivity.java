package com.myapps.majorprojectversion2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class PublisherActivity extends AppCompatActivity {


    protected BottomNavigationView bottomNavigationView;
    private Intent intent;

    protected ImageView uploadImage;
    protected TextInputEditText etTitle, etQuestion, etOption1, etOption2, etOption3, etOption4;
    protected ProgressBar progressBar;
    protected CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    protected Button upload;

    private Uri imageUri;
    String modelId;

    private final DatabaseReference root = FirebaseDatabase.getInstance("https://majorprojectv2-65c20-default-rtdb.firebaseio.com/").getReference("images");
    private final DatabaseReference root1 = FirebaseDatabase.getInstance("https://majorprojectv2-65c20-default-rtdb.firebaseio.com/").getReference("ads");
    private final StorageReference reference = FirebaseStorage.getInstance("gs://majorprojectv2-65c20.appspot.com").getReference("Images");

    String usernameForAds, correctOption;

    @SuppressLint({"ResourceType", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);


        bottomNavigationView = findViewById(R.id.bottomNavPublish);
        bottomNavigationView.setSelectedItemId(R.id.publishAds);


        Bundle bundle = getIntent().getExtras();
        usernameForAds = bundle.getString("user");


        uploadImage = findViewById(R.id.imgView);
        etTitle = findViewById(R.id.etTitle);
        etQuestion = findViewById(R.id.etQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etOption4 = findViewById(R.id.etOption4);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        if(checkBox1.isChecked()){
            correctOption = "1";
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        }else if(checkBox2.isChecked()){
            correctOption = "2";
            checkBox1.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
        }else if(checkBox3.isChecked()){
            correctOption = "3";
            checkBox2.setSelected(false);
            checkBox1.setSelected(false);
            checkBox4.setSelected(false);
        }else{
            correctOption = "4";
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox1.setSelected(false);
        }


        upload = findViewById(R.id.btnUpload);

        uploadImage.setOnClickListener(view -> {
            Intent gallery = new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult(gallery, 2);

        });

        upload.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadToFirebase(imageUri);
                uploadAdDetails(imageUri);
                uploadImage.setImageResource(R.drawable.ic_baseline_image_24);
                etTitle.setText("");
                etQuestion.setText("");
                etOption1.setText("");
                etOption2.setText("");
                etOption3.setText("");
                etOption4.setText("");
            } else {
                Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.viewAds:
                    intent = new Intent(getApplicationContext(), AdViewerActivity.class);
                    getApplicationContext().startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.profile:
                    intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    getApplicationContext().startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    private void uploadAdDetails(Uri imageUri) {
        String titleDb = Objects.requireNonNull(etTitle.getText()).toString();
        String questionDb = Objects.requireNonNull(etQuestion.getText()).toString();
        String option1Db = Objects.requireNonNull(etOption1.getText()).toString();
        String option2Db = Objects.requireNonNull(etOption2.getText()).toString();
        String option3Db = Objects.requireNonNull(etOption3.getText()).toString();
        String option4Db = Objects.requireNonNull(etOption4.getText()).toString();

        //
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            progressBar.setVisibility(View.INVISIBLE);
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Model model = new Model(uri.toString());
                modelId = root.push().getKey();
                assert modelId != null;
                root.child(modelId).setValue(model);
                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            });
        }).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "image uploading failed", Toast.LENGTH_SHORT).show();
        });

        //

        AdDetailsClass adDetailsClass = new AdDetailsClass(titleDb, questionDb, option1Db, option2Db, option3Db, option4Db, modelId);
        if (root1.child(usernameForAds).setValue(adDetailsClass).isSuccessful()) {
            Toast.makeText(getApplicationContext(), "Ad Published Successfully", Toast.LENGTH_SHORT).show();
        }
//        else {
//            Toast.makeText(getApplicationContext(), "error in database", Toast.LENGTH_SHORT).show();
//        }
    }

    private void uploadToFirebase(Uri imageUri) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            progressBar.setVisibility(View.INVISIBLE);
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Model model = new Model(uri.toString());
                modelId = root.push().getKey();
                assert modelId != null;
                root.child(modelId).setValue(model);
                Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            });
        }).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "image uploading failed", Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            uploadImage.setImageURI(imageUri);
        }
    }

}