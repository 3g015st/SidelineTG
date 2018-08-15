package com.example.benedictlutab.sidelinetg.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
//    public String DOMAIN = "http://192.168.1.3/Sideline/";
//      public String DOMAIN = "http://192.168.1.7/Sideline/";
    public String DOMAIN = "http://192.168.43.218/Sideline/";

    // Routes
    public String URL_CHECK_EMAIL_EXISTS    = DOMAIN + "api/taskgiver/isEmailExists.php";
    public String URL_SIGNUP                = DOMAIN + "api/taskgiver/createUserAccount.php";
    public String URL_LOAD_TASK_CATEGORIES  = DOMAIN + "api/taskgiver/loadTaskCategories.php";
    public String URL_POST_TASK             = DOMAIN + "api/taskgiver/postTask.php";
    public String URL_MY_TASKS              = DOMAIN + "api/taskgiver/myTasks.php";
    public String URL_TASK_OFFERS           = DOMAIN + "api/taskgiver/fetchTaskOffers.php";
    public String URL_ASSIGN_TASKER         = DOMAIN + "api/taskgiver/assignTasker.php";
    public String URL_LOAD_CHAT_ROOMS       = DOMAIN + "api/taskgiver/loadChatRooms.php";
    public String URL_ASSIGNED_TASKER       = DOMAIN + "api/taskgiver/fetchAssignedTasker.php";

    public String URL_TASK_DETAILS          = DOMAIN + "api/common/fetchTaskDetails.php";
    public String URL_CANCEL_TASK           = DOMAIN + "api/common/cancelTask.php";
    public String URL_LOGIN                 = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION      = DOMAIN + "api/common/checkConnection.php";

}
