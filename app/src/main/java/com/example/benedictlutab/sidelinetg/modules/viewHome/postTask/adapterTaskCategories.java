package com.example.benedictlutab.sidelinetg.modules.viewHome.postTask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.taskCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 7/19/2018.
 */

public class adapterTaskCategories extends RecyclerView.Adapter<adapterTaskCategories.ViewHolder>
{
    private Context context;
    private List<taskCategory> taskCategoryList;

    public adapterTaskCategories(Context context, List<taskCategory> taskCategoryList)
    {
        this.context = context;
        this.taskCategoryList = taskCategoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.posttask_layout_rv_task_category, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/ralewayRegular.ttf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        Log.e("adapterTskCatego:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        taskCategory taskCategory = taskCategoryList.get(position);

        // Bind data.
        holder.ID = taskCategory.getTask_category_id();
        holder.tvTaskTitle.setText(taskCategory.getName());
        holder.IMAGE_URL = apiRouteUtil.DOMAIN + "api" + taskCategory.getTask_category_img();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.ivTaskCategoryImage);

        holder.llTaskCategory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("TASK CATEG ID:", holder.ID);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return taskCategoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.llTskCategory) LinearLayout llTaskCategory;
        @BindView(R.id.tvTaskTitle) TextView tvTaskTitle;
        @BindView(R.id.ivTaskCategoryImage) ImageView ivTaskCategoryImage;

        private String IMAGE_URL, ID;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
