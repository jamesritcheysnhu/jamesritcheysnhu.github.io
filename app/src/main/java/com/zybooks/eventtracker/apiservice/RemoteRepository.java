package com.zybooks.eventtracker.apiservice;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import com.zybooks.eventtracker.backgroundservice.MessageMap;
import com.zybooks.eventtracker.clientmodel.User;
import com.zybooks.eventtracker.networkmodel.AuthenticateRequest;
import com.zybooks.eventtracker.networkmodel.RegisterRequest;
import com.zybooks.eventtracker.networkmodel.UserResponse;



// TODO: domain model for i/o, unless just looking up via id (then use int/string)
// TODO: if user does not exist, create one, otherwise notify user it already exists

// RemoteRepository: access point for APIs. Handles data messaging, but leaves redirects to others
// references:
// https://medium.com/@sanjaykushwaha_58217/jwt-authentication-in-android-a-step-by-step-guide-d0dd768cb21a
// https://gullapalli-nikitha.medium.com/retrofit-implementation-using-mvvm-architecture-b7f553f39643
public class RemoteRepository {
    // mUserApi: the JSON API
    private final UserApi mUserApi;

    // mJWTStore: the JWT encryption/decryption and storage
    private final EncryptedJWTStore mJWTStore;

    // sMsgs: possible messages for the view
    private static Messages sMsgs;

    // mMsg: error/status message
    private String mMsg;

    // mUser: the user
    private User mUser;

    public RemoteRepository(@NonNull EncryptedJWTStore encryptedJWTStore) {
        if (sMsgs == null) {
            sMsgs = new Messages();
        }

        mJWTStore = encryptedJWTStore;
        mMsg = "";

        // TODO: base url routes to localhost in dev, switch to network url in production
        // listen on localhost on port 3000
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/users/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        mUserApi = retrofit.create(UserApi.class);
    }

    public User register (User user, @NonNull ResponseCallback userCallback)
            throws IOException, GeneralSecurityException
    {
        // Making the register request
        RegisterRequest registerRequest = new RegisterRequest(user.username, user.password);

        // set API function handle
        Call<UserResponse> call = mUserApi.register(registerRequest);

        // create a background thread
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<UserResponse> call, @NonNull Response<UserResponse> res)
            {
                boolean isEncryptJWTOK;
                String token = "";
                String username = "";
                String remoteId = "";

                // set the output if response is valid
                if (isTokenOK(res)) {
                    token = res.body().token;
                }

                if (isUserOK(res)) {
                    username = res.body().user.username;
                    remoteId = res.body().user.id;
                }

                // encrypt the JWT for local storage
                if (isTokenOK(res) && isUserOK(res)) {
                    try {
                        mJWTStore.encryptToken(token, username);
                        isEncryptJWTOK = true;
                    } catch (GeneralSecurityException e) {
                        isEncryptJWTOK = false;
                    }
                }
                else {
                    isEncryptJWTOK = false;
                }

                boolean isAllOK =
                        isResponseOK(res) && isUserOK(res) && isMsgOK(res) && isEncryptJWTOK;

                // TODO: user does not currently have a local id so set to 0
                //       this will change when the local db storage is reimplemented
                if (isAllOK) {
                    mUser = User.create(0, remoteId, username);
                    mMsg = res.body().msg;
                    userCallback.onSuccess(mUser, mMsg);
                }

                else {
                    // client-side message; value dependent on status
                    String clientMsg = "";

                    if (!isResponseOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_bad_req");
                    }
                    else if (!isBodyOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_no_body");
                    }
                    else if (!isUserOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_no_user");
                    }
                    else if (!isEncryptJWTOK) {
                        clientMsg = sMsgs.get("toast_user_response_bad_token");
                    }

                    // create error message
                    String unknownServerMsg = sMsgs.get("toast_user_response_unknown");
                    String serverMsg = (isMsgOK(res)) ? res.body().msg : unknownServerMsg;
                    mMsg = buildErrorMessage(clientMsg, serverMsg);

                    // create partial user
                    mUser = User.createPartial(user.username);

                    // notify client of changes
                    userCallback.onError(mUser, mMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                // create error message
                String serverMsg = sMsgs.get("toast_user_response_unknown");
                String clientMsg = sMsgs.get("toast_user_response_bad_net");
                mMsg = buildErrorMessage(clientMsg, serverMsg);

                // return partial response including only the original request username parameter
                // WARNING: do not copy password
                mUser = User.createPartial(user.username);

                // notify client of error
                userCallback.onError(mUser, mMsg);
            }
        });

        return mUser;
    }

    public User login (User user, @NonNull ResponseCallback userCallback) {
        // Making the login request
        AuthenticateRequest authRequest = new AuthenticateRequest(user.username, user.password);

        // set API function handle
        Call<UserResponse> call = mUserApi.authenticate(authRequest);

        // create a background thread
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<UserResponse> call, @NonNull Response<UserResponse> res)
            {
                boolean isEncryptJWTOK;
                String token = "";
                String username = "";
                String remoteId = "";

                // set the output if response is valid
                if (isTokenOK(res)) {
                    token = res.body().token;
                }

                if (isUserOK(res)) {
                    username = res.body().user.username;
                    remoteId = res.body().user.id;
                }

                // encrypt the JWT for local storage
                if (isTokenOK(res) && isUserOK(res)) {
                    try {
                        mJWTStore.encryptToken(token, username);
                        isEncryptJWTOK = true;
                    } catch (GeneralSecurityException e) {
                        isEncryptJWTOK = false;
                    }
                }
                else {
                    isEncryptJWTOK = false;
                }

                boolean isAllOK =
                        isResponseOK(res) && isUserOK(res) && isMsgOK(res) && isEncryptJWTOK;

                // TODO: user does not currently have a local id so set to 0
                //       this will change when the local db storage is reimplemented
                if (isAllOK) {
                    mUser = User.create(0, remoteId, username);
                    mMsg = res.body().msg;
                    userCallback.onSuccess(mUser, mMsg);
                }
                else {
                    // client-side message; value dependent on status
                    String clientMsg = "";

                    if (!isResponseOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_bad_req");
                    }
                    else if (!isBodyOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_no_body");
                    }
                    else if (!isUserOK(res)) {
                        clientMsg = sMsgs.get("toast_user_response_no_user");
                    }
                    else if (!isEncryptJWTOK) {
                        clientMsg = sMsgs.get("toast_user_response_bad_token");
                    }

                    // create error message
                    String unknownServerMsg = sMsgs.get("toast_user_response_unknown");
                    String serverMsg = (isMsgOK(res)) ? res.body().msg : unknownServerMsg;
                    mMsg = buildErrorMessage(clientMsg, serverMsg);

                    // create partial user
                    mUser = User.createPartial(user.username);

                    // notify client of changes
                    userCallback.onError(mUser, mMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                // create error message
                String serverMsg = sMsgs.get("toast_user_response_unknown");
                String clientMsg = sMsgs.get("toast_user_response_bad_net");
                mMsg = buildErrorMessage(clientMsg, serverMsg);

                // return partial response including only the original request username parameter
                // WARNING: do not copy password
                mUser = User.createPartial(user.username);

                // notify client of error
                userCallback.onError(mUser, mMsg);
            }
        });

        return mUser;
    }

    public String message() {
        return mMsg;
    }

    private String buildErrorMessage (@NonNull String clientMsg, @NonNull String serverMsg) {
        String msg = "Client response: " + clientMsg + "; ";
        return msg + "Server response: " + serverMsg;
    }

    // response validation helpers: isResponseOK, isBodyOK, isTokenOK, isUserOK, isMsgOK
    private boolean isResponseOK (Response<UserResponse> res) {
        return res.isSuccessful();
    }

    private boolean isBodyOK (Response<UserResponse> res) {
        return res.body() != null;
    }

    private boolean isTokenOK (Response<UserResponse> res) {
        return (res.body() != null) && (res.body().token != null) && !res.body().token.isEmpty();
    }

    private boolean isUserOK (Response<UserResponse> res) {
        return
                (res.body() != null) &&
                (res.body().user != null) &&
                (res.body().user.username != null) &&
                !res.body().user.username.isEmpty();
    }

    private boolean isMsgOK (Response<UserResponse> res) {
        return (res.body() != null) && (res.body().msg != null) && !res.body().msg.isEmpty();
    }

    // Messages: stores a map of name keys and non-UI string text values
    private static class Messages implements MessageMap {
        private final Map<String, String> mMessages;

        public Messages() {
            this.mMessages = Map.of(
                    "toast_user_response_bad_req", "user response not in 200…300 range",
                    "toast_user_response_no_body", "user response body is missing",
                    "toast_user_response_no_user", "missing or invalid user",
                    "toast_user_response_no_msg", "no message found",
                    "toast_user_response_bad_token", "user response token invalid/missing",
                    "toast_user_response_bad_net", "network error",
                    "toast_user_response_unknown", "could not determine server status"
            );
        }

        public String get(String key) {
            return mMessages.get(key);
        }
    }
}
