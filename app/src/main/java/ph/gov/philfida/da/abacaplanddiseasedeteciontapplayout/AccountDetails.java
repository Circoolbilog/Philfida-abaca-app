package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AccountDetails extends AppCompatActivity {
    TextView lastName, firstName, middleName, emailAdd, birthday, permanentAdd, occupation, institution;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        assignIDS();
        getDBDetails();
    }

    private void assignIDS() {
        lastName = findViewById(R.id.lastName);
        firstName = findViewById(R.id.firstName);
        middleName = findViewById(R.id.middleName);
        emailAdd = findViewById(R.id.emailAdd);
        birthday = findViewById(R.id.birthday);
        permanentAdd = findViewById(R.id.permanentAddress);
        occupation = findViewById(R.id.occupation);
        institution = findViewById(R.id.institution);
    }

    private void getDBDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String lastNameS = userProfile.lastName;
                    String firstNameS = userProfile.firstName;
                    String middleNameS = userProfile.middleName;
                    String emailAddS = userProfile.email;
                    String birthdayS = userProfile.birthday;
                    String permanentAddS = userProfile.permanentAddress;
                    String occupationS = userProfile.occupation;
                    String institutionS = userProfile.institution;
                    lastName.append(lastNameS);
                    firstName.append(firstNameS);
                    middleName.append(middleNameS);
                    emailAdd.append(emailAddS);
                    birthday.append(birthdayS);
                    permanentAdd.append(permanentAddS);
                    occupation.append(occupationS);
                    institution.append(institutionS);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountDetails.this,"Something Wrong Happened",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        //SaveSharedPreference.clearUsername(this);
        finish();
    }
}