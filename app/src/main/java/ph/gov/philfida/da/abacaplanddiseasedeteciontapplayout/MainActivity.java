package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AssessmentActivity;

public class MainActivity extends AppCompatActivity {
    TextView username, account_type;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignIds();
        getDBDetails();
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
                    username.setText(firstName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void assignIds() {
        username = findViewById(R.id.userName);
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
}
