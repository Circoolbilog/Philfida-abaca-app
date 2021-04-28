package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DataBaseHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SymptomModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.Symptom;
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
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private String userID;
    String firstName, lastName, middleName, email, birthday, permAdd, occupation, institution;
    private String symptomName;
    private boolean Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;
    private String stringVal_Bract_Mosaic, stringVal_Bunchy_Top, stringVal_CMV, stringVal_Gen_Mosaic, stringVal_SCMV;
    ArrayList<SymptomModel> symptomModelArrayList;

    public static final String SHARED_PREFS = "USER_DATA";
    public static final String EMAIL = "EMAIL";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String MIDDLE_NAME = "MIDDLE_NAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String PERM_ADD = "PERM_ADD";
    public static final String OCCUPATION = "OCCUPATION";
    public static final String INSTITUTION = "INSTITUTION";
    SQLiteDatabase symptomDB;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeScreen();
        loadUserData();
        getDBDetails();
        saveUserData();
        downloadSymptomMap();
        createLocalDB();
    }

    private void welcomeScreen() {
        Intent intent = new Intent(this, WelcomeScreen.class);
        startActivity(intent);
    }

    private void createLocalDB() {
    }

    private void downloadSymptomMap() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        List<SymptomModel> everSymptom = dataBaseHelper.getSymptoms();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference2 = FirebaseDatabase.getInstance().getReference("Symptoms");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                IDK WHY but this part says no parsable data
//                Symptom symptom = snapshot.getValue(Symptom.class);

                if (snapshot.exists() && snapshot.getChildrenCount() != everSymptom.size()) {
                    for (SymptomModel symptom : everSymptom) {
                        dataBaseHelper.clear(symptom);
//                      Clear table
                    }
                    Log.d(TAG, "onDataChange: TABLE is cleared and ready to be repopulated");
                    for (long i = 0; i < snapshot.getChildrenCount(); i++) {
//                       symptomName = snapshot.child("0").child("SymptomName").getValue().toString();
                        symptomName = snapshot.child(String.valueOf(i)).child("SymptomName").getValue().toString();
                        stringVal_Bract_Mosaic = snapshot.child(String.valueOf(i)).child("Bract_Mosaic").getValue().toString();
                        stringVal_Bunchy_Top = snapshot.child(String.valueOf(i)).child("Bunchy_Top").getValue().toString();
                        stringVal_CMV = snapshot.child(String.valueOf(i)).child("CMV").getValue().toString();
                        stringVal_Gen_Mosaic = snapshot.child(String.valueOf(i)).child("Gen_Mosaic").getValue().toString();
                        stringVal_SCMV = snapshot.child(String.valueOf(i)).child("SCMV").getValue().toString();
                        Bract_Mosaic = Boolean.parseBoolean(stringVal_Bract_Mosaic);
                        Bunchy_Top = Boolean.parseBoolean(stringVal_Bunchy_Top);
                        CMV = Boolean.parseBoolean(stringVal_CMV);
                        Gen_Mosaic = Boolean.parseBoolean(stringVal_Gen_Mosaic);
                        SCMV = Boolean.parseBoolean(stringVal_SCMV);
                        Log.d(TAG, "onDataChange, Populating db: " + symptomName + " pos: " + i + " out of: " + snapshot.getChildrenCount());
                        addToLocalDB();
                    }
                } else if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange: db does need to be updated");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void addToLocalDB() {
        SymptomModel symptomModel;
        try {
            symptomModel = new SymptomModel(0, symptomName, Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV);
//            Toast.makeText(this, symptomModel.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Toast.makeText(this, "Error somehow?", Toast.LENGTH_SHORT).show();
            symptomModel = new SymptomModel(0, "NULL", false, false, false, false, false);
        }
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        boolean success = dataBaseHelper.addOne(symptomModel);

//        Toast.makeText(this, "success: "+success, Toast.LENGTH_SHORT).show();
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        Menu menu = navigationView.getMenu();
        MenuItem userNameInNav = menu.findItem(R.id.userName);
        userNameInNav.setTitle(firstName + " " + lastName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
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

    private void getDBDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
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
        saveUserData();
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
}
