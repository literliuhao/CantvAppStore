package com.can.appstore.http;

import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Result;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

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
            if (o instanceof Result) {
                Result result = (Result) o;
                return checkStatus(result.getStatus(), result.getMessage());
            } else if (o instanceof ListResult) {
                ListResult listResult = (ListResult) o;
                return checkStatus(listResult.getStatus(), listResult.getMessage());
            }
        }
        return null;
    }

    static CanErrorWrapper checkStatus(int status, String message) {
        if (status != 0) {
            CanErrorWrapper canErrorWrapper = new CanErrorWrapper();
            canErrorWrapper.reason = message;
            return canErrorWrapper;
        }
        return null;
    }
}
