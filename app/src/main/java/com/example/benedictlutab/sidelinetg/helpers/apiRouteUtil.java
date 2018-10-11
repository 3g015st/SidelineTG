package com.example.benedictlutab.sidelinetg.helpers;

/**
 * Created by Benedict Lutab on 7/16/2018.
 */

public class apiRouteUtil
{
    // Set domain
//    public String DOMAIN = "http://192.168.0.102/Sideline/admin_area/";
//    public String DOMAIN = "http://169.254.253.97/Sideline/";
    public String DOMAIN = "http://192.168.43.218/Sideline/admin_area/";
//    public String DOMAIN = "http://192.168.0.36/Sideline/";
//    public String DOMAIN = "http://192.168.1.9/Sideline/admin_area/";

    // Routes
    public String URL_CHECK_EMAIL_MOBILE_EXISTS    = DOMAIN + "api/taskgiver/isEmailOrMobileExists.php";
    public String URL_SIGNUP                       = DOMAIN + "api/taskgiver/createUserAccount.php";
    public String URL_LOAD_TASK_CATEGORIES         = DOMAIN + "api/taskgiver/loadTaskCategories.php";
    public String URL_POST_TASK                    = DOMAIN + "api/taskgiver/postTask.php";
    public String URL_MY_TASKS                     = DOMAIN + "api/taskgiver/myTasks.php";
    public String URL_TASK_OFFERS                  = DOMAIN + "api/taskgiver/fetchTaskOffers.php";
    public String URL_ASSIGN_TASKER                = DOMAIN + "api/taskgiver/assignTasker.php";
    public String URL_ASSIGNED_TASKER              = DOMAIN + "api/taskgiver/fetchAssignedTasker.php";
    public String URL_MARK_COMPLETE                = DOMAIN + "api/taskgiver/markAsCompleted.php";
    public String URL_UPDATE_PROF_INFO             = DOMAIN + "api/taskgiver/updateProfileInformation.php";

    public String URL_TASKER_SKILLS                = DOMAIN + "api/tasker/mySkills.php";
    public String URL_LOAD_BADGE                   = DOMAIN + "api/tasker/loadBadges.php";

    public String URL_TASK_DETAILS                 = DOMAIN + "api/common/fetchTaskDetails.php";
    public String URL_CANCEL_TASK                  = DOMAIN + "api/common/cancelTask.php";
    public String URL_LOGIN                        = DOMAIN + "api/common/login.php";
    public String URL_CHECK_CONNECTION             = DOMAIN + "api/common/checkConnection.php";
    public String URL_PROFILE_DETAILS              = DOMAIN + "api/common/loadUserInformation.php";
    public String URL_CHANGE_PASS                  = DOMAIN + "api/common/changePassword.php";
    public String URL_LOAD_EVAL                    = DOMAIN + "api/common/loadEvaluationList.php";
    public String URL_FETCH_TASK_HISTORY           = DOMAIN + "api/common/fetchTaskHistory.php";
    public String URL_LOAD_CHAT_ROOMS              = DOMAIN + "api/common/loadChatRooms.php";
    public String URL_LOAD_TASK_HIST_DTLS          = DOMAIN + "api/common/loadTaskHistoryDetails.php";
    public String URL_SEND_EVAL                    = DOMAIN + "api/common/sendEvaluation.php";
    public String URL_FETCH_COMP_CAT               = DOMAIN + "api/common/fetchComplaintCategories.php";
    public String URL_SEND_COMPLAINT               = DOMAIN + "api/common/sendComplaint.php";
    public String URL_EVAL_STATS            = DOMAIN + "api/common/fetchEvaluationStats.php";
    public String URL_TERMS                 = "http://192.168.1.9/Sideline/terms.php";

    // RECOVER ACCOUNT
    public String URL_SEARCH_EMAIL                 = DOMAIN + "api/common/recoverAccount/searchEmail.php";
    public String URL_SEND_VERIFICATION_CODE       = DOMAIN + "api/common/recoverAccount/sendVerificationCode.php";
    public String URL_VERIFY_CODE                  = DOMAIN + "api/common/recoverAccount/verifyCode.php";
    public String URL_CHANGE_PASSWORD              = DOMAIN + "api/common/recoverAccount/changePassword.php";
}
