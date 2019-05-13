package hkucs.comp3330.gogocoach;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

import hkucs.comp3330.gogocoach.firebase.Profile;

public class DisplayProfileFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private View view;
    private Profile currentProfile = new Profile();
    private String userId;
    private String username;
    private ImageView profileIcon;
    private String photoUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_display_profile, container, false);

        profileIcon = (ImageView) view.findViewById(R.id.profile_icon);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // get args
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
            photoUrl = bundle.getString("photoUrl");
            if(userId.equals(mFirebaseUser.getUid())){
                // display self profile
                // can edit, not chat
                view.findViewById(R.id.edit_fab).setVisibility(View.VISIBLE);
                view.findViewById(R.id.message_fab).setVisibility(View.GONE);
            }else{
                // display others profile
                // can chat, not edit
                view.findViewById(R.id.edit_fab).setVisibility(View.GONE);
                view.findViewById(R.id.message_fab).setVisibility(View.VISIBLE);
            }
        }else{
            // display self profile
            userId = mFirebaseUser.getUid();
            photoUrl = mFirebaseUser.getPhotoUrl().toString();
            // can edit, not chat
            view.findViewById(R.id.edit_fab).setVisibility(View.VISIBLE);
            view.findViewById(R.id.message_fab).setVisibility(View.GONE);
        }
        // set avatar
        (new Thread(new Runnable(){
            @Override
            public void run() {
                final Bitmap avatar = getCroppedBitmap(Bitmap.createScaledBitmap(loadImageFromNetwork(photoUrl), 500, 500, true));
                profileIcon.post(new Runnable(){
                    @Override
                    public void run() {
                        profileIcon.setImageBitmap(avatar);
                    }
                });
            }
        })).start();

        // get database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileRef = mDatabase.child("profile").child(userId);


        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){
                    Log.d("myTest", "profile exists");
                    currentProfile = dataSnapshot.getValue(Profile.class);
                    username = currentProfile.name;
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
                Bundle arguments = new Bundle();
                arguments.putString("receiver", userId);
                arguments.putString("receiverName", username);
                arguments.putString("receiverPhotoUrl", photoUrl);
                fragment.setArguments(arguments);
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
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        return view;
    }

    public Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream
                    ((InputStream) new URL(url).getContent());
            return bitmap;
        } catch(
                Exception e)

        {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}