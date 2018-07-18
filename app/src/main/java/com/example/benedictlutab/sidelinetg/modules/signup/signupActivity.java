package com.example.benedictlutab.sidelinetg.modules.signup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class signupActivity extends AppCompatActivity
{
    // Store cities in an array.
    private String[] cities = {"Caloocan", "Las Piñas", "Makati", "Malabon", "Manila",
                               "Mandaluyong", "Marikina", "Valenzuela", "Taguig", "San Juan",
                                "Quezon","Pateros","Pasig","Pasay","Parañaque"};

    private final String ROLE = "Task Giver";
    private String Gender;
    private DatePickerDialog.OnDateSetListener DateSetListener;
    private RadioButton rdbSelectedGender;

    @BindView(R.id.actvCity) AutoCompleteTextView actvCity;
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etFirstName) EditText etFirstName;
    @BindView(R.id.etLastName) EditText etLastName;
    @BindView(R.id.etBirthdate) EditText etBirthdate;
    @BindView(R.id.etAL1) EditText etAL1;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.rdbMale) RadioButton rdbMale;
    @BindView(R.id.rdbFemale) RadioButton rdbFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity_signup);
        ButterKnife.bind(this);

        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/ralewayRegular.ttf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        // Make uneditable.
        etBirthdate.setFocusable(false);

        // Set date listener.
        DateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                Log.e("signupAcitivity" , "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                etBirthdate.setText(date);
            }
        };

        loadCities();
    }

    @OnClick({R.id.etBirthdate, R.id.btnSubmit, R.id.btnBack})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.etBirthdate:
                displayDatePicker();
                break;
            case R.id.btnSubmit:
                signupUser();
                break;
            case R.id.btnBack:
                this.finish();
                break;
        }
    }

    public void displayDatePicker()
    {
        // Set max date to 12/31/2000 or 18 years old only.
        long MAX_DATE = 978192000000L;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(signupActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DateSetListener, year,month,day);
        dialog.getDatePicker().setMaxDate(MAX_DATE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private void loadCities()
    {
        // Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cities);

        // Start working from first character
        actvCity.setThreshold(1);
        actvCity.setAdapter(adapter);
    }

    public void fetchSelectedGender()
    {
        int selectedGender = rdgGender.getCheckedRadioButtonId();
        rdbSelectedGender = findViewById(selectedGender);
        Gender = rdbSelectedGender.getText().toString();
        Log.e("Fetch Gender: ", Gender);
    }

    private void signupUser()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        fetchSelectedGender();

        // Validate fields before passing data to REST API.
        if(!validationUtil.isValidEmail(etEmail))
        {
            etEmail.setError("Enter valid email address");
            ERROR_COUNT = true;
        }
        if(!validationUtil.isValidPassword(etPassword))
        {
            etPassword.setError("Invalid password pattern");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etFirstName))
        {
            etFirstName.setError("Input your first name");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etLastName))
        {
            etLastName.setError("Input your last name ");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etBirthdate))
        {
            etBirthdate.setError("Input your birthdate");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etAL1))
        {
            etAL1.setError("Input your address line 1");
            ERROR_COUNT = true;
        }
        if(!validationUtil.isValidCity(actvCity, cities))
        {
            actvCity.setError("Invalid or empty city");
            ERROR_COUNT = true;
        }
        if(!ERROR_COUNT)
        {
            // Submit information
            submitInfo();
        }

    }

    private void submitInfo()
    {
        Log.e("submitInfo:", "STARTED!");
        // Get route obj.
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        // Initialize loading dialog
        final SweetAlertDialog swalDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        swalDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        swalDialog.setTitleText("");
        swalDialog.setCancelable(false);

        // This POST request shall validate if the email already exists
        StringRequest StringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_CHECK_EMAIL_EXISTS, new Response.Listener<String>()
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
                            Intent intent = new Intent(signupActivity.this, accountKitActivity.class);

                            // If email does not exist, pass data to next activity.
                            Log.e("Passed data: ", etFirstName.getText().toString());
                            intent.putExtra("first_name", etFirstName.getText().toString());
                            intent.putExtra("last_name", etLastName.getText().toString());
                            intent.putExtra("gender", Gender);
                            intent.putExtra("birthdate", etBirthdate.getText().toString());
                            intent.putExtra("email", etEmail.getText().toString());
                            intent.putExtra("password", etPassword.getText().toString());
                            intent.putExtra("role", ROLE);
                            intent.putExtra("line_one", etAL1.getText().toString());
                            intent.putExtra("city", actvCity.getText().toString());

                            finish();
                            startActivity(intent);
                            break;
                        }
                        case "ERROR":
                        {
                            // If email already exists, try again.
                            TastyToast.makeText(getApplicationContext(), "Email does exists", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                            break;
                        }
                        default:
                        {
                            TastyToast.makeText(getApplicationContext(), "Network Error", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
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

                // Sending email to 'Parameter'.
                Parameter.put("email", etEmail.getText().toString());
                return Parameter;
            }
        };
        // Initialize requestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(signupActivity.this);

        // Send the StringRequest to the requestQueue.
        requestQueue.add(StringRequest);

        // Display progress dialog.
        swalDialog.show();
    }
}

