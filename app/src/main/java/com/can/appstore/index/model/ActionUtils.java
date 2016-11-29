package com.can.appstore.index.model;

import android.content.Context;

import com.can.appstore.active.ActiveActivity;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.applist.AppListActivity;
import com.can.appstore.specialdetail.SpecialDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuhao on 2016/11/23.
 */

public class ActionUtils {

    /**
     * 应用详情 ok
     * com.can.appstore.ACTION_APP_DETAIL
     * appID(String)
     * topicId(String)
     * <p>
     * 应用列表/排行列 表 ok
     * com.can.appstore.ACTION_APPLIST
     * srcType(int,应用0x101,排 行0x102)
     * typeId(String,大类别)
     * topicId(String，左侧小类 别)
     * <p>
     * 专题详情⻚ ok
     * com.can.appstore.ACTION_TOPIC_DETAIL
     * topicId(String)
     * <p>
     * 活动详情⻚ ok
     * com.can.appstore.ACTION_ACTIVITY_DETAIL
     * activeId(String)
     *
     * @param mContext
     * @param actionStr
     * @param actionData
     */
    public static void convertAction(Context mContext, String actionStr, String actionData) {
        switch (actionStr) {
            //应用详情
            case "action_app_detail":
                AppDetailActivity.actionStart(mContext, actionData);
                break;
            //专题详情
            case "action_topic_detail":
                SpecialDetailActivity.actionStart(mContext, actionData);
                break;
            //应用列表
            case "action_app_list":
                AppListActivity.actionStart(mContext, AppListActivity.MSG_HIDE_MENU_TOP_SHADOW, "", actionData);
                break;
            //活动详情
            case "action_app_activity_detail":
                ActiveActivity.actionStart(mContext, actionData);
                break;
            //特定应用分类
            case "action_app_list_with_type":
                try {
                    JSONObject jsonObject = new JSONObject(actionData);
                    String typeId = jsonObject.getString("typeId");
                    String topicId = jsonObject.getString("topicId");
                    AppListActivity.actionStart(mContext, AppListActivity.MSG_HIDE_MENU_TOP_SHADOW, typeId, topicId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
