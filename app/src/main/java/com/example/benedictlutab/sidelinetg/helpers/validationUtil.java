package com.example.benedictlutab.sidelinetg.helpers;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class validationUtil
{
    // General Validations
    public boolean isValidEmail(EditText text)
    {
        CharSequence email =  text.getText().toString();
        return(!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean isValidPassword(EditText text)
    {
        String password = text.getText().toString();

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public boolean isEmpty (EditText text)
    {
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

    public boolean isValidPhone(EditText text)
    {
        String phone = text.getText().toString();

        Pattern pattern;
        Matcher matcher;
        final String PHONE_PATTERN = "(\\+639)\\d{9}$";

        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone);

        return matcher.matches();
    }

    public boolean isValidCity(EditText text, String[] cities)
    {
        String city = text.getText().toString();

        // Convert String Array to List
        List<String> cityList = Arrays.asList(cities);

        return (cityList.contains(city));
    }

    // Post a Task Validations
    public boolean isValidTaskTitle(EditText text)
    {
        String title = text.getText().toString();

        if(title.length() < 15)
        {
            return false;
        }
        else
            return true;
    }

    public boolean isValidTaskDescription(EditText text)
    {
        String description = text.getText().toString();

        if(description.length() < 20)
        {
            return false;
        }
        else
            return true;
    }

    public boolean isValidTaskPayment(EditText text, float minimumPayment)
    {
        float payment = 0.0f;

        if(!text.getText().toString().isEmpty())
        {
            payment = Float.parseFloat(text.getText().toString());
        }

        if(payment < minimumPayment)
        {
            return false;
        }
        else
            return true;
    }
}
