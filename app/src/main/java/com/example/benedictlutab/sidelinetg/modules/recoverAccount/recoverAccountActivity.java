package com.example.benedictlutab.sidelinetg.modules.recoverAccount;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
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
import com.example.benedictlutab.sidelinetg.modules.changePassword.changePasswordActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class recoverAccountActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnSearch) Button btnSearch;
    @BindView(R.id.btnVerify) Button btnVerify;
    @BindView(R.id.btnChangePassword) Button btnChangePassword;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etCode) EditText etCode;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.llSearchEmail) LinearLayout llSearchEmail;
    @BindView(R.id.llVerifyCode) LinearLayout llVerifyCode;
    @BindView(R.id.llNewPassword) LinearLayout llNewPassword;

    final apiRouteUtil apiRouteUtil = new apiRouteUtil();

    private String EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recoveraccount_activity_recoveraccount);
        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    @OnClick({R.id.btnSearch, R.id.btnVerify, R.id.btnChangePassword, R.id.btnBack})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.btnSearch:
                searchEmail();
                break;
            case R.id.btnVerify:
                verifyCode();
                break;
            case R.id.btnChangePassword:
                changePassword();
                break;
            case R.id.btnBack:
                this.finish();
                break;
        }
    }

    private void searchEmail()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        // Validate fields before passing data to REST API.
        if(!validationUtil.isValidEmail(etEmail))
        {
            etEmail.setError("Enter valid email address");
            ERROR_COUNT = true;
        }

        if(!ERROR_COUNT)
        {
            Log.e("searchEmail: ", "STARTED!");
            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEARCH_EMAIL,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String ServerResponse)
                        {
                            String RESPONSE_CODE = ServerResponse.toString().replaceAll("\\s","");
                            Log.e("RESPONSE: ", RESPONSE_CODE);

                            // Display response.
                            if(RESPONSE_CODE.equals("EXISTS"))
                            {
                                EMAIL = etEmail.getText().toString();
                                sendVerificationCode();
                            }
                            else
                            {
                                TastyToast.makeText(recoverAccountActivity.this, "Email does not exists!",TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            // Showing error message if something goes wrong.
                            Log.e("Error Response:", volleyError.toString());
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    // Creating Map String Params.
                    Map<String, String> Parameter = new HashMap<String, String>();

                    // Sending all registration fields to 'Parameter'.
                    Parameter.put("email", etEmail.getText().toString());

                    return Parameter;
                }
            };
            // Initialize requestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(recoverAccountActivity.this);

            // Send the StringRequest to the requestQueue.
            requestQueue.add(StringRequest);
        }
    }

    private void verifyCode()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        // Validate fields before passing data to REST API.
        if(validationUtil.isEmpty(etCode))
        {
            etCode.setError("Verification code is empty");
            ERROR_COUNT = true;
        }

        if(!ERROR_COUNT)
        {
            Log.e("verifyCode: ", "STARTED!");
            // Init loading dialog.
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("");
            pDialog.setCancelable(false);

            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_VERIFY_CODE,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String ServerResponse)
                        {
                            String RESPONSE_CODE = ServerResponse.toString().replaceAll("\\s","");
                            Log.e("RESPONSE: ", RESPONSE_CODE);

                            if(RESPONSE_CODE.equals("VALID"))
                            {
                                // Hide other layouts except change pass layout.
                                llSearchEmail.setVisibility(View.GONE);
                                llNewPassword.setVisibility(View.VISIBLE);
                                llVerifyCode.setVisibility(View.GONE);
                            }
                            else
                            {
                                TastyToast.makeText(recoverAccountActivity.this, "Code is invalid or incorrect", TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            // Showing error message if something goes wrong.
                            Log.e("Error Response:", volleyError.toString());
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    // Creating Map String Params.
                    Map<String, String> Parameter = new HashMap<String, String>();

                    // Sending all registration fields to 'Parameter'.
                    Parameter.put("code", etCode.getText().toString());

                    return Parameter;
                }
            };
            // Initialize requestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(recoverAccountActivity.this);

            // Send the StringRequest to the requestQueue.
            requestQueue.add(StringRequest);

            pDialog.hide();
        }
    }

    private void changePassword()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        // Validate fields before passing data to REST API.
        if(!validationUtil.isValidPassword(etPassword))
        {
            etPassword.setError("Password contains invalid pattern");
            ERROR_COUNT = true;
        }

        if(!ERROR_COUNT)
        {
            Log.e("changePassword: ", "STARTED!");
            // Init loading dialog.
            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("");
            pDialog.setCancelable(false);

            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CHANGE_PASSWORD,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String ServerResponse)
                        {
                            String RESPONSE_CODE = ServerResponse.toString().replaceAll("\\s","");
                            Log.e("RESPONSE: ", RESPONSE_CODE);

                            if(RESPONSE_CODE.equals("SUCCESS"))
                            {
                                TastyToast.makeText(recoverAccountActivity.this, "Account has been successfully recovered!", TastyToast.LENGTH_LONG,TastyToast.SUCCESS).show();
                                finish();
                            }
                            else
                            {
                                TastyToast.makeText(recoverAccountActivity.this, "There has been an error in changing your password", TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            // Showing error message if something goes wrong.
                            Log.e("Error Response:", volleyError.toString());
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    // Creating Map String Params.
                    Map<String, String> Parameter = new HashMap<String, String>();

                    // Sending all registration fields to 'Parameter'.
                    Parameter.put("password", etPassword.getText().toString());
                    Parameter.put("email", EMAIL);

                    return Parameter;
                }
            };
            // Initialize requestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(recoverAccountActivity.this);

            StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Send the StringRequest to the requestQueue.
            requestQueue.add(StringRequest);

            pDialog.hide();
        }
    }

    private void sendVerificationCode()
    {
        Log.e("sendVerificationCode: ", "STARTED!");
        // Init loading dialog.
        final SweetAlertDialog pDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("");
        pDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SEND_VERIFICATION_CODE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        Log.e("RESPONSE: ", ServerResponse);

                        TastyToast.makeText(recoverAccountActivity.this, "6-digit verification code has been sent!", TastyToast.LENGTH_LONG,TastyToast.SUCCESS).show();

                        // Hide other layouts except verification layout.
                       llSearchEmail.setVisibility(View.GONE);
                       llNewPassword.setVisibility(View.GONE);
                       llVerifyCode.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        // Showing error message if something goes wrong.
                        Log.e("Error Response:", volleyError.toString());
                        TastyToast.makeText(recoverAccountActivity.this, "Connection Timeout!", TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                // Sending all registration fields to 'Parameter'.
                Parameter.put("email", etEmail.getText().toString());

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(recoverAccountActivity.this);

        StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        pDialog.hide();
    }
}
