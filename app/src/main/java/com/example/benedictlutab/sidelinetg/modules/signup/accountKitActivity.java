package com.example.benedictlutab.sidelinetg.modules.signup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetg.modules.login.loginActivity;
import com.example.benedictlutab.sidelinetg.modules.welcomeUser.entranceActivity;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;
import java.util.Map;

public class accountKitActivity extends AppCompatActivity
{
    public static int APP_REQUEST_CODE = 99;
    private String first_name, last_name, gender, mobile_number, birthdate, email, password, role, line_one, city;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_account_kit);

        fetchPassedValues();
        startPhoneVerification();
    }

    public void startPhoneVerification()
    {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);

        // Set default country code to PH.
        configurationBuilder.setDefaultCountryCode("PH");

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE && resultCode == RESULT_OK)
        {
            createUserAccount();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(accountKitActivity.this, entranceActivity.class);
        startActivity(intent);
        finish();
    }

    private void createUserAccount()
    {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>()
        {
            @Override
            public void onSuccess(final Account account)
            {
                // Get mobile number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null)
                {
                    mobile_number = phoneNumber.toString();
                    Log.e("Fetched P.Number: ", mobile_number);

                    sendUserCredentials();
                }
            }
            @Override
            public void onError(final AccountKitError error)
            {
                Log.e("getMobileNumber: ", error.toString());
            }
        });
    }

    private void fetchPassedValues()
    {
        first_name  =  getIntent().getStringExtra("first_name");
        last_name   =  getIntent().getStringExtra("last_name");
        gender      =  getIntent().getStringExtra("gender");
        birthdate   =  getIntent().getStringExtra("birthdate");
        email       =  getIntent().getStringExtra("email");
        password    =  getIntent().getStringExtra("password");
        role        =  getIntent().getStringExtra("role");
        line_one    =  getIntent().getStringExtra("line_one");
        city        =  getIntent().getStringExtra("city");
        Log.e("fetchPValues: ", "Got the data!"+first_name+last_name+gender+mobile_number+birthdate+email+password+role+line_one+city);
    }

    private void sendUserCredentials()
    {
        // Get route obj.
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Submit user's information to the server.

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_SIGNUP,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        swalDialog.hide();
                        // Showing response message coming from server.
                        String RESPONSE_CODE = ServerResponse.toString().replaceAll("\\s","");
                        Log.e("RESPONSE: ", RESPONSE_CODE);
                        switch(RESPONSE_CODE)
                        {
                            case "SUCCESS":
                            {
                                // If success go to loginActivity.
                                Intent loginIntent = new Intent(accountKitActivity.this, loginActivity.class);
                                startActivity(loginIntent);
                                finish();
                                TastyToast.makeText(getApplicationContext(), "Account creation successful!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                                break;
                            }
                            case "ERROR":
                            {
                                Intent signupIntent = new Intent(accountKitActivity.this, signupActivity.class);
                                startActivity(signupIntent);
                                finish();
                                TastyToast.makeText(getApplicationContext(), "Account creation failed!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                break;
                            }
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
                Parameter.put("first_name", first_name);
                Parameter.put("last_name", last_name);
                Parameter.put("gender", gender);
                Parameter.put("birth_date", birthdate);
                Parameter.put("mobile_number", mobile_number);
                Parameter.put("email", email);
                Parameter.put("password", password);
                Parameter.put("role", role);
                Parameter.put("line_one", line_one);
                Parameter.put("city", city);

                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(accountKitActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}

