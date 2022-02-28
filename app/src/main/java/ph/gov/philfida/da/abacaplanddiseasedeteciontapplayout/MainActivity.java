package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
            if (!((SettingsContainer) this.getApplication()).getGuest()){
                getUserDBDetails();
                loadUserData();
                saveUserData();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void askPermissions() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadDiseaseInfo();
        downloadDiseaseMap();
    }

    private void downloadDiseaseInfo() {
        DiseaseInfoDBHelper infoDBHelper = new DiseaseInfoDBHelper(MainActivity.this);
        List<DiseaseInfoDBModel> dbModelList = infoDBHelper.getDiseasesInfo();

        reference2 = FirebaseDatabase.getInstance().getReference("Disease_Info");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                DiseaseInfoDBModel infoDBModel;

                if (snapshot.exists()) {
//                      Clear table
//                    get info from firebase
                    for (DataSnapshot child : snapshot.getChildren()) {
                        DIName = child.getKey();
                        DIDesc = Objects.requireNonNull(child.child("desc").getValue()).toString();
                        DIPicture = Objects.requireNonNull(child.child("picture").getValue()).toString();
                        DITreatment = Objects.requireNonNull(child.child("treatment").getValue()).toString();
                        Log.d(TAG, "onDataChange: " + DIName + DIDesc + DIPicture + DITreatment);

                        //                    save info to local db

                        try {
                            int id = -1;
                            id += 1;
                            infoDBModel = new DiseaseInfoDBModel(id, DIName, DIDesc, DIPicture, DITreatment);

                            boolean success = infoDBHelper.addOneDiseaseInfo(infoDBModel);
                            if (!success)
                                Toast.makeText(getApplicationContext(), "try Failed to update local database", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "addToDiseaseInfoDb: " + infoDBModel.getDiseaseName() + " " + success +dbModelList.size());

                        } catch (Exception e) {
                            infoDBModel = new DiseaseInfoDBModel(90,"DIName", "DIDesc", "DIPicture", "DITreatment");
                            boolean success = infoDBHelper.addOneDiseaseInfo(infoDBModel);
                            if (!success)
                                Toast.makeText(getApplicationContext(), " catch Failed to update local database", Toast.LENGTH_SHORT).show();
                        }

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void downloadDiseaseMap() {
        DiseaseSymptomsDbHelper dbHelper = new DiseaseSymptomsDbHelper(this);
        List<DiseaseDBModel> diseaseDBModels = dbHelper.getDiseases();

        reference2 = FirebaseDatabase.getInstance().getReference("Diseases");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
                int dbSize = dbModelList.size();
                int[] largest = {((int) snapshot.child("0_No_Allocation").getChildrenCount()), ((int) snapshot.child("Bract_Mosaic").getChildrenCount()),
                        ((int) snapshot.child("Bunchy_Top").getChildrenCount()), ((int) snapshot.child("CMV").getChildrenCount()),
                        ((int) snapshot.child("Gen_Mosaic").getChildrenCount()), ((int) snapshot.child("SCMV").getChildrenCount())};
                int temp = 0;
                for (int number : largest) {
                    if (number > temp) {
                        temp = number;
                    }
                }
                if (snapshot.exists() && temp != dbSize) {
                    Log.d(TAG, "onDataChange: " + diseaseDBModels.size() + "/" + snapshot.getChildrenCount());
                    for (DiseaseDBModel disease : diseaseDBModels) {
                        dbHelper.clear(disease);
//                      Clear table
                    }


                    Log.d(TAG, "onDataChange: TABLE cleared, ready to be repopulated");
                    for (int i = 0; i != currentDbSize; i++) {

                        if (snapshot.child("0_No_Allocation").child(String.valueOf(i)).getValue() != null) {
                            stringVal_No_Allocation = Objects.requireNonNull(snapshot.child("0_No_Allocation").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_No_Allocation = "NULL";
                        }
                        if (snapshot.child("Bract_Mosaic").child(String.valueOf(i)).getValue() != null) {
                            stringVal_Bract_Mosaic = Objects.requireNonNull(snapshot.child("Bract_Mosaic").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_Bract_Mosaic = "NULL";
                        }
                        if (snapshot.child("Bunchy_Top").child(String.valueOf(i)).getValue() != null) {
                            stringVal_Bunchy_Top = Objects.requireNonNull(snapshot.child("Bunchy_Top").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_Bunchy_Top = "NULL";
                        }
                        if (snapshot.child("CMV").child(String.valueOf(i)).getValue() != null) {
                            stringVal_CMV = Objects.requireNonNull(snapshot.child("CMV").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_CMV = "NULL";
                        }
                        if (snapshot.child("Gen_Mosaic").child(String.valueOf(i)).getValue() != null) {
                            stringVal_Gen_Mosaic = Objects.requireNonNull(snapshot.child("Gen_Mosaic").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_Gen_Mosaic = "NULL";
                        }
                        if (snapshot.child("SCMV").child(String.valueOf(i)).getValue() != null) {
                            stringVal_SCMV = Objects.requireNonNull(snapshot.child("SCMV").child(String.valueOf(i)).getValue()).toString();
                        } else {
                            stringVal_SCMV = "NULL";
                        }
                        Log.d(TAG, "onDataChange: " + stringVal_No_Allocation);
                        addToDiseaseDb(i);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void welcomeScreen() {
        Intent intent = new Intent(this, WelcomeScreen.class);
        startActivity(intent);
    }


    private void addToDiseaseDb(int columnID) {
        DiseaseDBModel dbModel;
        try {
            dbModel = new DiseaseDBModel(columnID, stringVal_No_Allocation, stringVal_Bract_Mosaic, stringVal_Bunchy_Top, stringVal_CMV, stringVal_Gen_Mosaic, stringVal_SCMV);
            Log.d(TAG, "addToDiseaseDb: " + dbModel.getNo_Allocation());
        } catch (Exception e) {
            dbModel = new DiseaseDBModel(columnID, "NULL", "NULL", "NULL", "NULL", "NULL", "NULL");
        }
        DiseaseSymptomsDbHelper dbHelper = new DiseaseSymptomsDbHelper(this);
        boolean success = dbHelper.addOneSymptom(dbModel);
        if (!success)
            Toast.makeText(this, "Failed to update local database", Toast.LENGTH_SHORT).show();
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
            switch (item.getItemId()) {
                case R.id.userName:
                    openAccountDetails();
                    return true;
                case R.id.settings:
                    openSettings();
                    return true;
                case R.id.about:
                    openAboutActivity();
                    return true;
                case R.id.logout:
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
        Intent diagnose  = new Intent(this, DetectorActivity.class);
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

    //save user info to shared prefs(store locally)

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
