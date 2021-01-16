package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDBDetails();
        setUpNavDrawer();
    }

    private void setUpNavDrawer() {
        navigationView = findViewById(R.id.navView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = MainActivity.this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.userName:
                        Toast.makeText(MainActivity.this,"Username selected",Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this,"Settings selected",Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
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
                    String lastName = userProfile.lastName;
                    String firstName = userProfile.firstName;
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
    public void openAccountDetails(View view) {
        Intent intent = new Intent(MainActivity.this, AccountDetails.class);
        startActivity(intent);
    }

    public void openDiseaseIndex(View view) {
        Intent intent = new Intent(MainActivity.this, DiseaseIndex.class);
        startActivity(intent);
    }
}
