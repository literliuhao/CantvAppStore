package com.can.appstore.http;

import com.google.gson.JsonSyntaxException;

public class CanErrorWrapper {
    public static boolean DEBUG_MODE = false;
    private Throwable throwable;
    private String reason;

    public CanErrorWrapper(Throwable throwable, boolean internal, int httpCode) {
        this.throwable = throwable;
        getReason(throwable, internal, httpCode);
        if(DEBUG_MODE && throwable != null){
            throwable.printStackTrace();
        }
    }

    private void getReason(Throwable throwable, boolean internal, int httpCode) {
        if (internal) {
            reason = "程序内部错误";
        } else if (httpCode >= 400 && httpCode < 500) {
            reason = "";
        } else if (httpCode >= 500) {
            reason = "服务器发生错误";
        }
        if (throwable != null) {
            if (throwable instanceof JsonSyntaxException) {
                reason = "Gson解析错误";
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
}
