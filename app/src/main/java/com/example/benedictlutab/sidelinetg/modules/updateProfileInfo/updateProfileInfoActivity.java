package com.example.benedictlutab.sidelinetg.modules.updateProfileInfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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
import com.example.benedictlutab.sidelinetg.modules.myTasks.viewTaskDetails.taskDetailsActivity;
import com.example.benedictlutab.sidelinetg.modules.postTask.postTaskActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class updateProfileInfoActivity extends AppCompatActivity
{
    @BindView(R.id.etFirstName) EditText etFirstName;
    @BindView(R.id.etLastName) EditText etLastName;
    @BindView(R.id.etAddress) EditText etAddress;
    @BindView(R.id.actvCity) AutoCompleteTextView actvCity;

    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.btnRemovePhoto) Button btnRemovePhoto;

    @BindView(R.id.civProfPic) CircleImageView civProfPic;

    private final static int REQUEST_CODE_PROF_PIC = 150;

    private SharedPreferences sharedPreferences;
    private String USER_ID;
    private String PROF_PIC, PROF_PIC_RAW;

    private Bitmap bitmap_prof_pic;

    // Store cities in an array.
    private String[] cities = {"Caloocan", "Las Piñas", "Makati", "Malabon", "Manila",
            "Mandaluyong", "Marikina", "Valenzuela", "Taguig", "San Juan",
            "Quezon", "Pateros", "Navotas", "Pasig", "Pasay", "Parañaque"};

    apiRouteUtil apiRouteUtil = new apiRouteUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updateprofileinfo_activity_update_profile_info);
        ButterKnife.bind(this);

        changeFontFamily();

        // Get USER_ID
        sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID"))
        {
            USER_ID = sharedPreferences.getString("USER_ID", "");
            Log.e("USER_ID:", USER_ID);
        }

        loadCities();
        loadUserInfo();
    }

    private void loadCities()
    {
        // Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cities);

        // Start working from first character
        actvCity.setThreshold(1);
        actvCity.setAdapter(adapter);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @OnClick({R.id.civProfPic, R.id.btnBack, R.id.btnSubmit, R.id.btnRemovePhoto})
    public void setViewOnClickEvent(View view)
    {
        switch (view.getId()) {
            case R.id.civProfPic:
                // Send request to upload image from gallery.
                ActivityCompat.requestPermissions(updateProfileInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PROF_PIC);
                break;
            case R.id.btnBack:
                this.finish();
                break;
            case R.id.btnSubmit:
                submitTask();
                break;
            case R.id.btnRemovePhoto:
                removeProfPic();
                break;
        }
    }

    private void submitTask()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        if(validationUtil.isEmpty(etFirstName))
        {
            etFirstName.setError("First name is required!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etLastName))
        {
            etLastName.setError("Last name is required!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etAddress))
        {
            etAddress.setError("Address line one is required!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(actvCity))
        {
            actvCity.setError("City is required!");
            ERROR_COUNT = true;
        }

        if(!ERROR_COUNT)
        {
            sendRequest();
        }
    }

    private void sendRequest()
    {
        Log.e("sendRequest: ", "STARTED!");

        RequestQueue requestQueue = Volley.newRequestQueue(updateProfileInfoActivity.this);

        // Init loading dialog.
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText(" ");
        swalDialog.setContentText("Please wait while we are updating your information :)");
        swalDialog.setCancelable(false);

        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_UPDATE_PROF_INFO,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String ServerResponse)
                    {
                        // Showing response message coming from server.
                        String SERVER_RESPONSE = ServerResponse.replaceAll("\\s+","");
                        Log.e("RESPONSE: ", SERVER_RESPONSE);

                        if(SERVER_RESPONSE.equals("SUCCESSSUCCESS") || SERVER_RESPONSE.equals("SUCCESS"))
                        {
                            // Exit this activity then prompt success
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "Your information has been successfully updated!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS).show();
                            finish();
                        }
                        else
                        {
                            // Prompt error
                            swalDialog.hide();
                            TastyToast.makeText(getApplicationContext(), "There has been an error updating your information!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
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
                        Log.e("ERROR RESPONSE: ", volleyError.toString());
                        // No network connection.
                        new SweetAlertDialog(updateProfileInfoActivity.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Connection Timeout").setContentText("There seems to have a connection error, please try again later. :(")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                                {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog)
                                    {
                                        // Exit application.
                                        finish();
                                    }
                                })
                                .show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                // Convert to BASE64
                if(bitmap_prof_pic != null)
                {
                    Log.e("bitmap_prof_pic: ", "NOT NULL!");
                    String imageOneData = imageToString(bitmap_prof_pic);
                    Parameter.put("profile_picture", imageOneData);
                }

                Parameter.put("first_name", etFirstName.getText().toString());
                Parameter.put("last_name", etLastName.getText().toString());
                Parameter.put("line_one", etAddress.getText().toString());
                Parameter.put("city", actvCity.getText().toString());
                Parameter.put("USER_ID", USER_ID);

                return Parameter;
            }
        };
        StringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE_PROF_PIC)
        {
            Log.e("oRP-REQ-CODE: ", Integer.toString(requestCode));
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), requestCode);
            }
            else
            {
                TastyToast.makeText(getApplicationContext(), "You don't have permission to access gallery!", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
            }
            return;
        }
        else
            Log.e("oRP-REQ-CODE: ", Integer.toString(requestCode));

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_PROF_PIC)
        {
            Log.e("oAR-REQ-CODE: ", Integer.toString(requestCode));
            if (resultCode == RESULT_OK && data != null)
            {
                Uri filePath = data.getData();
                try
                {
                    InputStream inputStream = getContentResolver().openInputStream(filePath);
                    bitmap_prof_pic = BitmapFactory.decodeStream(inputStream);
                    setImage(bitmap_prof_pic, requestCode);
                } catch (FileNotFoundException ex)
                {
                    Log.e("onActivityResult: ", ex.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(Bitmap bitmap, int requestCode)
    {
        if (requestCode == REQUEST_CODE_PROF_PIC)
        {
            civProfPic.setImageBitmap(bitmap);
        }
    }

    private void removeProfPic()
    {
        if (bitmap_prof_pic != null || !PROF_PIC_RAW.equals("uploads/images/no_image.png"))
        {
            Log.e("IMAGES: ", "EMPTY!");
            TastyToast.makeText(this, "Profile picture has been removed", TastyToast.LENGTH_LONG, TastyToast.WARNING).show();

            civProfPic.setImageDrawable(getResources().getDrawable(R.drawable.main_default_user_img));

            bitmap_prof_pic = null;
        }
    }

    private void loadUserInfo()
    {
        Log.e("loadUserInfo: ", "STARTED !");

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

                        PROF_PIC     = apiRouteUtil.DOMAIN + jsonObject.getString("profile_picture");
                        PROF_PIC_RAW = jsonObject.getString("profile_picture");

                        Log.e("PROF-PIC: ", PROF_PIC);

                        // Load prof pic.
                        Picasso.with(updateProfileInfoActivity.this).load(PROF_PIC).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).fit().centerInside().into(civProfPic);

                        etFirstName.setText(jsonObject.getString("first_name"));
                        etLastName.setText(jsonObject.getString("last_name"));
                        etAddress.setText(jsonObject.getString("line_one"));
                        actvCity.setText(jsonObject.getString("city"));

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
                        Log.d("Error Response: ", volleyError.toString());
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
}
