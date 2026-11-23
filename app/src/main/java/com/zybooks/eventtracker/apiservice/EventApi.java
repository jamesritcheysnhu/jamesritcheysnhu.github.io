package com.zybooks.eventtracker.apiservice;

import com.zybooks.eventtracker.networkmodel.AuthenticateRequest;
import com.zybooks.eventtracker.networkmodel.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

// TODO: there should be data models for create, update, get, getAll, delete;
//       there should also be data model for event response
public interface EventApi {
    @POST("users/authenticate")
    Call<UserResponse> authenticate(@Body AuthenticateRequest request);

    //@GET("users/{userId}/events")
    //Call<List<EventResponse>> getUserEvents(@Path("userId") String userId);
}
