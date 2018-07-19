package com.example.benedictlutab.sidelinetg.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
    public String DOMAIN = "http://192.168.1.5/";

    // Routes
    public String URL_CHECK_EMAIL_EXISTS    = DOMAIN + "api/taskgiver/isEmailExists.php";
    public String URL_SIGNUP                = DOMAIN + "api/taskgiver/createUserAccount.php";
    public String URL_LOAD_TASK_CATEGORIES  = DOMAIN + "api/taskgiver/??.php";

    public String URL_LOGIN                 = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION      = DOMAIN + "api/common/checkConnection.php";

}
