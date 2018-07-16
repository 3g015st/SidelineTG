package com.example.benedictlutab.sidelinetg.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
    public String DOMAIN = "http://192.168.35.2/";

    // Routes
    public String URL_CHECK_EMAIL_EXISTS = DOMAIN + "api/validation/email_exists.php";
    public String URL_SIGNUP             = DOMAIN + "api/taskgiver/signup.php";
    public String URL_LOGIN              = DOMAIN + "api/common/login.php";
}
