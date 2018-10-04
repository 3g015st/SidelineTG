package com.example.benedictlutab.sidelinetg.modules.viewEvaluation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.models.Evaluation;
import com.example.benedictlutab.sidelinetg.modules.viewTaskerProfile.adapterLatestEval;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class evaluationActivity extends AppCompatActivity
{

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;
    private String USER_ID;

    @BindView(R.id.rv_evaluation)
    RecyclerView rv_evaluation;
    private List<Evaluation> evaluationList = new ArrayList<>();
    private adapterLatestEval adapterLatestEval;

    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private int lsEval;

    @BindView(R.id.llShow)
    LinearLayout llShow;
    @BindView(R.id.llEmpty) LinearLayout llEmpty;

    @BindView(R.id.btnBack)
    Button btnBack;

    private SharedPreferences sharedPreferences;

//    EVAL STATS
    @BindView(R.id.bcEvaluation) BarChart bcEvaluation;
    private ArrayList<BarEntry> countEval = new ArrayList<BarEntry>();
    private ArrayList<String> label = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewevaluation_activity_evaluation);

        ButterKnife.bind(this);

        Bundle Extras = getIntent().getExtras();
        if (Extras != null)
        {
            USER_ID = Extras.getString("USER_ID");
            Log.e("USER_ID: ", USER_ID);
        }
        else
        {
            // Get USER_ID
            sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("USER_ID"))
            {
                USER_ID = sharedPreferences.getString("USER_ID", "");
                Log.e("USER_ID: ", USER_ID);
            }
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

                fetchEvalList();
                fetchEvalStats();

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


                fetchEvalList();
                fetchEvalStats();

                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

    }

    private void initrvEval()
    {
        Log.d("initrvEval: ", String.valueOf(lsEval));

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_evaluation.setLayoutManager(layoutManager);

        adapterLatestEval = new adapterLatestEval(getApplicationContext(), evaluationList);
        rv_evaluation.setAdapter(adapterLatestEval);

        if (lsEval == 0)
        {
            Log.d("initRecyclerView: ", "GONE-VISIBLE");
            llShow.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.d("initRecyclerView: ", "VISIBLE-GONE");
            llShow.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void fetchEvalList()
    {
        Log.e("fetchEvalList: ", "STARTED!");
        evaluationList.clear();
        lsEval = 0;

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
                Parameter.put("SWITCH", "FULL");

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

        if(swipeRefLayout.isRefreshing())
        {
            swipeRefLayout.setRefreshing(false);
        }
    }

    private void fetchEvalStats()
    {
        Log.e("fetchEvalStats: ", "STARTED !");

        //RESET DAILY SALES AMOUNT
        countEval.clear();
        label.clear();
        final XAxis xAxis = bcEvaluation.getXAxis();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_EVAL_STATS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("SERVER RESPONSE: ", ServerResponse.toString());
                    JSONArray jsonArray = new JSONArray(ServerResponse.toString());

                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(x);
                        Log.e("jsonObject: ", "COUNT - " + x +", "+jsonObject);


                            countEval.add(new BarEntry(x, Float.parseFloat(jsonObject.getString("count"))));

                            //GET DAYS
                            label.add(jsonObject.getString("label") + " Star");
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(label));

                    }
                    // SET BARCHART ATTRIBS
                    bcEvaluation.setDrawBarShadow(false);
                    bcEvaluation.setDrawValueAboveBar(true);
                    bcEvaluation.setMaxVisibleValueCount(7);
                    bcEvaluation.setPinchZoom(false);
                    bcEvaluation.setDrawGridBackground(true);
                    bcEvaluation.getAxisLeft().setTextColor(Color.WHITE);
                    bcEvaluation.getAxisRight().setTextColor(Color.WHITE);
                    bcEvaluation.getXAxis().setTextColor(Color.WHITE);
                    bcEvaluation.getLegend().setTextColor(Color.WHITE);
                    bcEvaluation.setGridBackgroundColor(Color.TRANSPARENT);
                    bcEvaluation.fitScreen();

                    // SET DATASET NAME
                    BarDataSet bdsEarnings = new BarDataSet(countEval, "Number of ratings given.");
                    bdsEarnings.setColors(ColorTemplate.COLORFUL_COLORS);
                    bdsEarnings.setValueTextColor(Color.WHITE);
                    bdsEarnings.setValueTextSize(10f);

                    // I-INITIALIZE NA YUNG DATASET
                    BarData earningsData = new BarData(bdsEarnings);
                    earningsData.setBarWidth(0.9f);
                bcEvaluation.setData(earningsData);

                    // SET X-AXIS ATTRIBS
                    xAxis.setGranularity(1f); // only intervals of 1 day
                    xAxis.setTextSize(12);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                    swipeRefLayout.setRefreshing(false);

                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
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

        if (swipeRefLayout.isRefreshing()) {
            swipeRefLayout.setRefreshing(false);
        }
    }
}
