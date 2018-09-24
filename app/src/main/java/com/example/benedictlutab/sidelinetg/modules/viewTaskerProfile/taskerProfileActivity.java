package com.example.benedictlutab.sidelinetg.modules.viewTaskerProfile;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Evaluation;
import com.example.benedictlutab.sidelinetg.models.Skill;
import com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskDetails.taskDetailsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class taskerProfileActivity extends AppCompatActivity
{
    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;
    @BindView(R.id.ivCoverPhoto) ImageView ivCoverPhoto;
    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;
    @BindView(R.id.tvTaskerName) TextView tvTaskerName;
    @BindView(R.id.tvTskrName) TextView tvTskrName;
    @BindView(R.id.tvMemberSince) TextView tvMemberSince;
    @BindView(R.id.tvCity) TextView tvCity;
    @BindView(R.id.tvGender) TextView tvGender;
    @BindView(R.id.tvAge) TextView tvAge;

    @BindView(R.id.tvShortBio) TextView tvShortBio;
    @BindView(R.id.tvAverageRating) TextView tvAverageRating;
    @BindView(R.id.tvNoLatestReviews) TextView tvNoLatestReviews;

    @BindView(R.id.rv_skills) RecyclerView rvSkills;
    private List<Skill> skillList = new ArrayList<>();
    private adapterDisplaySkills adapterDisplaySkills;

    @BindView(R.id.rv_featuredrevs) RecyclerView rv_featuredrevs;
    private List<Evaluation> evaluationList = new ArrayList<>();
    private adapterLatestEval adapterLatestEval;

    private int lsEval;

    private int lsSkills;

    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtaskerprofile_activity_tasker_profile);
        ButterKnife.bind(this);

        Bundle Extras = getIntent().getExtras();
        if (Extras != null)
        {
            USER_ID = Extras.getString("USER_ID");
            Log.e("USER_ID: ", USER_ID);
        }

        changeFontFamily();
        initSwipeRefLayout();
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskerDetails();
                fetchSkills();
                fetchEvalList();

            }
        });
        swipeRefLayout.setColorSchemeResources(R.color.colorPrimaryDark, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeRefLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefLayout.setRefreshing(true);

                // Fetching data from server
                fetchTaskerDetails();
                fetchSkills();
                fetchEvalList();

                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void initrvSkills()
    {
        Log.d("lsSkills: ", String.valueOf(lsSkills));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSkills.setLayoutManager(layoutManager);

        adapterDisplaySkills = new adapterDisplaySkills(getApplicationContext(), skillList);
        rvSkills.setAdapter(adapterDisplaySkills);

        if (lsSkills == 0)
        {
            Log.d("initRecyclerView: ", "GONE-VISIBLE");
            rvSkills.setVisibility(View.GONE);
        }
        else
        {
            Log.d("initRecyclerView: ", "VISIBLE-GONE");
            rvSkills.setVisibility(View.VISIBLE);
        }
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

    }

    private void fetchTaskerDetails()
    {
        Log.e("fetchTaskerDetails: ", "STARTED !");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_PROFILE_DETAILS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);

                        // Load tasker's prof pic.
                        Picasso.with(taskerProfileActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                fit().centerInside().into(civTaskerPhoto);

                        // Load cover photo.
                        Picasso.with(taskerProfileActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).transform(new BlurTransformation(taskerProfileActivity.this, 25, 1)).
                                fit().centerInside().into(ivCoverPhoto);

                        tvTaskerName.setText(jsonObject.getString("first_name")+"'s"+" PROFILE");
                        tvTskrName.setText(jsonObject.getString("first_name") +" "+ jsonObject.getString("last_name").substring(0, 1)+".");
                        tvMemberSince.setText("Tasker since "+jsonObject.getString("date_created"));
                        tvCity.setText(jsonObject.getString("city")+ " City");
                        tvAge.setText(jsonObject.getString("age")+" years old");
                        tvGender.setText(jsonObject.getString("gender"));

                        tvShortBio.setText(jsonObject.getString("short_bio"));
                        tvAverageRating.setText(jsonObject.getString("avg_rating") +" "+ "Average Rating");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());

                }
                swipeRefLayout.setRefreshing(false);
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.d("Error Response: ", volleyError.toString());
                        swipeRefLayout.setRefreshing(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchSkills()
    {
        Log.e("fetchSkills: ", "STARTED!");
        skillList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_TASKER_SKILLS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        // Adding the jsonObject to the List.
                        skillList.add(new Skill(jsonObject.getString("skill_id"),
                                jsonObject.getString("name")));
                        lsSkills = skillList.size();
                        Log.e("lsSkills: ", String.valueOf(lsSkills));
                    }
                    initrvSkills();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("tasker_id", USER_ID);

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void initrvEval()
    {
        Log.d("initrvEval: ", String.valueOf(lsEval));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_featuredrevs.setLayoutManager(layoutManager);

        adapterLatestEval = new adapterLatestEval(getApplicationContext(), evaluationList);
        rv_featuredrevs.setAdapter(adapterLatestEval);

        if (lsEval == 0)
        {
            Log.d("initRecyclerView: ", "GONE-VISIBLE");
            rv_featuredrevs.setVisibility(View.GONE);
            tvNoLatestReviews.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.d("initRecyclerView: ", "VISIBLE-GONE");
            rv_featuredrevs.setVisibility(View.VISIBLE);
            tvNoLatestReviews.setVisibility(View.VISIBLE);
        }
    }

    private void fetchEvalList()
    {
        Log.e("fetchEvalList: ", "STARTED!");
        evaluationList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_EVAL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        // Adding the jsonObject to the List.
                        evaluationList.add(new Evaluation(jsonObject.getString("full_name"),
                                jsonObject.getString("profile_picture"),
                                jsonObject.getString("review"),
                                jsonObject.getString("date_time_sent"),
                                jsonObject.getString("rating"),
                                jsonObject.getString("title")));
                        lsEval = evaluationList.size();
                        Log.e("lsEval: ", String.valueOf(lsEval));
                    }
                    initrvEval();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("receiver_id", USER_ID);

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
}
