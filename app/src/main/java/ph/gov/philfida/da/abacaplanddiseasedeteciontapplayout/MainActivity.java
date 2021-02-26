package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AboutApp;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AccountDetails;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AssessmentActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseIndex;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.Map;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String firstName, lastName, middleName, email, birthday, permAdd, occupation, institution;

    public static final String SHARED_PREFS = "USER_DATA";
    public static final String EMAIL = "EMAIL";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String MIDDLE_NAME = "MIDDLE_NAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String PERM_ADD = "PERM_ADD";
    public static final String OCCUPATION = "OCCUPATION";
    public static final String INSTITUTION = "INSTITUTION";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUserData();
        getDBDetails();
        saveUserData();
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
        if (user.getPhotoUrl() != null){
            Glide.with(MainActivity.this)
                    .load(user.getPhotoUrl())
                    .override(400,400)
                    .into(navPicture );
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
                        return  true;
                }
                return false;
            }
        });
    }

    private void openSettings() {
        Intent intent = new Intent(this,SettingsActivity.class);
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
