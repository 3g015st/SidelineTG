package com.example.benedictlutab.sidelinetg.modules.messages.loadChatRooms;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.ChatRoom;
import com.example.benedictlutab.sidelinetg.modules.messages.chatRoomDetails.chatDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 8/13/2018.
 */

public class adapterChatRooms extends RecyclerView.Adapter<adapterChatRooms.ViewHolder>
{
    public Activity context;
    public List<ChatRoom> chatRoomList;

    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();

    public adapterChatRooms(Activity context, List<ChatRoom> chatRoomList)
    {
            this.context = context;
            this.chatRoomList = chatRoomList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loadchatrooms_layout_rv_chatrooms, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        ChatRoom chatRoom = chatRoomList.get(position);


        // Bind data.
        holder.TASK_GIVER_ID = chatRoom.getTask_giver_id();
        holder.TASKER_ID = chatRoom.getTasker_id();
        holder.TASK_ID = chatRoom.getTask_id();

        Log.e("IDs: ", holder.TASK_GIVER_ID + holder.TASKER_ID + holder.TASK_ID);

        holder.tvTaskerName.setText(chatRoom.getFirst_name() +" "+ chatRoom.getLast_name().substring(0, 1)+".");
        holder.tvTaskName.setText(chatRoom.getTitle());

        holder.IMAGE_URL = apiRouteUtil.DOMAIN + chatRoom.getProfile_picture();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskerPhoto);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot)
            {
                // When the chat room is clicked...
                holder.llChatRoom.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // If chat room exists go to the room
                        if(dataSnapshot.child(holder.TASK_ID).exists())
                        {
                            Log.e("ROOM: ", "EXISTS");
                            Intent intent = new Intent(context, chatDetailsActivity.class);
                            intent.putExtra("TASK_ID", holder.TASK_ID);
                            intent.putExtra("TASKER_NAME", holder.tvTaskerName.getText().toString());
                            intent.putExtra("TG_ID", holder.TASK_GIVER_ID);
                            intent.putExtra("TASK_NAME", holder.tvTaskName.getText().toString());
                            intent.putExtra("IMAGE_URL", holder.IMAGE_URL);
                            context.startActivity(intent);
                        }
                        else // Then make the room lol.
                        {
                            Log.e("ROOM: ", "DOES NOT EXISTS");
                            Map<String, Object> roomMap = new HashMap<String, Object>();
                            roomMap.put(holder.TASK_ID, "");
                            databaseReference.updateChildren(roomMap);

                            Intent intent = new Intent(context, chatDetailsActivity.class);
                            intent.putExtra("TASK_ID", holder.TASK_ID);
                            intent.putExtra("TASKER_NAME", holder.tvTaskerName.getText().toString());
                            intent.putExtra("TG_ID", holder.TASK_GIVER_ID);
                            intent.putExtra("TASK_NAME", holder.tvTaskName.getText().toString());
                            intent.putExtra("IMAGE_URL", holder.IMAGE_URL);
                            context.startActivity(intent);
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return chatRoomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llChatRoom) LinearLayout llChatRoom;
        @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
        @BindView(R.id.tvTaskerName) TextView tvTaskerName;
        @BindView(R.id.tvTaskName) TextView tvTaskName;

        String TASKER_ID, TASK_GIVER_ID, TASK_ID, IMAGE_URL;


        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
