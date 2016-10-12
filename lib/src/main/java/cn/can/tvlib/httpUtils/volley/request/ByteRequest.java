package cn.can.tvlib.httpUtils.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import cn.can.tvlib.httpUtils.volley.HttpListener;
import cn.can.tvlib.httpUtils.volley.HttpRequest;

public class ByteRequest extends RequestWrapper<byte[]> {

    public ByteRequest(HttpRequest httpRequest, HttpListener<byte[]> listener) {
        super(httpRequest, listener);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
