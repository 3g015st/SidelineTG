package com.example.benedictlutab.sidelinetg.modules.loadTaskHistory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.modules.sendComplaintReport.complaintReportActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.RotationRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class taskHistoryDetailsActivity extends AppCompatActivity
{
    @BindView(R.id.tvDateCompleted) TextView tvDateCompleted;
    @BindView(R.id.tvTransCode) TextView tvTransCode;
    @BindView(R.id.tvTaskerName) TextView tvTaskerName;
    @BindView(R.id.tvMemberSince) TextView tvMemberSince;
    @BindView(R.id.tvTaskFee) TextView tvTaskFee;
    @BindView(R.id.tvCommFee) TextView tvCommFee;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvCategory) TextView tvCategory;
    @BindView(R.id.tvLineOneCity) TextView tvLineOneCity;

    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnSend) Button btnSend;
    @BindView(R.id.btnGoToComplaint) Button btnGoToComplaint;

    @BindView(R.id.swipeRefLayout_id) SwipeRefreshLayout swipeRefLayout;

    @BindView(R.id.civTaskerPhoto) CircleImageView civTaskerPhoto;

    @BindView(R.id.llShow) LinearLayout llShow;
    @BindView(R.id.llHide) LinearLayout llHide;

    @BindView(R.id.srbStar) RotationRatingBar srbStar;
    @BindView(R.id.srbRateTG) RotationRatingBar srbRateTG;

    @BindView(R.id.tvReview) TextView tvReview;
    @BindView(R.id.etReviewTG) EditText etReviewTG;

    private String TASK_ID, TASKER_ID, USER_ID, REVIEW, sendRating;
    private float RATING;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadtaskhistory_activity_task_history_details);

        ButterKnife.bind(this);

        changeFontFamily();
        fetchPassedValues();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        srbRateTG.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener()
        {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating)
            {
                sendRating = Float.toString(rating);
                Log.e("RATING: ", "" + sendRating);
            }
        });

        initSwipeRefLayout();
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    private void fetchPassedValues()
    {
        TASK_ID = getIntent().getStringExtra("TASK_ID");
        Log.e("fetchPValues: ", "Got the data!");
    }


    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    @OnClick(R.id.btnGoToComplaint)
    public void goToComplaint()
    {
        Intent intent = new Intent(this, complaintReportActivity.class);
        intent.putExtra("TASKER_ID", TASKER_ID);
        startActivity(intent);
    }

    private void initSwipeRefLayout()
    {
        swipeRefLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                // Fetching data from server
                fetchTaskHistoryDetails();
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
                fetchTaskHistoryDetails();
                swipeRefLayout.setRefreshing(false);
            }
        });
    }

    private void fetchTaskHistoryDetails()
    {
        Log.e("fetchTaskHistDetails: ", "STARTED !");
        Log.e("fetchTaskHistDetails: ", "USER_ID - " + USER_ID);

        final apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOAD_TASK_HIST_DTLS, new Response.Listener<String>()
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
                        Log.e("jsonObject: ", "COUNT - " + x +", "+jsonObject);

                        if(x == 0)
                        {
                            // Load task giver's prof pic.
                            Picasso.with(taskHistoryDetailsActivity.this).load(apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture")).
                                    fit().centerInside().into(civTaskerPhoto);
                            tvTitle.setText(jsonObject.getString("title"));
                            tvTaskerName.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
                            tvMemberSince.setText("Tasker since " + jsonObject.getString("date_created"));

                            TASKER_ID = jsonObject.getString("user_id");
                            Log.e("TASK_GIVER_ID: ", TASKER_ID);

                            tvTransCode.setText("Transaction ID: TSK-" + jsonObject.getString("transaction_code"));
                            tvLineOneCity.setText(jsonObject.getString("line_one") + ", " + jsonObject.get("city"));
                            tvDateCompleted.setText(jsonObject.getString("date_time_end"));
                            tvCategory.setText(jsonObject.getString("category_name"));
                            tvTaskFee.setText("TASK FEE: PHP " + jsonObject.getString("task_fee"));
                            tvCommFee.setText("SIDELINE SERVICE CHARGE: PHP " + jsonObject.getString("comm_fee"));

                            Log.e("Rating-Rating: ", "WALA TANGINA!");
                            llShow.setVisibility(View.GONE);
                            llHide.setVisibility(View.VISIBLE);
                        }

                        if(x > 0)
                        {
                            REVIEW = jsonObject.getString("review");
                            RATING = Float.parseFloat(jsonObject.getString("rating"));
                            Log.e("Rating-Rating: ", String.valueOf(RATING) +", "+ REVIEW);
                            if(RATING == 0.0)
                            {
                                llShow.setVisibility(View.GONE);
                                llHide.setVisibility(View.VISIBLE);
                            }
                            else if(RATING != 0.0)
                            {
                                llShow.setVisibility(View.VISIBLE);
                                llHide.setVisibility(View.GONE);

                                srbStar.setRating(RATING);
                                if(REVIEW == "NONE")
                                {
                                    tvReview.setVisibility(View.GONE);
                                }
                                else
                                {
                                    tvReview.setText(REVIEW);
                                }
                            }
                        }
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

                Parameter.put("TASK_ID", TASK_ID);
                Parameter.put("USER_ID", USER_ID);
                Parameter.put("ROLE", "Task Giver");

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @OnClick(R.id.btnSend)
    public void sendEvaluation()
    {
        Log.e("sendEvaluation: ", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while your evaluation is sending...");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEND_EVAL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.equals("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "Thanks for sending an evaluation!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                           initSwipeRefLayout();
                        }
                        else
                        {
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error sending your evaluation!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        swalDialog.hide();
                        Log.e("Error Response:", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                if(etReviewTG.getText().toString().isEmpty())
                {
                    String review = " ";
                    Parameter.put("review", review);
                }
                else
                {
                    Parameter.put("review", etReviewTG.getText().toString());
                }

                Parameter.put("receiver_id", TASKER_ID);
                Parameter.put("rating", sendRating);
                Parameter.put("sender_id", USER_ID);
                Parameter.put("task_id", TASK_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(taskHistoryDetailsActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

}
