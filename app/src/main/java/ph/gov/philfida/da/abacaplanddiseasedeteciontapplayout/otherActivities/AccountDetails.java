package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Login;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.User;

public class AccountDetails extends AppCompatActivity {
    private static final String TAG = "AccountDetails";
    TextView name, labelName, lastName, firstName, middleName, emailAdd, birthday, permanentAdd, occupation, institution;
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
    String PROFILE_IMAGE_URL = null;
    Bitmap profilePicBitmap;
    boolean editMode;
    Button editProfile,logout,save;
    ImageView cardBG,profilePicture;
    CardView cardView;
    EditText editLastName,editFirstName, editMiddleName, editEmail, editBirthday, editPermanentAdd, editOccupation, editInstitution;
    int TAKE_IMAGE_CODE = 10001;

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
    //make sure that the layout looks good even when typing
    private void layoutAdjustments() {
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    cardBG.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                } else {
                    cardView.setVisibility(View.VISIBLE);
                    cardBG.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //exit edit mode and save changes made.(update database)

    private void exitEditView(){
        editMode = false;
        editProfile.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        labelName.setVisibility(View.VISIBLE);
        lastName.setVisibility(View.GONE);
        firstName.setVisibility(View.GONE);
        middleName.setVisibility(View.GONE);
        editLastName.setVisibility(View.GONE);
        editFirstName.setVisibility(View.GONE);
        editMiddleName.setVisibility(View.GONE);
        emailAdd.setVisibility(View.VISIBLE);
        editEmail.setVisibility(View.GONE);
        birthday.setVisibility(View.VISIBLE);
        editBirthday.setVisibility(View.GONE);
        permanentAdd.setVisibility(View.VISIBLE);
        editPermanentAdd.setVisibility(View.GONE);
        occupation.setVisibility(View.VISIBLE);
        editOccupation.setVisibility(View.GONE);
        institution.setVisibility(View.VISIBLE);
        editInstitution.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        cardView.setClickable(false);
    }

    //show edit text to enable the user to edit info

    private void enterEditMode() {
        editMode = true;
        editProfile.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        labelName.setVisibility(View.GONE);
        lastName.setVisibility(View.VISIBLE);
        firstName.setVisibility(View.VISIBLE);
        middleName.setVisibility(View.VISIBLE);
        editLastName.setVisibility(View.VISIBLE);
        editLastName.setText(lastNameS);
        editFirstName.setVisibility(View.VISIBLE);
        editMiddleName.setVisibility(View.VISIBLE);
        editFirstName.setText(firstNameS);
        editMiddleName.setText(middleNameS);
        emailAdd.setVisibility(View.GONE);
        editEmail.setVisibility(View.VISIBLE);
        editEmail.setText(emailAddS);
        birthday.setVisibility(View.GONE);
        editBirthday.setVisibility(View.VISIBLE);
        editBirthday.setText(birthdayS);
        permanentAdd.setVisibility(View.GONE);
        editPermanentAdd.setVisibility(View.VISIBLE);
        editPermanentAdd.setText(permanentAddS);
        occupation.setVisibility(View.GONE);
        editOccupation.setVisibility(View.VISIBLE);
        editOccupation.setText(occupationS);
        institution.setVisibility(View.GONE);
        editInstitution.setVisibility(View.VISIBLE);
        editInstitution.setText(institutionS);
        logout.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnImageClick();
            }
        });
    }

    //open camera to change profile picture of user

    private void handleOnImageClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    //get image taken from camera and pass it to handleUpload method as a Bitmap

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePicture.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    //upload profile picture to the database.

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("ProfilePictures")
                .child(userID+".jpeg");
        reference.putBytes(byteArrayOutputStream.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ",e.getCause() );
            }
        });
    }

    //get the url for the profile picture of user
    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: "+uri);
                setProfileUrl(uri);
            }
        });
    }

    //upload the picture taken to the firebase database
    private void setProfileUrl(Uri uri){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AccountDetails.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountDetails.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //laod user data from shared preferences (locally stored)

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

    //update views,(text, images etc)

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
        profilePicture.setImageBitmap(profilePicBitmap);
        if (user.getPhotoUrl() != null){
            Glide.with(AccountDetails.this)
                    .load(user.getPhotoUrl())
                    .override(400,400)
                    .into(profilePicture);
        }
    }

    //this is where views are assigned with their ids.

    private void assignIDS() {
        editFirstName = findViewById(R.id.editFistName);
        editMiddleName = findViewById(R.id.editMiddleName);
        editEmail = findViewById(R.id.editEmailAdd);
        editBirthday = findViewById(R.id.editBirthday);
        editPermanentAdd = findViewById(R.id.editPermanentAdd);
        editOccupation = findViewById(R.id.editOccupation);
        editInstitution =findViewById(R.id.editInstitution);
        profilePicture = findViewById(R.id.userAvatar);
        cardBG = findViewById(R.id.cardBG);
        editLastName = findViewById(R.id.editLastName);
        cardView = findViewById(R.id.cardView);
        editProfile = findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterEditMode();
            }
        });
        name = findViewById(R.id.lastName);
        labelName = findViewById(R.id.labelName);
        lastName = findViewById(R.id.labelLastName);
        firstName = findViewById(R.id.labelFirstName);
        middleName = findViewById(R.id.labelMiddleName);
        emailAdd = findViewById(R.id.emailAdd);
        birthday = findViewById(R.id.birthday);
        permanentAdd = findViewById(R.id.permanentAddress);
        occupation = findViewById(R.id.occupation);
        institution = findViewById(R.id.institution);
        logout = findViewById(R.id.logout);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
                updateViews();
                exitEditView();
            }
        });
    }

    //get the user info from firebase database

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
                if (user.getPhotoUrl()!= null){
                    Glide.with(AccountDetails.this)
                            .load(user.getPhotoUrl())
                            .into(profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountDetails.this, "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });
    }
//
//log out the user
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

    @Override
    public void onBackPressed() {
        if (editMode){
            exitEditView();
            return;
        }
        super.onBackPressed();
    }

    //send edited info to the database
    //TODO: Validate user info

    public void updateDatabase(){
        User user = new User(lastNameS,middleNameS,firstNameS,birthdayS,emailAddS,permanentAddS,occupationS,institutionS);
       // reference.child(userID).updateChildren();
    }

}