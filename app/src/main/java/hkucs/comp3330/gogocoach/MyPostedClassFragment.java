package hkucs.comp3330.gogocoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class MyPostedClassFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private View view;
    private ArrayList<Classes> classesArray;

    @Override
    public void onResume(){
        super.onResume();

        ((MainActivity) getActivity()).setActionBarTitle("Posted Classes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userId = "";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // others user classes
            userId = bundle.getString("userId");
        }else{
            // self classes
            userId = mFirebaseUser.getUid();
        }

        DatabaseReference ref = mDatabase.child("classes").child(userId);

        view = inflater.inflate(R.layout.fragment_my_class_list, container, false);

        classesArray = new ArrayList<Classes>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){

                        for (DataSnapshot coachClassesSnapshot: dataSnapshot.getChildren()) {
                            Log.d("coachClassesSnapshot", "exists");
                            Classes c = (Classes) coachClassesSnapshot.getValue(Classes.class);
                            //Log.d("coachClassesSnapshot", c.id +c.price + c.location);

                            classesArray.add(c);
                        }

                    MyAdapter adapter = new MyAdapter(view.getContext(), classesArray);

                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
                        @Override
                        public void onItemClick(View view , Classes c){
                            //Log.d("position: ", String.valueOf(position));

                            Intent intent = new Intent(getActivity(), DetailClassActivity.class);
                            intent.putExtra("classes", c);
                            startActivityForResult(intent, 1);
                        }
                    });

                }else{
                    Log.d("class database", "not exists");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting data failed, log a message
                Log.d("class database", "loadClasses:onCancelled", databaseError.toException());
            }
        });

        //ref.addValueEventListener(profileListener);

        //Log.d("classesArray: ", String.valueOf(classesArray.size()));

        String[] s = new String[3];
        s[0] = "$100";
        s[1] = "$200";
        s[2] = "$300";

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myTest", "resultCode:"+resultCode);
        if (resultCode == Activity.RESULT_OK) {

            final String action = data.getExtras().getString("action");
            Log.d("myTest", action);
            if(action.equals(DetailClassActivity.ACTION_BROWSE_PROFILE)){
                Log.d("myTest", "ACTION_BROWSE_PROFILE");
                String userId = data.getExtras().getString("userId");
                String photoUrl = data.getExtras().getString("photoUrl");

                Fragment fragment = new DisplayProfileFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("photoUrl", photoUrl);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, fragment);

                fragmentTransaction.commit();
            }
            if(action.equals(DetailClassActivity.ACTION_BOOKING)){
                final Classes classToBook = (Classes) data.getExtras().getSerializable("classToBook");
                // TODO
            }
        }
    }
}