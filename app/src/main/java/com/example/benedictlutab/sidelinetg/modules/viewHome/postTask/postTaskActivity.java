package com.example.benedictlutab.sidelinetg.modules.viewHome.postTask;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.benedictlutab.sidelinetg.R;
import com.example.benedictlutab.sidelinetg.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetg.helpers.validationUtil;
import com.example.benedictlutab.sidelinetg.modules.signup.signupActivity;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class postTaskActivity extends AppCompatActivity
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.btnPost) Button btnPost;

    @BindView(R.id.tvTaskCategory) TextView tvTaskCategory;

    @BindView(R.id.tilTaskTitle) TextInputLayout tilTaskTitle;
    @BindView(R.id.tilDescription)  TextInputLayout tilDescription;
    @BindView(R.id.tilTaskDate) TextInputLayout tilTaskDate;
    @BindView(R.id.tilTaskAddress) TextInputLayout tilTaskAddress;
    @BindView(R.id.tilTaskPayment) TextInputLayout tilTaskPayment;

    @BindView(R.id.etTaskTitle) EditText etTaskTitle;
    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etTaskDate) EditText etTaskDate;
    @BindView(R.id.etTaskAddress) EditText etTaskAddress;
    @BindView(R.id.etTaskPayment) EditText etTaskPayment;

    @BindView(R.id.ivImageOne) ImageView ivImageOne;
    @BindView(R.id.ivImageTwo) ImageView ivImageTwo;

    private String TASK_CATEGORY_ID, TASK_CATEGORY_NAME;
    private int REQUEST_CODE_IMG_ONE = 100, REQUEST_CODE_IMG_TWO = 200;
    private float MINIMUM_PAYMENT;
    private DatePickerDialog.OnDateSetListener DateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posttask_activity_post_task);
        ButterKnife.bind(this);

        fetchPassedValues();
        changeFontFamily();

        // Make uneditable.
        etTaskAddress.setFocusable(false);
        etTaskDate.setFocusable(false);

        // Set title as fetch task category
        tvTaskCategory.setText(TASK_CATEGORY_NAME);

        // Set minimum payment editText hint
        etTaskPayment.setHintTextColor(getResources().getColor(R.color.colorAccent));
        etTaskPayment.setHint(Float.toString(MINIMUM_PAYMENT));

        // Set task date listener.
        DateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                Log.e("postTaskActivity:" , "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                etTaskDate.setText(date);
            }
        };
    }

    @OnClick({R.id.ivImageOne, R.id.ivImageTwo, R.id.etTaskDate, R.id.btnBack, R.id.btnPost})
    public void setViewOnClickEvent(View view)
    {
        switch(view.getId())
        {
            case R.id.ivImageOne:
                // Send request to upload image from gallery.
                ActivityCompat.requestPermissions(postTaskActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_IMG_ONE);
                break;
            case R.id.ivImageTwo:
                ActivityCompat.requestPermissions(postTaskActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_IMG_TWO);
                break;
            case R.id.etTaskDate:
                displayDatePicker();
                break;
            case R.id.btnBack:
                this.finish();
                break;
            case R.id.btnPost:
                submitTask();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case 100:
            case 200:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode)
        {
            case 100:
            case 200:
                if(resultCode == RESULT_OK && data != null)
                {
                    Uri filePath = data.getData();
                    try
                    {
                        InputStream inputStream = getContentResolver().openInputStream(filePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        setImage(bitmap, requestCode);
                    }
                    catch(FileNotFoundException ex)
                    {
                        Log.e("onActivityResult: ", ex.toString());
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(Bitmap bitmap, int requestCode)
    {
        switch(requestCode)
        {
            case 100:
                ivImageOne.setImageBitmap(bitmap);
                break;
            case 200:
                ivImageTwo.setImageBitmap(bitmap);
                break;
        }
    }

    private void fetchPassedValues()
    {
        TASK_CATEGORY_ID    =  getIntent().getStringExtra("TASK_CATEGORY_ID");
        TASK_CATEGORY_NAME  =  getIntent().getStringExtra("TASK_CATEGORY_NAME");
        MINIMUM_PAYMENT     =  Float.parseFloat(getIntent().getStringExtra("MINIMUM_PAYMENT"));
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

    private void displayDatePicker()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Get min and max date
        long CURRENT_DATE = System.currentTimeMillis();
        long LAST_DATE    = getLastTimeStampOfCurrentMonth(month,year);

        DatePickerDialog dialog = new DatePickerDialog(postTaskActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DateSetListener, year,month,day);
        dialog.getDatePicker().setMinDate(CURRENT_DATE);
        dialog.getDatePicker().setMaxDate(LAST_DATE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static long getLastTimeStampOfCurrentMonth(int month, int year)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year); //leap year 29 Feb;)
        cal.set(Calendar.MONTH, month);
        cal.set(year,month, cal.getActualMaximum(Calendar.DAY_OF_MONTH), cal.getActualMaximum(Calendar.HOUR_OF_DAY),cal.getActualMaximum(Calendar.MINUTE),cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND,cal.getActualMaximum(Calendar.MILLISECOND));
        return cal.getTimeInMillis();
    }


    private void submitTask()
    {
        boolean ERROR_COUNT = false;
        validationUtil validationUtil = new validationUtil();

        if(!validationUtil.isValidTaskTitle(etTaskTitle))
        {
            etTaskTitle.setError("Task title is less than 30 characters!");
            ERROR_COUNT = true;
        }
        if(!validationUtil.isValidTaskDescription(etDescription))
        {
            etDescription.setError("Description is less than 50 characters!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etTaskPayment))
        {
            etTaskPayment.setError("Payment is required!");
            ERROR_COUNT = true;
        }
        if(!validationUtil.isValidTaskPayment(etTaskPayment, MINIMUM_PAYMENT))
        {
            etTaskPayment.setError("Your budget is budget is less than the minimum fee!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etTaskAddress))
        {
            etTaskAddress.setError("Address is required!");
            ERROR_COUNT = true;
        }
        if(validationUtil.isEmpty(etTaskDate))
        {
            etTaskDate.setError("Task date is required!");
            ERROR_COUNT = true;
        }
        if(!ERROR_COUNT)
        {
            sendRequest();
        }
    }

    private void sendRequest()
    {

    }
}
