package com.can.appstore.homerank;

import com.can.appstore.homerank.bean.RankBean;
import com.can.appstore.homerank.utils.GsonUtil;

import java.util.List;

/**
 * Created by yibh on 2016/10/17 10:41 .
 */

public class HomeRankPresenter implements HomeRankContract.Presenter {
    private HomeRankContract.View mView;

    public HomeRankPresenter(HomeRankContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadingData() {
        mView.startLoading();
        mView.getData(getData());
    }

    public List getData(){
        RankBean rankBean = GsonUtil.jsonToBean(mTestData, RankBean.class);
        List<RankBean.DataBean> dataList = rankBean.getData();
        return dataList;
    }


    private String mTestData="{\n" +
            "    \"status\": 0,\n" +
            "    \"message\": \"成功\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"系统工具排行榜\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"鲁大师\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"驱动人生6\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"分区助手\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"一键还原系统\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"CCleaner\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"游戏排行榜\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"穿越火线\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"英雄联盟\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"植物大战僵尸\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"捕鱼达人\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"开心消消乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"教育排行榜\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"少儿学习助手\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"金山打字通\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"每日英语听力\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"唐诗宋词\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"新概念英语\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"音乐排行榜\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"网易云音乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"QQ音乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"酷狗音乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"酷我音乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"虾米音乐\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"办公软件\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"Microsoft Office\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"YoMail\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"Foxmail\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"WPS\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"福昕pdf\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"123\",\n" +
            "            \"name\": \"浏览器排行榜\",\n" +
            "            \"data\": [\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"Chrome\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"Firefox\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"IE9\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"世界之窗\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"id\": 1,\n" +
            "                    \"name\": \"Opera\",\n" +
            "                    \"icon\": \"图标url\",\n" +
            "                    \"marker\": \"======图标或类型======\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
