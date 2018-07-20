package com.example.benedictlutab.sidelinetg.modules.viewHome.postTask;

import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class postTaskActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;
    @BindView(R.id.tilTaskTitle) TextInputLayout tilTaskTitle;
    @BindView(R.id.tilDescription)  TextInputLayout tilDescription;
    @BindView(R.id.tilTaskDate) TextInputLayout tilTaskDate;
    @BindView(R.id.tilTaskAddress) TextInputLayout tilTaskAddress;
    @BindView(R.id.tilTaskPayment) TextInputLayout tilTaskPayment;

    private String TASK_CATEGORY_ID, TASK_CATEGORY_NAME, MINIMUM_PAYMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posttask_activity_post_task);
        ButterKnife.bind(this);

        fetchPassedValues();
        changeFontFamily();

        // Set title as fetch task category
        tvTaskCategory.setText(TASK_CATEGORY_NAME);
    }

    private void fetchPassedValues()
    {
        TASK_CATEGORY_ID  =  getIntent().getStringExtra("TASK_CATEGORY_ID");
        TASK_CATEGORY_NAME  =  getIntent().getStringExtra("TASK_CATEGORY_NAME");
        MINIMUM_PAYMENT   =  getIntent().getStringExtra("MINIMUM_PAYMENT");
        Log.e("PASSED VALUES:", TASK_CATEGORY_ID + MINIMUM_PAYMENT + TASK_CATEGORY_NAME);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Change Hint Font Style.
        tilTaskTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ralewayRegular.ttf"));
        tilDescription.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ralewayRegular.ttf"));
        tilTaskDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ralewayRegular.ttf"));
        tilTaskAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ralewayRegular.ttf"));
        tilTaskPayment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ralewayRegular.ttf"));
    }

    @OnClick({R.id.btnBack})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.btnBack:
                this.finish();
                break;
        }
    }
}
