package hkucs.comp3330.gogocoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
public class CreateNewsFeed extends  Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button resetButton;
    private Button submitButton;
    private DatabaseReference mRootRef;
    private EditText topic;
    private EditText content;
    private View view;

    @Override
    public void onResume(){
        super.onResume();

        ((MainActivity) getActivity()).setActionBarTitle("Post News Feed");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        view = inflater.inflate(R.layout.create_news_feed, container, false);

        resetButton = view.findViewById(R.id.resetButton);
        submitButton = view.findViewById(R.id.addClassButton);
        topic= view.findViewById(R.id.newsTopic);
        content= view.findViewById(R.id.newsContent);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> newsData = new HashMap<>();
                String userId=mFirebaseUser.getUid();
                String name=mFirebaseUser.getDisplayName();
                String photoUrl = mFirebaseUser.getPhotoUrl().toString();
                newsData.put("topic", topic.getText().toString());
                newsData.put("content", content.getText().toString());
                newsData.put("id",userId);
                newsData.put("name",name);
                newsData.put("photoUrl",photoUrl);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                newsData.put("time",formatter.format(date));
                mRootRef.child("newsFeed").child(userId).child(topic.getText().toString()).setValue(newsData);

                Fragment fragment = new NewsFeedFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic.setText("");
                content.setText("");
            }
        });

        return view;
    }





}
