package com.example.benedictlutab.sidelinetg.modules.viewMyProfile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Evaluation;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.RotationRatingBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Benedict Lutab on 8/24/2018.
 */

public class adapterLatestEval extends RecyclerView.Adapter<adapterLatestEval.ViewHolder>
{
    private Context context;
    private List<Evaluation> evaluationList;

    public adapterLatestEval(Context context, List<Evaluation> evaluationList)
    {
        this.context = context;
        this.evaluationList = evaluationList;
    }

    @Override
    public adapterLatestEval.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loadeval_layout_rv_evaluation, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new adapterLatestEval.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adapterLatestEval.ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        final Evaluation evaluation = evaluationList.get(position);

        //Bind fetched image url from server
        holder.IMAGE_URL = apiRouteUtil.DOMAIN + evaluation.getProfile_picture();
        Log.e("IMAGE URL: ", holder.IMAGE_URL);

        holder.tvDateSent.setText(evaluation.getDate_time_sent().toString());
        holder.tvReview.setText(evaluation.getReview().toString());

        holder.srbStar.setRating(Float.parseFloat(evaluation.getRating()));
        holder.tvReview.setText("'"+evaluation.getReview().toString()+"'");

        holder.tvTitle.setText(evaluation.getTitle().toString());

        Picasso.with(context).load(holder.IMAGE_URL).fit().centerInside().into(holder.civTaskerPhoto);

    }

    @Override
    public int getItemCount()
    {
        return evaluationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvDateSent) TextView tvDateSent;
        @BindView(R.id.tvReview) TextView tvReview;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
        @BindView(R.id.srbStar) RotationRatingBar srbStar;

        private String IMAGE_URL;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
