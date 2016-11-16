package com.can.appstore.http;

import retrofit2.Response;

public interface CanCallback<T> {
    void onResponse(CanCall<T> call, Response<T> response) throws Exception;

    void onFailure(CanCall<T> call, CanErrorWrapper errorWrapper);

}
