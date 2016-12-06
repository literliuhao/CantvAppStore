package com.can.appstore.index.model;

import android.content.Context;

import com.can.appstore.ActionConstants;
import com.can.appstore.AppConstants;
import com.can.appstore.R;
import com.can.appstore.active.ActiveActivity;
import com.can.appstore.appdetail.AppDetailActivity;
import com.can.appstore.applist.AppListActivity;
import com.can.appstore.specialdetail.SpecialDetailActivity;
import com.dataeye.sdk.api.app.channel.DCResourceLocation;
import com.dataeye.sdk.api.app.channel.DCResourcePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuhao on 2016/11/23.
 */

public class ActionUtils {

    /**
     * 内置图片
     */
    private ActionUtils() {
        photoMap.put("recommend_photo1", R.drawable.homerank_bottom_bg1);
        photoMap.put("recommend_photo2", R.drawable.homerank_bottom_bg2);
        photoMap.put("recommend_photo3", R.drawable.homerank_bottom_bg3);
        photoMap.put("recommend_photo4", R.drawable.homerank_bottom_bg4);
        photoMap.put("recommend_photo5", R.drawable.homerank_bottom_bg5);
    }

    private static ActionUtils actionUtils;

    public static ActionUtils getInstance() {
        if (null == actionUtils) {
            actionUtils = new ActionUtils();
        }
        return actionUtils;
    }

    private Map<String, Integer> photoMap = new HashMap();

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
     * @param id
     * @param actionStr
     * @param actionData
     */
    public void convertAction(Context mContext, String id, String actionStr, String actionData) {
        DCResourcePair pair = DCResourcePair.newBuilder().setResourceLocationId(id).build();
        DCResourceLocation.onClick(pair);
        switch (actionStr) {
            //应用详情
            case ActionConstants.ACTION_APP_DETAIL:
                AppDetailActivity.actionStart(mContext, actionData, AppConstants.RESOURCES_POSITION, id);
                break;
            //专题详情
            case ActionConstants.ACTION_TOPIC_DETAIL:
                SpecialDetailActivity.actionStart(mContext, actionData);
                break;
            //应用列表
            case ActionConstants.ACTION_APP_LIST:
                AppListActivity.actionStart(mContext, AppListActivity.MSG_HIDE_MENU_TOP_SHADOW, "", actionData);
                break;
            //活动详情
            case ActionConstants.ACTION_ACTIVITY_DETAIL:
                ActiveActivity.actionStart(mContext, actionData);
                break;
            //特定应用分类
            case ActionConstants.ACTION_APP_LIST_WITH_TYPE:
                try {
                    JSONObject jsonObject = new JSONObject(actionData);
                    String typeId = jsonObject.optString("typeId","");
                    String topicId = jsonObject.optString("topicId","");
                    AppListActivity.actionStart(mContext, AppListActivity.MSG_HIDE_MENU_TOP_SHADOW, typeId, topicId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public Boolean checkURL(String mURL) {
        String regex = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(mURL);
        return matcher.matches();
    }

    public int getResourceId(String iconName) {
        if (photoMap.containsKey(iconName)) {
            return photoMap.get(iconName);
        }else{
            return R.drawable.homerank_bottom_bg1;
        }
    }
}