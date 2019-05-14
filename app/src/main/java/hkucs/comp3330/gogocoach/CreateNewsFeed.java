package hkucs.comp3330.gogocoach;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Intent;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class CreateNewsFeed extends  Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Button resetButton;
    private Button submitButton;
    private DatabaseReference mRootRef;
    private EditText topic;
    private EditText content;
    private View view;


    private Button btnChoose;
    private ImageView imageView;
    private Uri filePath;
    private String imageUrl;
    String userId;
    //Firebase
    StorageReference storageReference;
    private Map<String, Object> newsData = new HashMap<>();
    private final int PICK_IMAGE_REQUEST = 71;

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

        btnChoose = view.findViewById(R.id.btnChoose);
        imageView = view.findViewById(R.id.newsImage);
        storageReference = FirebaseStorage.getInstance().getReference();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId=mFirebaseUser.getUid();
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


                if(filePath != null)
                {
                    final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful() && task != null) {
                                imageUrl = task.getResult().getDownloadUrl().toString();

                                newsData.put("imageUrl",imageUrl);
                                mRootRef.child("newsFeed").child(userId).child(topic.getText().toString()).setValue(newsData);


                                //storageReference.getDownloadUrl();
                                Fragment fragment = new NewsFeedFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.commit();



                            }
                        }
                    });

                }
                else {

                    mRootRef.child("newsFeed").child(userId).child(topic.getText().toString()).setValue(newsData);


                    //storageReference.getDownloadUrl();
                    Fragment fragment = new NewsFeedFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.commit();
                }


            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic.setText("");
                content.setText("");
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

}
