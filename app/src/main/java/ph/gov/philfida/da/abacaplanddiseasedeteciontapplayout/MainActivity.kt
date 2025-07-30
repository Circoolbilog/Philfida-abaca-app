/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;
// Import stuff
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AboutApp;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AccountDetails;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AssessmentActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseIndex;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.Map;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.SettingsActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.WelcomeScreen;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private FirebaseUser user;
    private DatabaseReference reference2;
    String firstName, lastName, middleName, email, birthday, permAdd, occupation, institution;
    //    private boolean Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;
    private String stringVal_Bract_Mosaic, stringVal_Bunchy_Top, stringVal_CMV, stringVal_Gen_Mosaic, stringVal_SCMV, stringVal_No_Allocation;
    private String DIName, DIDesc, DIPicture, DITreatment;
//    private int symptomID;

    public static final String SHARED_PREFS = "USER_DATA";
    public static final String EMAIL = "EMAIL";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String MIDDLE_NAME = "MIDDLE_NAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String PERM_ADD = "PERM_ADD";
    public static final String OCCUPATION = "OCCUPATION";
    public static final String INSTITUTION = "INSTITUTION";
    private static final String TAG = "MainActivity";
    private final int currentDbSize = 39;

    int ALL_PERMISSIONS = 101;
    boolean saving;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (((SettingsContainer) this.getApplication()).getShowWelcome() == null) {
            ((SettingsContainer) this.getApplication()).setShowWelcome(true);
        }
        if (((SettingsContainer) this.getApplication()).getShowWelcome()) {
            welcomeScreen();
        }
        askPermissions();
        try {
            if (!((SettingsContainer) this.getApplication()).getGuest()) {
                getUserDBDetails();
                loadUserData();
                saveUserData();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        // Initialize the database
        DiseaseInfoDBHelper dbHelper = new DiseaseInfoDBHelper(this);

    }

    private void askPermissions() {
    // Ask user for permission for read and write, and location
        final String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }




    private void welcomeScreen() {
        Intent intent = new Intent(this, WelcomeScreen.class);
        startActivity(intent);
    }



    //Load user data from Shared Preference(locally stored)

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        lastName = sharedPreferences.getString(LAST_NAME, "");
        firstName = sharedPreferences.getString(FIRST_NAME, "");
        middleName = sharedPreferences.getString(MIDDLE_NAME, "");
        email = sharedPreferences.getString(EMAIL, "");
        birthday = sharedPreferences.getString(BIRTHDAY, "");
        permAdd = sharedPreferences.getString(PERM_ADD, "");
        occupation = sharedPreferences.getString(OCCUPATION, "");
        institution = sharedPreferences.getString(INSTITUTION, "");
    }

    //setting up navigation drawer(sidebar)

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private void setUpNavDrawer() {
        navigationView = findViewById(R.id.navView);
        View header = navigationView.getHeaderView(0);
        TextView navName = header.findViewById(R.id.navUserName);
        navName.setText(firstName + " " + lastName);
        ImageView navPicture = header.findViewById(R.id.userAvatar);
        if (user.getPhotoUrl() != null) {
            Glide.with(MainActivity.this)
                    .load(user.getPhotoUrl())
                    .override(400, 400)
                    .into(navPicture);
        }
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        Menu menu = navigationView.getMenu();
        MenuItem userNameInNav = menu.findItem(R.id.userName);
        userNameInNav.setTitle(firstName + " " + lastName);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.userName) {
                openAccountDetails();
                return true;
            } else if (itemId == R.id.settings) {
                openSettings();
                return true;
            } else if (itemId == R.id.about) {
                openAboutActivity();
                return true;
            } else if (itemId == R.id.logout) {
                logOut();
                return true;
            }
            return false;
        });
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutApp.class);
        startActivity(intent);
    }

    //Logout user

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    //set up what happens whe user selects item from the menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Download user data from Firebase Database

    private void getUserDBDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    firstName = userProfile.firstName;
                    lastName = userProfile.lastName;
                    middleName = userProfile.middleName;
                    email = userProfile.email;
                    birthday = userProfile.birthday;
                    permAdd = userProfile.permanentAddress;
                    institution = userProfile.institution;
                    occupation = userProfile.occupation;
                    saveUserData();
                    setUpNavDrawer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        setUpNavDrawer();
    }

    //Open Diagnose Activity

    public void openDiagnoseActivity(View view) {
        Intent diagnose = new Intent(this, DetectorActivity.class);
        startActivity(diagnose);
    }

    //Open Assesment Activity

    public void openAssessmentActivity(View view) {
        Intent assessment = new Intent(this, AssessmentActivity.class);
        startActivity(assessment);
    }

    //Open AccountDetails Activity

    public void openAccountDetails() {
        Intent intent = new Intent(MainActivity.this, AccountDetails.class);
        startActivity(intent);
    }

    //open Disease Index Activity

    public void openDiseaseIndex(View view) {
        Intent intent = new Intent(MainActivity.this, DiseaseIndex.class);
        startActivity(intent);
    }

    /**
     * download images from database
     */

    class DownloadRunnable implements Runnable {

        String image, name;

        DownloadRunnable(String imageURL, String name) {
            this.image = imageURL;
            this.name = name;
        }

        @Override
        public void run() {
            downloadImage(image, name);
        }

    }


    void downloadImage(String imageURL, String name) {
        Log.d(TAG, "downloadImage: try download");
        if (!verifyPermissions()) {
            return;
        }

        String dirPath = Environment.getExternalStorageDirectory() + "/Pictures/Diseases/";

        final File dir = new File(dirPath);

        final String fileName = name;

        Glide.with(this)
                .load(imageURL)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        saveImage(bitmap, dir, fileName);
                        Log.d(TAG, "downloadImage: save " + imageURL);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Log.d(TAG, "onLoadFailed: failed");
                    }
                });

    }

    public Boolean verifyPermissions() {

        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {

            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1);
            return false;
        }

        return true;

    }

    private void saveImage(Bitmap image, File storageDir, String imageFileName) {
    File img = new File(storageDir.toString() + "/" + imageFileName + ".jpg");
        boolean successDirCreated = false;
        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir();
            Log.d(TAG, "downloadImage: try download");
        }
 
        Log.d(TAG, "downloadImage: " + img.toString() + " exists: " + img.exists());
        if (img.exists()){
            Log.d(TAG, "downloadImage: File exists");
            return;
        }
        File imageFile = new File(storageDir, imageFileName);
        OutputStream fOut;
        if (isBuildVersionQ()) {
            //Save Image File
            Uri imagePathUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver imageOneResolver = getContentResolver();
            ContentValues imageOneCV = new ContentValues();
            imageOneCV.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName + ".jpg");
            imageOneCV.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageOneCV.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Diseases");

            Uri imageUri = imageOneResolver.insert(imagePathUri, imageOneCV);
            Log.d(TAG, "downloadImage: try save Q");
            try {
                fOut = imageOneResolver.openOutputStream(Objects.requireNonNull(imageUri));
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                Objects.requireNonNull(fOut).close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, "downloadImage: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "downloadImage: saved");

        } else {
            try {
                fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
                Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    //save user info to shared prefs(store locally)

    /**
     * end
     * download images from database
     */
    //Check for the build version of the android phone
    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }

    public void saveUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_NAME, lastName);
        editor.putString(FIRST_NAME, firstName);
        editor.putString(MIDDLE_NAME, middleName);
        editor.putString(EMAIL, email);
        editor.putString(BIRTHDAY, birthday);
        editor.putString(PERM_ADD, permAdd);
        editor.putString(OCCUPATION, occupation);
        editor.putString(INSTITUTION, institution);
        editor.apply();
    }

    //open account details when clicked from navigation menu
    public void openAccountDetailsFromNav(View view) {
        openAccountDetails();
    }

    public void openMap(View view) {
        Intent map = new Intent(this, Map.class);
        startActivity(map);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openAboutApp(View view) {
        Intent intent = new Intent(this, AboutApp.class);
        startActivity(intent);
    }
}
