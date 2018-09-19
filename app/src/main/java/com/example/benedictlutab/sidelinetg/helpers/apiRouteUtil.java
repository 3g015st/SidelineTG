package com.example.benedictlutab.sidelinetg.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
    public String DOMAIN = "http://192.168.0.100/Sideline/admin_area/";
//    public String DOMAIN = "http://169.254.253.97/Sideline/";
//    public String DOMAIN = "http://192.168.43.218/Sideline/admin_area/";
//    public String DOMAIN = "http://192.168.0.36/Sideline/";

    // Routes
    public String URL_CHECK_EMAIL_MOBILE_EXISTS    = DOMAIN + "api/taskgiver/isEmailOrMobileExists.php";
    public String URL_SIGNUP                       = DOMAIN + "api/taskgiver/createUserAccount.php";
    public String URL_LOAD_TASK_CATEGORIES         = DOMAIN + "api/taskgiver/loadTaskCategories.php";
    public String URL_POST_TASK                    = DOMAIN + "api/taskgiver/postTask.php";
    public String URL_MY_TASKS                     = DOMAIN + "api/taskgiver/myTasks.php";
    public String URL_TASK_OFFERS                  = DOMAIN + "api/taskgiver/fetchTaskOffers.php";
    public String URL_ASSIGN_TASKER                = DOMAIN + "api/taskgiver/assignTasker.php";
    public String URL_LOAD_CHAT_ROOMS              = DOMAIN + "api/taskgiver/loadChatRooms.php";
    public String URL_ASSIGNED_TASKER              = DOMAIN + "api/taskgiver/fetchAssignedTasker.php";
    public String URL_MARK_COMPLETE                = DOMAIN + "api/taskgiver/markAsCompleted.php";

    public String URL_TASKER_SKILLS                = DOMAIN + "api/tasker/mySkills.php";

    public String URL_TASK_DETAILS                 = DOMAIN + "api/common/fetchTaskDetails.php";
    public String URL_CANCEL_TASK                  = DOMAIN + "api/common/cancelTask.php";
    public String URL_LOGIN                        = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION             = DOMAIN + "api/common/checkConnection.php";
    public String URL_PROFILE_DETAILS              = DOMAIN + "api/common/viewProfileDetails.php";
    public String URL_CHANGE_PASS                  = DOMAIN + "api/common/changePassword.php";

    // RECOVER ACCOUNT

}
