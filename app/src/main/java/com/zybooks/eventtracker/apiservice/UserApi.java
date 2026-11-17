package com.zybooks.eventtracker.apiservice;

import com.zybooks.eventtracker.networkmodel.RegisterRequest;
import com.zybooks.eventtracker.networkmodel.AuthenticateRequest;
import com.zybooks.eventtracker.networkmodel.UserResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

// TODO: the data models should be called AuthenticateRequest, RegisterRequest, and UserResponse
//       each model contains the matching JSON and
public interface UserApi {
    @POST("users/authenticate")
    Call<UserResponse> authenticate(@Body AuthenticateRequest request);

    @POST("users/register")
    Call<UserResponse> register(@Body RegisterRequest request);
}
