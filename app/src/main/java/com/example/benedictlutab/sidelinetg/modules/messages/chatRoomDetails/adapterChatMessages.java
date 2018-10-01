package com.example.benedictlutab.sidelinetg.modules.messages.chatRoomDetails;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;

import com.example.benedictlutab.sidelinetg.models.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 8/13/2018.
 */

public class adapterChatMessages extends RecyclerView.Adapter<adapterChatMessages.ViewHolder>
{
    public Activity context;
    public List<Message> messageList;

    private SharedPreferences sharedPreferences;

    private String USER_ID;

    public adapterChatMessages(Activity context, List<Message> messageList)
    {
        this.context = context;
        this.messageList = messageList;

        // Get USER_ID
        sharedPreferences = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID: ", USER_ID);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chatroomdetails_layout_rv_messages, null);
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

        Log.e("POSITION: ", Integer.toString(position));
        Message message = messageList.get(position);

        Log.e("MESSAGE,SENDER_ID,DS: ", message.getMessage()+", "+message.getSender_id()+", "+message.getDate_sent());
        holder.SENDER_ID = message.getSender_id();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Bind data.
        if(message.getSender_id().equals(USER_ID))
        {
            params.gravity = Gravity.LEFT|Gravity.START;
            holder.llMessage.setLayoutParams(params);

            holder.llMessage.setGravity(Gravity.LEFT);
            holder.tvMessage.setText(message.getMessage());
            holder.tvDateSent.setText(message.getDate_sent());

            holder.bblMessage.setArrowDirection(ArrowDirection.LEFT);
            holder.bblMessage.setBubbleColor(Color.parseColor("#80cbc4"));
        }
        else
        {
            params.gravity = Gravity.RIGHT|Gravity.END;
            holder.llMessage.setLayoutParams(params);

            holder.llMessage.setGravity(Gravity.RIGHT);
            holder.tvMessage.setText(message.getMessage());
            holder.tvDateSent.setText(message.getDate_sent());

            holder.bblMessage.setArrowDirection(ArrowDirection.RIGHT);
            holder.bblMessage.setBubbleColor(Color.parseColor("#ef9a9a"));
        }
    }

    @Override
    public int getItemCount()
    {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvMessage) TextView tvMessage;
        @BindView(R.id.tvDateSent) TextView tvDateSent;
        @BindView(R.id.bblMessage) BubbleLayout bblMessage;
        @BindView(R.id.llMessage) LinearLayout llMessage;

        private String SENDER_ID;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
