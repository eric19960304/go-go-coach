package hkucs.comp3330.gogocoach;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

import hkucs.comp3330.gogocoach.firebase.Classes;

public class ClassListFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private View view;
    private ArrayList<Classes> classesArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("classes");

        view = inflater.inflate(R.layout.fragment_class_list, container, false);

        classesArray = new ArrayList<Classes>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){
                    Log.d("classes database", "exists");
                    for (DataSnapshot coachesSnapshot: dataSnapshot.getChildren()) {
                        Log.d("coaches data", "exists");

                        for (DataSnapshot coachClassesSnapshot: coachesSnapshot.getChildren()) {
                            Log.d("coachClassesSnapshot", "exists");
                            Classes c = coachClassesSnapshot.getValue(Classes.class);
                            //Log.d("coachClassesSnapshot", c.id +c.price + c.location);

                            classesArray.add(c);
                        }
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
//                            intent.putExtra("className", c.className);
//                            intent.putExtra("description", c.description);
//                            intent.putExtra("id", c.id);
//                            intent.putExtra("location", c.location);
//                            intent.putExtra("name", c.name);
//                            intent.putExtra("number", c.number);
//                            intent.putExtra("price", c.price);
//                            intent.putExtra("time", c.time);
//                            intent.putExtra("type", c.type);
                            startActivity(intent);
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
}