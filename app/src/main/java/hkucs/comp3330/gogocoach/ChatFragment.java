package hkucs.comp3330.gogocoach;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hkucs.comp3330.gogocoach.firebase.Message;

public class ChatFragment extends Fragment {
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messengerTextView;
        CircleImageView messengerImageView;
        TextView text_message_time;

        public MessageViewHolder(View v) {
            super(v);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            text_message_time = (TextView) itemView.findViewById(R.id.text_message_time);

        }
    }
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static final String TAG = "ChatFragment";
    public String UESRCHAT = "userChat";
    public String MESSAGES_CHILD;
    public String MESSAGES = "messages";
    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final int REQUEST_IMAGE = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private String mUsername;
    private String mPhotoUrl;
    private String mReceiverPhotoUrl;
    private String mUserId;
    private String mReceiverUserId;
    private String mReceiverUserName;
    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;


    public static String uidCompareTo(String uid1, String uid2){
        if(uid1.compareTo(uid2) >= 0){
            return uid1+uid2;
        }
        return uid2+uid1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    Message tempMessage = new Message(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL, mUserId, dateFormat.format(new Date()));
                    mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(MESSAGES).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle arguments = this.getArguments();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = arguments.getString("senderName");
        mUserId = mFirebaseUser.getUid();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mReceiverUserId = arguments.getString("receiver");
        mReceiverUserName = arguments.getString("receiverName");
        //ChatRoom ID Initialization
        MESSAGES_CHILD = uidCompareTo(mUserId, mReceiverUserId);
        mReceiverPhotoUrl = arguments.getString("receiverPhotoUrl");
        Log.d(TAG, "Open ChatFragment with receiverUid = "+mReceiverUserId);
        mPhotoUrl = arguments.getString("senderPhotoUrl");
        //ChatRoom Initialization
        mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(mUserId).child("name").setValue(mUsername);
        mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(mUserId).child("icon").setValue(mPhotoUrl);
        mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(mReceiverUserId).child("name").setValue(mReceiverUserName);
        mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(mReceiverUserId).child("icon").setValue(mReceiverPhotoUrl);
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        SnapshotParser<Message> parser = new SnapshotParser<Message>() {
            @Override
            public Message parseSnapshot(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    message.setId(dataSnapshot.getKey());
                }
                return message;
            }
        };
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(MESSAGES);
        //DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messagesRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                Message model = getItem(position);
                if (model.getUid().equals(mUserId)) {
                    return VIEW_TYPE_MESSAGE_SENT;
                } else {
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
                    return new MessageViewHolder(inflater.inflate(R.layout.item_message_received, viewGroup, false));
                }
                return new MessageViewHolder(inflater.inflate(R.layout.item_message_sent, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            Message message) {
                if (message.getName() != null) {
                    viewHolder.messageTextView.setText(message.getName());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                }
                if (message.getTime() != null) {
                    viewHolder.text_message_time.setText(message.getTime());
                    viewHolder.text_message_time.setVisibility(TextView.VISIBLE);
                }

                viewHolder.messengerTextView.setText(message.getText());
                if (message.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ChatFragment.this)
                            .load(message.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }

            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSendButton = (Button) view.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                if(mMessageEditText.getText().toString().replace(" ","").equals("")){
                    //No content, do not send
                }else{
                    Message message = new
                            Message(mMessageEditText.getText().toString(),
                            mUsername,
                            mPhotoUrl,
                            null /* no image */,
                            mUserId,
                            dateFormat.format(new Date()));
                    mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child(MESSAGES)
                            .push().setValue(message);
                    //Update last message logic
                    mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child("lastMessage").setValue(message.getText());
                    mFirebaseDatabaseReference.child(UESRCHAT).child(MESSAGES_CHILD).child("lastUpdate").setValue(message.getTime());
                    mMessageEditText.setText("");
                }
            }
        });

        mAddMessageImageView = (ImageView) view.findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.

            }
        });
        return view;
    }
    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }
}