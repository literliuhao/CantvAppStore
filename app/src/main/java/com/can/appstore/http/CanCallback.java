package com.can.appstore.http;

import retrofit2.Call;
import retrofit2.Response;

public interface CanCallback<T> {
    void onResponse(Call<T> call, Response<T> response) throws Exception;

    void onFailure(Call<T> call, CanErrorWrapper errorWrapper);

}
