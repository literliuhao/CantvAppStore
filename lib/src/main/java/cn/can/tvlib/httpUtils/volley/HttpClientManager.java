package cn.can.tvlib.httpUtils.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.can.tvlib.httpUtils.volley.request.ByteRequest;
import cn.can.tvlib.httpUtils.volley.request.GsonRequest;
import cn.can.tvlib.httpUtils.volley.request.JsonArrRequest;
import cn.can.tvlib.httpUtils.volley.request.JsonObjRequest;
import cn.can.tvlib.httpUtils.volley.request.StrRequest;
import okhttp3.OkHttpClient;

public class HttpClientManager implements IHttpClient {

    private static final int[] sLock = new int[0];
    private static HttpClientManager INSTANCE;
    private final RequestQueue mRequestQueue;
    private final Context mContext;

    private HttpClientManager(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context, new OkHttp3Stack(new OkHttpClient()));
    }

    /**
     * 这里使用Application的Context
     *
     * @param context
     * @return
     */
    public static HttpClientManager getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (sLock) {
                if (null == INSTANCE) {
                    INSTANCE = new HttpClientManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 添加请求
     *
     * @param request
     */
    public void addRequest(Request request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancelRequest(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    @Override
    public Request request(HttpRequest httpRequest, HttpListener<String> listener, Object tag) {
        StrRequest request = new StrRequest(httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

    @Override
    public Request byteRequest(HttpRequest httpRequest, HttpListener<byte[]> listener, Object tag) {
        ByteRequest request = new ByteRequest(httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

    @Override
    public Request jsonObjectRequest(HttpRequest httpRequest, HttpListener<JSONObject> listener, Object tag) {
        JsonObjRequest request = new JsonObjRequest(httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

    @Override
    public Request jsonArrayRequest(HttpRequest httpRequest, HttpListener<JSONArray> listener, Object tag) {
        JsonArrRequest request = new JsonArrRequest(httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

    @Override
    public <T> Request gsonRequest(Class<T> tClass, HttpRequest httpRequest, HttpListener<T> listener, Object tag) {
        GsonRequest<T> request = new GsonRequest<T>(tClass, httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

    @Override
    public <T> Request gsonRequest(TypeToken<T> typeToken, HttpRequest httpRequest, HttpListener<T> listener, Object tag) {
        GsonRequest<T> request = new GsonRequest<T>(typeToken, httpRequest, listener);
        addRequest(request, tag);
        return request;
    }

}
