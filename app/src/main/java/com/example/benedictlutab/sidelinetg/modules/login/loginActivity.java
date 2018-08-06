package com.example.benedictlutab.sidelinetg.modules.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
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
import com.example.benedictlutab.sidelinetg.modules.viewHome.homeActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class loginActivity extends AppCompatActivity
{
    @BindView(R.id.etRecoverAccount) EditText etRecoverAccount;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.tilEmail) TextInputLayout tilEmail;
    @BindView(R.id.tilPassword) TextInputLayout tilPassword;

    private String ROLE = "Task Giver";
    private SharedPreferences sharedPreferences;
    private String message, response_code;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_login);
        ButterKnife.bind(this);

        changeFontFamily();

        // Make uneditable.
        etRecoverAccount.setFocusable(false);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Change Hint Font Style.
        tilEmail.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/avenir.otf"));
        tilPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/avenir.otf"));
    }

    @OnClick(R.id.btnLogin)
    public void loginUser()
    {
        Log.e("loginUser:", "START!");

        validationUtil validationUtil = new validationUtil();
        if(validationUtil.isValidEmail(etEmail) && validationUtil.isEmpty(etPassword))
        {
            etEmail.setError("Email address is invalid");
            etPassword.setError("Password is required!");
        }
        else if(validationUtil.isEmpty(etEmail) || validationUtil.isEmpty(etPassword))
        {
            TastyToast.makeText(getApplicationContext(), "Both fields are required", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
        }
        else
        {
            // Get route obj.
            apiRouteUtil apiRouteUtil = new apiRouteUtil();

            // Init loading dialog.
            final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            swalDialog.setTitleText("");
            swalDialog.setCancelable(false);

            StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_LOGIN,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String ServerResponse)
                        {
                            swalDialog.hide();
                            // Showing response message coming from server.
                            Log.e("RESPONSE: ", ServerResponse);

                            // Fetch JSON Response
                            try
                            {
                                JSONArray jsonArray = new JSONArray(ServerResponse);

                                for(int x = 0; x < jsonArray.length(); x++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(x);
                                    message               = jsonObject.getString("message");
                                    response_code         = jsonObject.getString("response_code");

                                    Log.e("Fetch jsonArray:", message + response_code);
                                }

                                if(message.equals("Invalid email or password") && response_code.equals("ERROR"))
                                {
                                    TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                }
                                else if(message.equals("Account does not exists.") && response_code.equals("ERROR"))
                                {
                                    TastyToast.makeText(getApplicationContext(), message, TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                                }
                                else if(response_code.equals("SUCCESS"))
                                {
                                    sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("USER_ID", message);
                                    editor.commit();

                                    // Go to homeActivity
                                    Intent intent = new Intent(loginActivity.this, homeActivity.class);
                                    finish();
                                    startActivity(intent);
                                    Log.e("loginUser:", "SUCCESS!" + message);
                                }

                            }
                            catch(JSONException e)
                            {
                                e.printStackTrace();
                                swalDialog.hide();
                                Log.e("loginUser (CATCH): ", e.toString());
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

                    // Sending all registration fields to 'Parameter'.
                    Parameter.put("email", etEmail.getText().toString());
                    Parameter.put("password", etPassword.getText().toString());
                    Parameter.put("role", ROLE);

                    return Parameter;
                }
            };
            // Initialize requestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(loginActivity.this);

            // Send the StringRequest to the requestQueue.
            requestQueue.add(StringRequest);

            // Display progress dialog.
            swalDialog.show();
        }
        }

}
