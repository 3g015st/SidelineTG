package com.example.benedictlutab.sidelinetg.modules.myTasks.myTaskList;

import android.app.Activity;
import android.content.Context;
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
import com.example.benedictlutab.sidelinetg.models.Task;
import com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskDetails.taskDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 7/26/2018.
 */

public class adapterMyTasks extends RecyclerView.Adapter<adapterMyTasks.ViewHolder>
{
    public Activity context;
    public List<Task> taskList;

    public adapterMyTasks(Activity context, List<Task> taskList)
    {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mytasks_layout_rv_my_tasks, null);
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

        Task task = taskList.get(position);

        // Bind data.
        holder.TASK_ID      = task.getTask_id();
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskAddress.setText(task.getAddress());
        holder.tvTaskDate.setText(task.getDate_time_end());
        holder.tvTaskStatus.setText(task.getStatus());
        holder.tvTaskPayment.setText("PHP " + task.getTask_fee());

        holder.IMAGE_URL = apiRouteUtil.DOMAIN + task.getImage_one();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskPhoto);

        holder.llMyTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Go to Task Details.
                Log.e("TASK ID: ", holder.TASK_ID);
                Intent intent = new Intent(context, taskDetailsActivity.class);
                intent.putExtra("TASK_ID", holder.TASK_ID);
                context.startActivityForResult(intent, 69);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llMyTask) LinearLayout llMyTask;
        @BindView(R.id.civTaskPhoto) CircleImageView civTaskPhoto;
        @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
        @BindView(R.id.tvTaskAddress) TextView tvTaskAddress;
        @BindView(R.id.tvTaskDate) TextView tvTaskDate;
        @BindView(R.id.tvTaskStatus) TextView tvTaskStatus;
        @BindView(R.id.tvTaskPayment) TextView tvTaskPayment;

        private String TASK_ID, IMAGE_URL;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("onActivityResult", "START!");
        // check if the request code is same as what is passed  here it is 2
        if(requestCode == 69)
        {
            // Refresh activity.

        }
    }
}
