package cn.can.tvlib.httpUtils.volley2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * volley网络请求工具类<br/><br/>
 *
 * Created by zhangbingyuan on 2016/10/9.<p/>
 *
 * 注：<br/>
 * 1. 必须在App启动时调用init方法初始化此工具类
 */

public class VolleyManager implements RequestManager<VolleyError> {

    private static volatile VolleyManager instance;
    private RequestQueue mRequestQueue;

    private VolleyManager(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context){
        if(instance == null){
            synchronized (VolleyManager.class){
                if(instance == null){
                    instance = new VolleyManager(context);
                }
            }
        }
    }

    public static VolleyManager getInstance() {
        return instance;
    }

    @Override
    public void get(String url, final RequestCallback callback, Object tag, Object subTag) {
        Response.Listener<String> successCallback = null;
        Response.ErrorListener failCallback = null;
        if(callback != null){
            successCallback = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    callback.onSuccess(response);
                }
            };

            failCallback = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFail(error);
                }
            };
        }

        StringRequest req = new StringRequest(Request.Method.GET, url, successCallback, failCallback);
        req.setTag(tag);
        req.setSubTag(subTag);
        req.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(mRequestQueue != null){
            mRequestQueue.add(req);
        } else {
            Log.w(TAG, "Failed to send GET request. ( " + url + " )[RequestQueue == NULL]");
        }
    }

    @Override
    public void post(String url, final Map headers, final Map params, final RequestCallback callback, Object tag, Object subTag) {
        Response.Listener<String> successCallback = null;
        Response.ErrorListener failCallback = null;
        if(callback != null){
            successCallback = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    callback.onSuccess(response);
                }
            };

            failCallback = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.onFail(error);
                }
            };
        }

        StringRequest req = new StringRequest(Request.Method.GET, url, successCallback, failCallback){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers != null) {
                    return headers;
                }
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params != null) {
                    return params;
                }
                return super.getParams();
            }
        };
        req.setTag(tag);
        req.setSubTag(subTag);
        req.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if(mRequestQueue != null){
            mRequestQueue.add(req);
        } else {
            Log.w(TAG, "Failed to send POST request.( " + url + " ) [RequestQueue == NULL]");
        }
    }

    @Override
    public void cancelAllRequest() {
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    @Override
    public void cancelRequest(final Object tag) {
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    if(request instanceof StringRequest){
                        StringRequest req = (StringRequest) request;
                        Object reqTag = req.getTag();
                        if(tag == reqTag){
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }


    public static class StringRequest extends com.android.volley.toolbox.StringRequest {

        private Object subTag;

        public StringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public Object getSubTag() {
            return subTag;
        }

        public void setSubTag(Object secondTag) {
            this.subTag = secondTag;
        }
    }

    public static class XMLRequest extends Request<XmlPullParser> {

        private final Response.Listener<XmlPullParser> mListener;

        public XMLRequest(int method, String url, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            mListener = listener;
        }

        public XMLRequest(String url, Response.Listener<XmlPullParser> listener, Response.ErrorListener errorListener) {
            this(Method.GET, url, listener, errorListener);
        }

        @Override
        protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
            try {
                String xmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new StringReader(xmlString));
                return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (XmlPullParserException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(XmlPullParser response) {
            mListener.onResponse(response);
        }
    }

}
