package com.nesstbase;

import com.nesstbase.apis.AuthApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Temp {
    private AuthApi authApi;

    public void login() {
        authApi.login("", "")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }
}
