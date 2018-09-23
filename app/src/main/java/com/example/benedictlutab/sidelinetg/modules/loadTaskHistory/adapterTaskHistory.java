package com.example.benedictlutab.sidelinetg.modules.loadTaskHistory;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 9/22/2018.
 */

public class adapterTaskHistory extends RecyclerView.Adapter<adapterTaskHistory.ViewHolder>
{
    private Context context;
    private List<Task> taskHistoryList;

    public adapterTaskHistory(Context context, List<Task> taskHistoryList)
    {
        this.context = context;
        this.taskHistoryList = taskHistoryList;
    }

    @Override
    public adapterTaskHistory.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.taskhistory_layout_rv_task_history, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new adapterTaskHistory.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adapterTaskHistory.ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        final Task task = taskHistoryList.get(position);

        // Bind data.
        holder.TASK_ID = task.getTask_id();

        holder.tvTitle.setText(task.getTitle());
        holder.tvCompletedDate.setText("DATE AND TIME COMPLETED: " + task.getDate_completed());
        holder.tvTaskCategory.setText(task.getCategory());

        holder.llTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("TASK ID: ", holder.TASK_ID);
                Intent intent = new Intent(context, taskHistoryDetailsActivity.class);
                intent.putExtra("TASK_ID", holder.TASK_ID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return taskHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llTask) LinearLayout llTask;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvCompletedDate) TextView tvCompletedDate;
        @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;

        private String TASK_ID;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
