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

public class ClassListFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_class_list, container, false);

        String[] s = new String[3];
        s[0] = "$100";
        s[1] = "$200";
        s[2] = "$300";
        MyAdapter adapter = new MyAdapter(view.getContext(), s);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                //Log.d("position: ", String.valueOf(position));

                Intent intent = new Intent(getActivity(), DetailClassActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}