package com.kangtech.tauonmusicremote.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
                builder.addHeader("kangtech_31", authToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
