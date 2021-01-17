package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AccountDetails;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AssessmentActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseIndex;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    TextView username, account_type;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String firstName,lastName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDBDetails();
    }

    private void setUpNavDrawer() {
        navigationView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // get menu from navigationView
        Menu menu = navigationView.getMenu();
        MenuItem userNameInNav = menu.findItem(R.id.userName);
        userNameInNav.setTitle(firstName + " "+ lastName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.userName:
                        openAccountDetails();
                        return true;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this,"Settings selected",Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.logout:
                        logOut();
                }
                return false;
            }
        });
    }
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        //SaveSharedPreference.clearUsername(this);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDBDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null){
                    firstName = userProfile.firstName;
                    lastName = userProfile.lastName;
                    setUpNavDrawer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openDiagnoseActivity(View view) {
        Intent diagnose = new Intent(this,DetectorActivity.class);
        startActivity(diagnose);
    }

    public void openAssessmentActivity(View view){
        Intent assessment = new Intent(this, AssessmentActivity.class);
        startActivity(assessment);
    }
    public void openAccountDetails() {
        Intent intent = new Intent(MainActivity.this, AccountDetails.class);
        startActivity(intent);
    }

    public void openDiseaseIndex(View view) {
        Intent intent = new Intent(MainActivity.this, DiseaseIndex.class);
        startActivity(intent);
    }
}
