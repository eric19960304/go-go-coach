package hkucs.comp3330.gogocoach;

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

import hkucs.comp3330.gogocoach.firebase.UserChatItem;

public class ChatListFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUsername;
    private String mUserId;
    private View view;
    public static final String TAG = "ChatListFragment";
    private ArrayList<UserChatItem> chatArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("userChat");
        mUsername = mFirebaseUser.getDisplayName();
        mUserId = mFirebaseUser.getUid();
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chatArray = new ArrayList<UserChatItem>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.exists()){
                    //Log.d("classes database", "exists");
                    for (DataSnapshot c: dataSnapshot.getChildren()){
                        String chatroomId = c.getKey();
                        //Log.d(TAG, "chatroomId: "+chatroomId);
                        if(chatroomId.contains(mUserId)){
                            String receiverId = chatroomId.replace(mUserId, "");
                            //Log.d(TAG, "receiverId: "+receiverId);
                            String receiverName = c.child(receiverId).child("name").getValue(String.class);
                            //Log.d(TAG, "receiverName: "+receiverName);
                            String receiverPhotoUrl = c.child(receiverId).child("icon").getValue(String.class);
                            //Log.d(TAG, "receiverPhotoUrl: "+receiverPhotoUrl);
                            String lastMessage = c.child("lastMessage").getValue(String.class);
                            //Log.d(TAG, "lastMessage: "+lastMessage);
                            String lastUpdate = c.child("lastUpdate").getValue(String.class);
                            //Log.d(TAG, "lastUpdate: "+lastUpdate);
                            UserChatItem uci = new UserChatItem(receiverId, receiverName, receiverPhotoUrl, lastMessage, lastUpdate);
                            chatArray.add(uci);
                        } //chatArray.add(c.child(mUserId).getValue(String.class));
                    }
                    MyChatAdapter adapter = new MyChatAdapter(view.getContext(), chatArray);

                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new MyChatAdapter.OnItemClickListener(){
                        @Override
                        public void onItemClick(View view , UserChatItem c){
                            //Log.d("position: ", String.valueOf(position));
                            Fragment fragment = new ChatFragment();
                            Bundle arguments = new Bundle();
                            arguments.putString("receiver", c.getUid());
                            //arguments.putString("receiver", c.getName());
                            arguments.putString("receiverPhotoUrl", c.getPhotoUrl());
                            fragment.setArguments(arguments);
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, fragment);
                            fragmentTransaction.commit();
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
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("myTest", "resultCode:"+resultCode);

    }
}