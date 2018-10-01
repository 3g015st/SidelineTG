package com.example.benedictlutab.sidelinetg.modules.messages.chatRoomDetails;


import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Message;
import com.example.benedictlutab.sidelinetg.modules.messages.loadChatRooms.adapterChatRooms;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class chatDetailsActivity extends AppCompatActivity
{
    private String TASK_ID, TG_ID, TASK_NAME, TASKER_NAME, IMAGE_URL, TEMP_KEY;

    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvTaskerName) TextView tvTaskerName;

    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;

    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.btnBack) Button btnBack;

    @BindView(R.id.etMessage) EditText etMessage;

    private DatabaseReference databaseReference;

    private adapterChatMessages adapterChatMessages;
    private List<Message> messageList = new ArrayList<>();

    @BindView(R.id.rv_messages) RecyclerView rv_messages;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroomdetails_activity_chat_details);
        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        fetchExtras();
        setValues();

        // Set db reference listener to load all messages.
        initDbReferenceChildListeners();
    }

    private void fetchExtras()
    {
        Log.e("fetchExtras: ", "START!");
        TASK_ID  =  getIntent().getStringExtra("TASK_ID");
        TG_ID   =  getIntent().getStringExtra("TG_ID");
        TASK_NAME  =  getIntent().getStringExtra("TASK_NAME");
        TASKER_NAME   =  getIntent().getStringExtra("TASKER_NAME");
        IMAGE_URL  =  getIntent().getStringExtra("IMAGE_URL");
        Log.e("FETCH VALUES: ", TASK_ID +","+TG_ID+","+TASK_NAME+","+TASKER_NAME+","+IMAGE_URL);
    }

    private void setValues()
    {
        Log.e("setValues: ", "START!");
        tvTitle.setText(TASK_NAME);
        tvTaskerName.setText(TASKER_NAME);
        Picasso.with(this).load(IMAGE_URL).fit().centerInside().into(civTaskerPhoto);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(TASK_ID);
    }

    private void sendChatMessage()
    {
        Log.e("sendChatMessage: ", "START!");

            // Set Message Unique Key
            Map<String, Object> keyMap = new HashMap<String, Object>();
            TEMP_KEY = databaseReference.push().getKey();
            databaseReference.updateChildren(keyMap);

            DatabaseReference messageReference = databaseReference.child(TEMP_KEY);
            Map<String, Object> messageMap = new HashMap<String, Object>();
            messageMap.put("USER_ID", TG_ID);
            messageMap.put("MSG", etMessage.getText().toString());

            // DATE SENT
            DateFormat df = new SimpleDateFormat("MM dd yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            messageMap.put("DATE_SENT", date);
            messageReference.updateChildren(messageMap);

            etMessage.setText(" ");
    }

    private void getAllMessages(DataSnapshot dataSnapshot)
    {
        Log.e("getAllMessages: ", "START!");
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext())
        {
            String date_sent = ((DataSnapshot)i.next()).getValue(String.class);
            String message   = ((DataSnapshot)i.next()).getValue(String.class);
            String sender_id = ((DataSnapshot)i.next()).getValue(String.class);

            messageList.add(new Message(message, sender_id, date_sent));
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_messages.setLayoutManager(layoutManager);

        adapterChatMessages = new adapterChatMessages(chatDetailsActivity.this, messageList);
        rv_messages.setAdapter(adapterChatMessages);
        adapterChatMessages.notifyDataSetChanged();
    }

    private void initDbReferenceChildListeners()
    {
        Log.e("initDbReferenceCh: ", "START!");

        databaseReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                getAllMessages(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                getAllMessages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @OnClick({R.id.btnSend, R.id.btnBack})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.btnBack:
                this.finish();
                break;
            case R.id.btnSend:
                if(!etMessage.getText().toString().trim().equals(""))
                {
                    sendChatMessage();
                }
                break;
        }
    }
}
