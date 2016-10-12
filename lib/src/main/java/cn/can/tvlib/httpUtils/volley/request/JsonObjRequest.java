package cn.can.tvlib.httpUtils.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import cn.can.tvlib.httpUtils.volley.HttpListener;
import cn.can.tvlib.httpUtils.volley.HttpRequest;

public class JsonObjRequest extends RequestWrapper<JSONObject> {

    public JsonObjRequest(HttpRequest httpRequest, HttpListener<JSONObject> listener) {
        super(httpRequest, listener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String result = getResponseString(response);
        if (result.equals(PARSEERROR)) {
            return Response.error(new ParseError());
        }
        try {
            return Response.success(new JSONObject(result), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
