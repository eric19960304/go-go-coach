package hkucs.comp3330.gogocoach;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PostClassFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button addClassButton;
    private Button resetButton;
    private EditText className;
    private EditText description;
    private EditText noOfPeople;
    private EditText type;
    private EditText price;
    private EditText location;
    private EditText time;

    private DatabaseReference mRootRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        View view =inflater.inflate(R.layout.fragment_post_class, container, false);
        addClassButton = (Button) view.findViewById(R.id.addClassButton);
        resetButton = (Button) view.findViewById(R.id.resetButton);
        className = (EditText) view.findViewById(R.id.className);
        description = (EditText) view.findViewById(R.id.description);
        noOfPeople = (EditText) view.findViewById(R.id.people);
        type= (EditText) view.findViewById(R.id.type);
        time= (EditText) view.findViewById(R.id.time);
        location= (EditText) view.findViewById(R.id.location);
        price= (EditText) view.findViewById(R.id.price);



        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> classData = new HashMap<>();
                String userId=mFirebaseUser.getUid();
                String name=mFirebaseUser.getDisplayName();
                classData.put("location", location.getText().toString());
                classData.put("time", time.getText().toString());
                classData.put("description", description.getText().toString());
                classData.put("price", price.getText().toString());
                classData.put("type", type.getText().toString());
                classData.put("number", noOfPeople.getText().toString());
                classData.put("id",userId);
                classData.put("name",name);
                mRootRef.child("classes").child(userId).child(className.getText().toString()).setValue(classData);

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                className.setText("");
                description .setText("");
                noOfPeople.setText("");
                type.setText("");
                time.setText("");
                location.setText("");
                price.setText("");

            }
        });

        return view;
    }
}