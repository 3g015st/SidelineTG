package com.example.benedictlutab.sidelinetg.modules.viewTaskerProfile;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Skill;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Benedict Lutab on 7/26/2018.
 */

public class adapterDisplaySkills extends RecyclerView.Adapter<adapterDisplaySkills.ViewHolder>
{

    public Context context;
    public List<Skill> skillList;

    public adapterDisplaySkills(Context context, List<Skill> skillList)
    {
        this.context = context;
        this.skillList = skillList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.displayskills_layout_rv_skillset, null);
        if(view != null)
        {
            fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(view.getContext().getAssets(), "fonts/avenir.otf");
            fontStyleCrawler.replaceFonts((ViewGroup)view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final adapterDisplaySkills.ViewHolder holder, int position)
    {
        Log.e("onBindViewHolder:", "STARTED!");

        Skill skill = skillList.get(position);

        // Bind data.
        holder.SKILL_ID      = skill.getSkill_id();
        holder.SKILL_NAME    = skill.getName();
        holder.tvSkill.setText(skill.getName());
    }

    @Override
    public int getItemCount()
    {
        return skillList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvSkill) TextView tvSkill;

        private String SKILL_ID, SKILL_NAME;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
