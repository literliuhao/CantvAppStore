package cn.can.tvlib.httpUtils.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import cn.can.tvlib.httpUtils.volley.HttpListener;
import cn.can.tvlib.httpUtils.volley.HttpRequest;
public class StrRequest extends RequestWrapper<String> {

    public StrRequest(HttpRequest httpRequest, HttpListener<String> listener) {
        super(httpRequest, listener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String result = getResponseString(response);
        if (result.equals(PARSEERROR)) {
            return Response.error(new ParseError());
        }
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }
}