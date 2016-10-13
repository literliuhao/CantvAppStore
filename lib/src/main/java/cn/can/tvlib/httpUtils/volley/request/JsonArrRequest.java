package cn.can.tvlib.httpUtils.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import cn.can.tvlib.httpUtils.volley.HttpListener;
import cn.can.tvlib.httpUtils.volley.HttpRequest;

public class JsonArrRequest extends RequestWrapper<JSONArray> {

    public JsonArrRequest(HttpRequest httpRequest, HttpListener<JSONArray> listener) {
        super(httpRequest, listener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        String result = getResponseString(response);
        if (result.equals(PARSEERROR)) {
            return Response.error(new ParseError());
        }
        try {
            return Response.success(new JSONArray(result), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
