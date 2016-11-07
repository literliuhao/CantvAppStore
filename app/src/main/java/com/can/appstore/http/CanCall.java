package com.can.appstore.http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CanCall<T> {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private final Call<T> rawCall;


    public CanCall(Call<T> rawCall) {
        this.rawCall = rawCall;
    }


    public Response<T> execute() throws IOException {
        return rawCall.execute();
    }

    public void enqueue(final CanCallback<T> canCallback) {
        rawCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(final Call<T> call, final Response<T> response) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            CanErrorWrapper canErrorWrapper = CanErrorWrapper.errorCheck(response.body());
                            if (canErrorWrapper != null) {
                                canCallback.onFailure(CanCall.this, canErrorWrapper);
                                return;
                            }
                            try {
                                canCallback.onResponse(CanCall.this, response);
                            } catch (Exception e) {
                                canCallback.onFailure(CanCall.this, CanErrorWrapper.newInstance(e, true));
                            }
                        } else {
                            canCallback.onFailure(CanCall.this, CanErrorWrapper.newInstance(response.code()));
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call<T> call, final Throwable t) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        canCallback.onFailure(CanCall.this, CanErrorWrapper.newInstance(t, false));
                    }
                });
            }
        });
    }

    public boolean isExecuted() {
        return rawCall.isExecuted();
    }


    public void cancel() {
        rawCall.cancel();
    }


    public boolean isCanceled() {
        return rawCall.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public CanCall<T> clone() {
        return new CanCall<>(rawCall.clone());
    }

    public Request request() {
        return rawCall.request();
    }

}
