package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Login;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.User;

public class AccountDetails extends AppCompatActivity {
    TextView name, emailAdd, birthday, permanentAdd, occupation, institution;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    String lastNameS, firstNameS, middleNameS, emailAddS, birthdayS, permanentAddS, occupationS, institutionS;
    public static final String SHARED_PREFS = "USER_DATA";
    public static final String EMAIL = "EMAIL";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String MIDDLE_NAME = "MIDDLE_NAME";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String PERM_ADD = "PERM_ADD";
    public static final String OCCUPATION = "OCCUPATION";
    public static final String INSTITUTION = "INSTITUTION";
    boolean editMode;
    Button editProfile;
    ImageView cardBG;
    CardView profilePicture;
    EditText editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        assignIDS();
        getDBDetails();
        loadUserData();
        updateViews();
        layoutAdjustments();
        if (editMode){
            enterEditMode();
        }
    }
    private void layoutAdjustments() {
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    cardBG.setVisibility(View.GONE);
                    profilePicture.setVisibility(View.GONE);
                } else {
                    profilePicture.setVisibility(View.VISIBLE);
                    cardBG.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void enterEditMode() {
        editProfile.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        editName.setVisibility(View.VISIBLE);
        editName.setText(firstNameS + " "+lastNameS+" ");
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        lastNameS = sharedPreferences.getString(LAST_NAME, "");
        firstNameS = sharedPreferences.getString(FIRST_NAME, "");
        middleNameS = sharedPreferences.getString(MIDDLE_NAME, "");
        emailAddS = sharedPreferences.getString(EMAIL, "");
        birthdayS = sharedPreferences.getString(BIRTHDAY, "");
        permanentAddS = sharedPreferences.getString(PERM_ADD, "");
        occupationS = sharedPreferences.getString(OCCUPATION, "");
        institutionS = sharedPreferences.getString(INSTITUTION, "");
    }

    private void updateViews() {
        name.setText(firstNameS + " ");
        if (!middleNameS.equals("N/A")) {
            name.append(middleNameS + " ");
        }
        name.append(lastNameS);
        emailAdd.append(emailAddS);
        birthday.append(birthdayS);
        permanentAdd.append(permanentAddS);
        occupation.append(occupationS);
        institution.append(institutionS);
    }

    private void assignIDS() {
        cardBG = findViewById(R.id.cardBG);
        editName = findViewById(R.id.editLastName);
        profilePicture = findViewById(R.id.cardView);
        editProfile = findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterEditMode();
            }
        });
        name = findViewById(R.id.lastName);
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
                    lastNameS = userProfile.lastName;
                    firstNameS = userProfile.firstName;
                    middleNameS = userProfile.middleName;
                    emailAddS = userProfile.email;
                    birthdayS = userProfile.birthday;
                    permanentAddS = userProfile.permanentAddress;
                    occupationS = userProfile.occupation;
                    institutionS = userProfile.institution;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountDetails.this, "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}