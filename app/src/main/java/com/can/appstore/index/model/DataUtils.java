package com.can.appstore.index.model;

import android.content.Context;
import android.util.Log;

import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;

import cn.can.tvlib.common.storage.PreferencesUtils;


/**
 * Created by liuhao on 2016/10/17.
 */

public class DataUtils {

    private Context mContext;

    private ListResult<Navigation> mListResult;

    private DataUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    private static DataUtils instance;

    public static DataUtils getInstance(Context context) {
        if (null == instance) {
            instance = new DataUtils(context.getApplicationContext());
        }
        return instance;
    }

    public static final String INDEX_DATA = "indexData";

    public String getCache() {
        String indexData = PreferencesUtils.getString(mContext, INDEX_DATA);
        if (null != indexData) {
            Log.i("DataUtils", "indexData " + indexData);
            return indexData;
        } else {
            return indexCache;
        }
    }

    public void clearData() {
        PreferencesUtils.putString(mContext, INDEX_DATA, null);
    }

    public void setCache(String mJson) {
        PreferencesUtils.putString(mContext, INDEX_DATA, mJson);
    }

    public void setIndexData(ListResult<Navigation> listResult) {
        mListResult = listResult;
    }

    public ListResult<Navigation> getIndexData() {
        return mListResult;
    }

    public static String indexCache = "{\"status\":0,\"message\":\"\\u6210\\u529f\",\"data\":[{\"id\":\"20\",\"title\":\"\\u63a8\\u8350\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"id\":\"426\",\"title\":\"\\u88c5\\u673a\\u5fc5\\u5907\",\"location\":\"1\",\"action\":\"action_topic_detail\",\"width\":1,\"height\":1,\"x\":0,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583646423b400.jpg\",\"actionData\":\"45\"},{\"id\":\"427\",\"title\":\"\\u6700\\u65b0\\u4e0a\\u67b6\",\"location\":\"2\",\"action\":\"action_topic_detail\",\"width\":1,\"height\":1,\"x\":0,\"y\":1,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836466a1b2b8.jpg\",\"actionData\":\"45\"},{\"id\":\"428\",\"title\":\"\\u6d3b\\u52a8\\u4e13\\u533a\",\"location\":\"3\",\"action\":\"action_activity_detail\",\"width\":1,\"height\":1,\"x\":0,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583646b578a75.jpg\",\"actionData\":\"43\"},{\"id\":\"429\",\"title\":\"\\u8292\\u679cTV\",\"location\":\"4\",\"action\":\"action_app_detail\",\"width\":2,\"height\":2,\"x\":1,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583647050f4bd.jpg\",\"actionData\":\"6063\"},{\"id\":\"430\",\"title\":\"\\u9ad8\\u80fd\\u64a9\\u59b9\",\"location\":\"5\",\"action\":\"action_topic_detail\",\"width\":2,\"height\":1,\"x\":1,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583678fb04867.jpg\",\"actionData\":\"35\"},{\"id\":\"431\",\"title\":\"\\u534e\\u6570TV\",\"location\":\"6\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":3,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367954ab069.jpg\",\"actionData\":\"6064\"},{\"id\":\"432\",\"title\":\"\\u8e0f\\u9752\\u89c5\\u6625\",\"location\":\"7\",\"action\":\"action_topic_detail\",\"width\":2,\"height\":1,\"x\":3,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836799f5e812.jpg\",\"actionData\":\"45\"},{\"id\":\"433\",\"title\":\"\\u94f6\\u6cb3\\u5947\\u5f02\\u679c\",\"location\":\"8\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":4,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583688eb97d43.jpg\",\"actionData\":\"6065\"},{\"id\":\"434\",\"title\":\"\\u624d\\u667a\\u5c0f\\u5929\\u5730\",\"location\":\"9\",\"action\":\"action_app_detail\",\"width\":2,\"height\":2,\"x\":5,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367a11d67b7.jpg\",\"actionData\":\"6066\"},{\"id\":\"435\",\"title\":\"\\u76f4\\u64ad\\u79c0\",\"location\":\"10\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":5,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367a33c7d96.jpg\",\"actionData\":\"6067\"},{\"id\":\"436\",\"title\":\"fitime\",\"location\":\"11\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":6,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367b7830f7a.jpg\",\"actionData\":\"6053\"},{\"id\":\"437\",\"title\":\"\\u5723\\u5251\\u8054\\u76df\",\"location\":\"12\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":7,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583688df4437c.jpg\",\"actionData\":\"6068\"},{\"id\":\"438\",\"title\":\"\\u5357\\u74dc\\u7535\\u5f71\",\"location\":\"13\",\"action\":\"action_app_detail\",\"width\":2,\"height\":1,\"x\":7,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367c13aa95b.jpg\",\"actionData\":\"6072\"},{\"id\":\"439\",\"title\":\"\\u5168\\u7403\\u8d2d\",\"location\":\"14\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":8,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367c7a2766b.jpg\",\"actionData\":\"6057\"},{\"id\":\"440\",\"title\":\"\\u6811\",\"location\":\"15\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":9,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367cf18f66f.jpg\",\"actionData\":\"6058\"},{\"id\":\"441\",\"title\":\"\\u4f18\\u9177\",\"location\":\"16\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":9,\"y\":1,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367d2bd1e13.jpg\",\"actionData\":\"6052\"},{\"id\":\"442\",\"title\":\"\\u5143\\u6c14\\u52c7\\u58eb\",\"location\":\"17\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":9,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367d6e623ad.jpg\",\"actionData\":\"6050\"},{\"id\":\"443\",\"title\":\"\\u91ca\\u9b42\",\"location\":\"18\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":10,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367d99e3c45.jpg\",\"actionData\":\"6049\"},{\"id\":\"444\",\"title\":\"\\u6709\\u4e50\\u6597\\u5730\\u4e3b\",\"location\":\"19\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":10,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58367ddfe3d4b.jpg\",\"actionData\":\"6052\"}]},{\"id\":\"22\",\"title\":\"\\u6392\\u884c\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[]},{\"id\":\"23\",\"title\":\"\\u6559\\u80b2\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"id\":\"457\",\"title\":\"\\u5b55\\u5a74\\u65e9\\u6559\",\"location\":\"1\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836814f68747.jpg\",\"actionData\":\"54\"},{\"id\":\"458\",\"title\":\"\\u804c\\u4e1a\\u6559\\u80b2\",\"location\":\"2\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":1,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58368145d4a07.jpg\",\"actionData\":\"59\"},{\"id\":\"459\",\"title\":\"\\u66f4\\u591a\\u5206\\u7c7b\",\"location\":\"3\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836817697d2a.jpg\",\"actionData\":\"65\"},{\"id\":\"460\",\"title\":\"\\u718a\\u732b\\u62fc\\u97f3\",\"location\":\"4\",\"action\":\"action_app_detail\",\"width\":2,\"height\":2,\"x\":1,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583681af973a3.jpg\",\"actionData\":\"6052\"},{\"id\":\"461\",\"title\":\"\\u611f\\u6069\\u6bcd\\u4eb2\\u8282\",\"location\":\"5\",\"action\":\"action_topic_detail\",\"width\":2,\"height\":1,\"x\":1,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583681da068b2.jpg\",\"actionData\":\"45\"},{\"id\":\"462\",\"title\":\"\\u9c7c\\u4e50\\u8d1d\\u8d1d\",\"location\":\"6\",\"action\":\"action_app_detail\",\"width\":1,\"height\":3,\"x\":3,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583681fe4574a.jpg\",\"actionData\":\"6063\"},{\"id\":\"463\",\"title\":\"\\u5f00\\u5b66\\u5b63\",\"location\":\"7\",\"action\":\"action_topic_detail\",\"width\":1,\"height\":2,\"x\":4,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58368295eafbc.jpg\",\"actionData\":\"35\"},{\"id\":\"464\",\"title\":\"\\u6211\\u56fe\\u5e7c\\u513f\",\"location\":\"8\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":4,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583682b35c8c0.jpg\",\"actionData\":\"6065\"},{\"id\":\"465\",\"title\":\"\\u5b66\\u4e60\",\"location\":\"9\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":5,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583682ce1dbc1.jpg\",\"actionData\":\"6066\"},{\"id\":\"466\",\"title\":\"\\u5feb\\u4e50\\u5b66\\u5802\",\"location\":\"10\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":5,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836831ebb014.jpg\",\"actionData\":\"6067\"}]},{\"id\":\"24\",\"title\":\"\\u5e94\\u7528\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"id\":\"498\",\"title\":\"\\u5f71\\u97f3\\u8d44\\u8baf\",\"location\":\"1\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583685fc104aa.jpg\",\"actionData\":\"61\"},{\"id\":\"499\",\"title\":\"\\u5b9e\\u7528\\u5de5\\u5177\",\"location\":\"2\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":1,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58368617ecdc9.jpg\",\"actionData\":\"60\"},{\"id\":\"500\",\"title\":\"\\u66f4\\u591a\\u5206\\u7c7b\",\"location\":\"3\",\"action\":\"action_app_list\",\"width\":1,\"height\":1,\"x\":0,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583686318b08f.jpg\",\"actionData\":\"65\"},{\"id\":\"501\",\"title\":\"4\\u6708\\u4f18\\u8d28\\u5e94\\u7528\",\"location\":\"4\",\"action\":\"action_topic_detail\",\"width\":2,\"height\":2,\"x\":1,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836865909fb2.jpg\",\"actionData\":\"45\"},{\"id\":\"502\",\"title\":\"\\u53bb\\u54ea\\u513f\\u65c5\\u884c\",\"location\":\"5\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":1,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/58368681f1dbd.jpg\",\"actionData\":\"6052\"},{\"id\":\"503\",\"title\":\"\\u5feb\\u624b\\u770b\\u7247\",\"location\":\"6\",\"action\":\"action_app_detail\",\"width\":1,\"height\":1,\"x\":2,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583686b9ab057.jpg\",\"actionData\":\"6068\"},{\"id\":\"504\",\"title\":\"\\u552f\\u54c1\\u4f1a\",\"location\":\"7\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":3,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583686d87d728.jpg\",\"actionData\":\"6050\"},{\"id\":\"505\",\"title\":\"\\u4e94\\u4e00\\u6b22\\u4e50\\u9882\",\"location\":\"8\",\"action\":\"action_topic_detail\",\"width\":2,\"height\":1,\"x\":3,\"y\":2,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/583686f8a2d3e.jpg\",\"actionData\":\"35\"},{\"id\":\"506\",\"title\":\"\\u542c\\u8bf4\\u4ea4\\u901a\",\"location\":\"9\",\"action\":\"action_app_detail\",\"width\":1,\"height\":2,\"x\":4,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836871f0205d.jpg\",\"actionData\":\"6064\"},{\"id\":\"507\",\"title\":\"\\u6f14\\u6280\\u6d3e\",\"location\":\"10\",\"action\":\"action_app_detail\",\"width\":1,\"height\":3,\"x\":5,\"y\":0,\"icon\":\"http:\\/\\/172.16.11.32:8010\\/upload\\/Recommend\\/2016-11-24\\/5836873711597.jpg\",\"actionData\":\"6065\"}]}]}";


}
