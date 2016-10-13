package cn.can.tvlib.httpUtils.volley2;

import java.util.Map;

/**
 * Created by zhangbingyuan on 2016/10/9.
 */

public interface RequestManager<ErrorType> {

    /**
     * GET 请求
     *
     * @param url
     * @param callback
     * @param tag      request tag，建议为关联的页面类名，方便在页面退出时取消所有网络请求
     * @param subTag   request tag，建议保证其唯一性，方便在某些特殊情况下取消某个网络请求
     */
    void get(String url, RequestCallback<ErrorType> callback, Object tag, Object subTag);

    /**
     * POST 请求
     *
     * @param url
     * @param callback
     * @param headers
     * @param params
     * @param tag      request tag，建议为关联的页面类名，方便在页面退出时取消所有网络请求
     * @param subTag   request tag，建议保证其唯一性，方便在某些特殊情况下取消某个网络请求
     */
    void post(String url, Map<String, String> headers, Map<String, String> params, RequestCallback<ErrorType> callback,
              Object tag, Object subTag);

    void cancelAllRequest();

    void cancelRequest(Object tag);

    interface RequestCallback<ErrorType> {

        void onSuccess(String response);

        void onFail(ErrorType error);
    }

}
