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
    String PROFILE_IMAGE_URL = null;
    Bitmap profilePicBitmap;
    boolean editMode;
    Button editProfile;
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
    private void exitEditView(){

    }
    private void enterEditMode() {
        editProfile.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        editLastName.setVisibility(View.VISIBLE);
        editLastName.setText(lastNameS);
        editFirstName.setVisibility(View.VISIBLE);
        editMiddleName.setVisibility(View.VISIBLE);
        editFirstName.setText(firstNameS);
        editMiddleName.setText(middleNameS);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnImageClick();
            }
        });
    }

    private void handleOnImageClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

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

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: "+uri);
                setProfileUrl(uri);
            }
        });
    }

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
        profilePicture.setImageBitmap(profilePicBitmap);
        if (user.getPhotoUrl() != null){
//            Glide.with(AccountDetails.this)
//                    .asBitmap()
//                    .load(user.getPhotoUrl())
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap,
//                                                    Transition<? super Bitmap> transition) {
//                            int w = bitmap.getWidth();
//                            int h = bitmap.getHeight();
//                            profilePicture.setImageBitmap(bitmap);
//                        }
//                    });
            Glide.with(AccountDetails.this)
                    .load(user.getPhotoUrl())
                    .override(400,400)
                    .into(profilePicture);
        }
    }

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
}