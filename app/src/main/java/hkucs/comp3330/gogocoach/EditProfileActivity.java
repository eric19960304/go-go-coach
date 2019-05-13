package hkucs.comp3330.gogocoach;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hkucs.comp3330.gogocoach.firebase.Profile;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference profileRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userId = getIntent().getStringExtra("userId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        profileRef = mDatabase.child("profile").child(userId);

        Profile currentProfile = (Profile) getIntent().getSerializableExtra("currentProfile");
        if(currentProfile.sportTypes!=null){
            ((TextView) findViewById(R.id.sportTypesInput)).setText(currentProfile.sportTypes);
        }
        if(currentProfile.bio!=null) {
            ((TextView) findViewById(R.id.bioInput)).setText(currentProfile.bio);
        }
        if(currentProfile.email!=null){
            ((TextView) findViewById(R.id.emailInput)).setText(currentProfile.email);
        }
        if(currentProfile.contactNumber!=null){
            ((TextView) findViewById(R.id.contactNumberInput)).setText(currentProfile.contactNumber);
        }

        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String sportTypes = ((TextView) findViewById(R.id.sportTypesInput)).getText().toString();
                String bio = ((TextView) findViewById(R.id.bioInput)).getText().toString();
                String name = mFirebaseUser.getDisplayName();
                String email = ((TextView) findViewById(R.id.emailInput)).getText().toString();
                String contact = ((TextView) findViewById(R.id.contactNumberInput)).getText().toString();

                profileRef.setValue(new Profile(sportTypes, bio, name, email, contact));

                finish();
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
