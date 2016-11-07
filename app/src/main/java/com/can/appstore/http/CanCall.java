package com.can.appstore.http;

import java.io.IOException;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CanCall<T> {
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
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    try {
                        canCallback.onResponse(call, response);
                    } catch (Exception e) {
                        canCallback.onFailure(call, CanErrorWrapper.newInstance(e, true));
                    }
                } else {
                    canCallback.onFailure(call, CanErrorWrapper.newInstance(response.code()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

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
