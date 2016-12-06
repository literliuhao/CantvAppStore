package com.can.appstore.http;

import com.can.appstore.entity.ResultWrapper;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.net.UnknownHostException;

public class CanErrorWrapper {
    private static boolean DEBUG_MODE = false;
    private Throwable throwable;
    private String reason;

    private CanErrorWrapper() {
    }

    private CanErrorWrapper(Throwable throwable, boolean internal, int httpCode) {
        this.throwable = throwable;
        getReason(throwable, internal, httpCode);
        if (DEBUG_MODE && throwable != null) {
            throwable.printStackTrace();
        }
    }

    private void getReason(Throwable throwable, boolean internal, int httpCode) {
        if (internal) {
            reason = "程序内部发生错误";
        } else if (httpCode >= 400 && httpCode < 500) {
            reason = "HTTP请求错误";
        } else if (httpCode >= 500) {
            reason = "服务器发生错误";
        } else if (throwable != null) {
            if (throwable instanceof JsonSyntaxException
                    || throwable instanceof JsonParseException) {
                reason = "Gson解析错误";
            } else if (throwable instanceof UnknownHostException) {
                reason = "域名解析错误";
            } else {
                reason = "未知错误";
            }
        }
        // TODO 待完善
    }

    static CanErrorWrapper newInstance(int httpCode) {
        return new CanErrorWrapper(null, false, httpCode);
    }

    static CanErrorWrapper newInstance(Throwable throwable, boolean internal) {
        return new CanErrorWrapper(throwable, internal, 0);
    }

    public static void setDebugMode(boolean debug) {
        DEBUG_MODE = debug;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getReason() {
        return reason;
    }

    static CanErrorWrapper errorCheck(Object o) {
        if (o != null) {
            if (o instanceof ResultWrapper) {
                ResultWrapper result = (ResultWrapper) o;
                if (!result.isSuccessful()) {
                    CanErrorWrapper canErrorWrapper = new CanErrorWrapper();
                    canErrorWrapper.reason = result.getMessage();
                    return canErrorWrapper;
                }
            }
        }
        return null;
    }
}
