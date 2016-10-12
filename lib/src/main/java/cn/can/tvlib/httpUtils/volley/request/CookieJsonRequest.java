package cn.can.tvlib.httpUtils.volley.request;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.can.tvlib.httpUtils.volley.HttpListener;
import cn.can.tvlib.httpUtils.volley.HttpRequest;

/**
 * 返回json格式数据，带有Cookie返回值
 * Created by ljtyzhr on 2016/1/26.
 */
public class CookieJsonRequest extends RequestWrapper<JSONObject> {

    private Response.Listener<JSONObject> mListener;
    public String cookieFromResponse;
    private String mHeader;
    private Map<String, String> mParams;
    private Map<String, String> sendHeader = new HashMap<String, String>(1);

    /**
     * 默认可以使用GET请求
     * @param httpRequest
     * @param listener
     */
    public CookieJsonRequest(HttpRequest httpRequest, HttpListener<JSONObject> listener) {
        super(httpRequest, listener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            mHeader = response.headers.toString();

            //使用正则表达式从reponse的头中提取cookie内容的子串
            Pattern pattern=Pattern.compile("Set-Cookie.*?;");
            Matcher m=pattern.matcher(mHeader);
            if(m.find()){
                cookieFromResponse = m.group();
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            if (!TextUtils.isEmpty(cookieFromResponse)){
                //去掉cookie末尾的分号
                cookieFromResponse = cookieFromResponse.substring(11,cookieFromResponse.length()-1);
                //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
                jsonObject.put("Cookie",cookieFromResponse);
            }
            return Response.success(jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return sendHeader;
    }
    
    /**
     * 设置发送时候的Cookie
     * @param cookie
     */
    public void setSendCookie(String cookie){
        sendHeader.put("Cookie",cookie);
    }
}
