package com.can.appstore.index.model;

import com.can.appstore.index.entity.ChildBean;
import com.can.appstore.index.entity.LayoutBean;
import com.can.appstore.index.entity.PageBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhao on 2016/10/20.
 */

public class JsonFormat {
    public PageBean parseJson(String json) {
        PageBean pageBean = null;
        try {
            pageBean = new PageBean();
            JSONObject mainObject = new JSONObject(json);
            JSONArray jsonArray = mainObject.getJSONArray("data");
            LayoutBean layoutBean;
            List<LayoutBean> layoutLists = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject childObject = new JSONObject(jsonArray.get(i).toString());
                layoutBean = new LayoutBean();
                layoutBean.setId(childObject.getInt("id"));
                layoutBean.setTitle(childObject.getString("title"));
                layoutBean.setBaseWidth(childObject.getInt("baseWidth"));
                layoutBean.setBaseHeight(childObject.getInt("baseHeight"));
                layoutBean.setLineSpace(childObject.getInt("lineSpace"));
                JSONArray childArray = childObject.getJSONArray("layout");
                List<ChildBean> beanList = null;
                beanList = new ArrayList<>();
                for (int j = 0; j < childArray.length(); j++) {
                    JSONObject layoutObject = new JSONObject(childArray.get(j).toString());
                    ChildBean childBean = new ChildBean();
                    childBean.setId(layoutObject.getInt("id"));
                    childBean.setX(layoutObject.getInt("x"));
                    childBean.setY(layoutObject.getInt("y"));
                    childBean.setWidth(layoutObject.getInt("width"));
                    childBean.setHeight(layoutObject.getInt("height"));
                    childBean.setTitle(layoutObject.getString("title"));
                    childBean.setAction(layoutObject.getString("action"));
                    childBean.setActionData(layoutObject.getString("actionData"));
                    childBean.setIcon(layoutObject.getString("icon"));
                    beanList.add(childBean);
                }
                layoutBean.setPages(beanList);
                layoutLists.add(layoutBean);
            }
            pageBean.setPageLists(layoutLists);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pageBean;
    }
}
