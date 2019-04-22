package hkucs.comp3330.gogocoach;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PostClassFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button addClassButton;
    private Button resetButton;
    private EditText className;
    private EditText description;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        View view =inflater.inflate(R.layout.fragment_post_class, container, false);
        addClassButton = (Button) view.findViewById(R.id.addClassButton);
        resetButton = (Button) view.findViewById(R.id.resetButton);
        className = (EditText) view.findViewById(R.id.className);
        description = (EditText) view.findViewById(R.id.description);



        return inflater.inflate(R.layout.fragment_post_class, container, false);
    }
}