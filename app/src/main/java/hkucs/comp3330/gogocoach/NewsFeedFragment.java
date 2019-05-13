package hkucs.comp3330.gogocoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hkucs.comp3330.gogocoach.firebase.NewsFeed;

import static android.app.Activity.RESULT_OK;

public class NewsFeedFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private View view;
    private ArrayList<NewsFeed> newsArray;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("newsFeed");

        view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        newsArray = new ArrayList<NewsFeed>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot coachesSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot coachClassesSnapshot : coachesSnapshot.getChildren()) {
                            NewsFeed n = (NewsFeed) coachClassesSnapshot.getValue(NewsFeed.class);

                            newsArray.add(n);

                        }
                    }
                    Collections.sort(newsArray);
                    NewsAdapter adapter = new NewsAdapter(view.getContext(), newsArray);
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, NewsFeed n) {
                            //Log.d("position: ", String.valueOf(position));
                            String userId = n.id;
                            String photoUrl = n.photoUrl;

                            Fragment fragment = new DisplayProfileFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", userId);
                            bundle.putString("photoUrl", photoUrl);
                            fragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.frame_layout, fragment);

                            fragmentTransaction.commit();
                        }
                    });

                } else {
                    Log.d("class database", "not exists");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting data failed, log a message
                Log.d("class database", "loadClasses:onCancelled", databaseError.toException());
            }
        });

        view.findViewById(R.id.newPost).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Fragment fragment = new CreateNewsFeed();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();

            }
        });

        return view;
    }


    // return inflater.inflate(R.layout.fragment_news_feed, container, false);
}