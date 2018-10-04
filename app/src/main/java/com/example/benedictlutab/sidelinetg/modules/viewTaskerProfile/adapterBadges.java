package com.example.benedictlutab.sidelinetg.modules.viewTaskerProfile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Badge;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 10/4/2018.
 */

public class adapterBadges extends RecyclerView.Adapter<adapterBadges.ViewHolder>
{

    public Context context;
    public List<Badge> badgeList;

    public adapterBadges(Context context, List<Badge> badgeList)
    {
        this.context = context;
        this.badgeList = badgeList;
    }

    @Override
    public adapterBadges.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.viewmyprofile_layout_rv_badges, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new adapterBadges.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adapterBadges.ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        Badge badge = badgeList.get(position);

        // Bind data.
        holder.ID      = badge.getBadge_id();
        holder.tvName.setText(badge.getName());

        holder.DESCRIPTION    = badge.getDescription();

        holder.IMAGE = apiRouteUtil.DOMAIN + badge.getImage();
        Log.e("IMAGE URL: ", holder.IMAGE);

        //Bind fetched image url from server
        Picasso.with(context).load(holder.IMAGE).fit().centerInside().into(holder.ivBadgeImage);

        holder.ivBadgeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("ivBadgeImage: ", "CLICKED!!!");
                displayBadgeDialog(holder.tvName.getText().toString(), holder.DESCRIPTION, holder.ivBadgeImage);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return badgeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivBadgeImage) ImageView ivBadgeImage;

        private String ID, NAME, DESCRIPTION, IMAGE;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void displayBadgeDialog(String title, String description, ImageView ivBadge)
    {
        Log.e("displayBadgeDialog: ", "START!");
        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(title)
                .setContentText(description)
                .setCustomImage(ivBadge.getDrawable())
                .show();
    }
}
