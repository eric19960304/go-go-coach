package hkucs.comp3330.gogocoach;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hkucs.comp3330.gogocoach.firebase.Profile;

public class DisplayProfileFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private View view;
    private Profile currentProfile = new Profile();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileRef = mDatabase.child("profile").child(mFirebaseUser.getUid());

        view = inflater.inflate(R.layout.fragment_display_profile, container, false);

        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){
                    Log.d("myTest", "profile exists");
                    currentProfile = dataSnapshot.getValue(Profile.class);

                    view.findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_content_empty).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_content).setVisibility(View.VISIBLE);

                    ((TextView) view.findViewById(R.id.available_class)).setText(currentProfile.sportTypes);
                    ((TextView) view.findViewById(R.id.bio)).setText(currentProfile.bio);
                    ((TextView) view.findViewById(R.id.contact_number)).setText(currentProfile.contactNumber);
                    ((TextView) view.findViewById(R.id.email)).setText(currentProfile.email);
                }else{
                    Log.d("myTest", "profile not exists");
                    view.findViewById(R.id.loadingSpinner).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_content).setVisibility(View.GONE);
                    view.findViewById(R.id.profile_content_empty).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting data failed, log a message
                Log.d("myTest", "loadPost:onCancelled", databaseError.toException());
            }
        };
        profileRef.addValueEventListener(profileListener);

        FloatingActionButton message_fab = view.findViewById(R.id.message_fab);
        message_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ChatFragment();

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

        FloatingActionButton edit_fab = view.findViewById(R.id.edit_fab);
        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("currentProfile", currentProfile);
                startActivity(intent);
            }
        });

        return view;
    }

}