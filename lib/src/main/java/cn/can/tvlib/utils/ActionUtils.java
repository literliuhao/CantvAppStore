package cn.can.tvlib.utils;

/**
 * Created by liuhao on 2016/11/23.
 */

public class ActionUtils {
//    应用详情 ok
//    com.can.appstore.ACTION_APP_DETAIL
//    appID(String)
//    topicId(String)

//    应用列表/排行列 表 ok
//    com.can.appstore.ACTION_APPLIST
//    srcType(int,应用0x101,排 行0x102)
//    typeId(String,大类别)
//    topicId(String，左侧小类 别)

//    专题详情⻚ ok
//    com.can.appstore.ACTION_TOPIC_DETAIL
//    topicId(String)

//    活动详情⻚ ok
//    com.can.appstore.ACTION_ACTIVITY_DETAIL
//    activeId(String)

//    消息中心 ok
//    com.can.appstore.ACTION_MESSAGE

    public static String convertAction(String actionStr) {
        String action;
        switch (actionStr) {
            //应用详情
            case "action_app_detail":
                action = "com.can.appstore.ACTION_APP_DETAIL";
                break;
            //专题详情
            case "action_topic_detail":
                action = "com.can.appstore.ACTION_TOPIC_DETAIL";
                break;
            //应用列表
            case "action_app_list":
                action = "com.can.appstore.ACTION_APPLIST";
                break;
            //活动详情⻚
            case "action_app_activity_detail":
                action = "com.can.appstore.ACTION_ACTIVITY_DETAIL";
                break;
            //消息中心
            case "action_action_message":
                action = "com.can.appstore.ACTION_MESSAGE";
                break;
            default:
                action = "";
        }
        return action;
    }


}
