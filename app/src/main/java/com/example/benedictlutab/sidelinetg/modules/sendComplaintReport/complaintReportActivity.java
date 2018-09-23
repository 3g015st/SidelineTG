package com.example.benedictlutab.sidelinetg.modules.sendComplaintReport;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.helpers.validationUtil;
import com.example.benedictlutab.sidelinetg.modules.loadTaskHistory.taskHistoryDetailsActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class complaintReportActivity extends Activity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnSend) Button btnSend;

    @BindView(R.id.etDetails) EditText etDetails;

    @BindView(R.id.nsComplaintCategory) NiceSpinner nsComplaintCategory;

    private String TASKER_ID, USER_ID, CATEGORY_ID, DETAILS;

    private SharedPreferences sharedPreferences;
    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private ArrayList<String> categoryName = new ArrayList<String>();
    private ArrayList<String> categoryId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendcomplaintreport_activity_complaint_report);

        ButterKnife.bind(this);

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        changeFontFamily();
        fetchPassedValues();

        fetchComplaintCategories();

        nsComplaintCategory.setOnItemSelectedListener(this);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    private void fetchPassedValues()
    {
        TASKER_ID = getIntent().getStringExtra("TASKER_ID");
        Log.e("fetchPValues: ", "Got the data! - " + TASKER_ID);
    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void fetchComplaintCategories()
    {
        Log.e("fetchComplaintCateg: ", "STARTED !");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_FETCH_COMP_CAT, new Response.Listener<String>()
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

                        categoryId.add(jsonObject.getString("complaint_category_id"));
                        categoryName.add(jsonObject.getString("name"));

                        // SET DEFAULT CATEGORY ID
                        if(x == 0)
                        {
                            CATEGORY_ID = categoryId.get(0);
                        }

                        nsComplaintCategory.attachDataSource(categoryName);
                    }
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

                return Parameter;
            }
        };
        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        CATEGORY_ID = categoryId.get(position);
        Log.e("CATEGORY-ID(FETCHED): ", CATEGORY_ID);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @OnClick(R.id.btnSend)
    public void submitTask()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        if(!validationUtil.isValidComplaint(etDetails))
        {
            etDetails.setError("Details is empty or is less than 40 characters.");
            ERROR_COUNT = true;
        }

        if(!ERROR_COUNT)
        {
            DETAILS = etDetails.getText().toString();
            Log.e("DETAILS: ", DETAILS);
            submitRequest();
        }
    }

    public void submitRequest()
    {
        Log.e("submitRequest: ", "START!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while your complaint is sending...");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEND_COMPLAINT,
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
                            TastyToast.makeText(getApplicationContext(), "Complaint successfully sent, expect a reply from us within 24 hours.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            finish();
                        }
                        else
                        {
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error sending your complaint!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
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

                Parameter.put("description", DETAILS);
                Parameter.put("complainant_id", USER_ID);
                Parameter.put("defendant_id", TASKER_ID);
                Parameter.put("complaint_category_id", CATEGORY_ID);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(complaintReportActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}
