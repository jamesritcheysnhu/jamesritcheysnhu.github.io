package com.zybooks.eventtracker.apiservice;

import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.zybooks.eventtracker.clientmodel.User;
import com.zybooks.eventtracker.networkmodel.AuthenticateRequest;
import com.zybooks.eventtracker.networkmodel.RegisterRequest;
import com.zybooks.eventtracker.networkmodel.UserResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import com.zybooks.eventtracker.backgroundservice.EncryptedDataStore;

// RemoteRepository: access point for APIs. Handles data messaging, but leaves redirects to others
// referemce:
// https://medium.com/@sanjaykushwaha_58217/jwt-authentication-in-android-a-step-by-step-guide-d0dd768cb21a
public class RemoteRepository {

    private final EncryptedDataStore mEncryptedDataStore;
    private final SharedPreferences mSharedPreferences;

    public RemoteRepository(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
        mEncryptedDataStore = new EncryptedDataStore(sharedPreferences);
    }

    public User register (User user) throws IOException, GeneralSecurityException {
        // Making the login request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:3000/users/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        UserApi userApi = retrofit.create(UserApi.class);
        RegisterRequest registerRequest = new RegisterRequest(user.username, user.password);
        // TODO: network calls must be executed on a background thread - throws networkonmainthread exception
        Response<UserResponse> userResponse = userApi.register(registerRequest).execute();

        if (!userResponse.isSuccessful()) {
            throw new IOException("user was not received in response");
        }
        if (userResponse.body() == null || userResponse.body().user() == null) {
            throw new NullPointerException("user response body is missing");
        }

        String token = userResponse.body().token();
        String username = userResponse.body().user().username();

        // encrypt the JWT for local storage
        String encryptedToken = encryptToken(token, username);

        // store encrypted token local keystore
        mSharedPreferences.edit().putString("encrypted_jwt", encryptedToken).apply();

        user.password = ""; // clear password so it is not sent back to client

        return user;
    }

    public User login (User user) throws IOException, GeneralSecurityException {
        // Making the login request
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("localhost:3000/users/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        UserApi userApi = retrofit.create(UserApi.class);
        AuthenticateRequest authRequest = new AuthenticateRequest(user.username, user.password);
        Response<UserResponse> userResponse = userApi.authenticate(authRequest).execute();

        if (!userResponse.isSuccessful()) {
            throw new IOException("user was not received in response");
        }

        if (userResponse.body() == null || userResponse.body().user() == null) {
            throw new NullPointerException("user response body is missing");
        }

        String token = userResponse.body().token();
        String username = userResponse.body().user().username();

        // encrypt the JWT for local storage
        String encryptedToken = encryptToken(token, username);

        // store encrypted token local keystore
        mSharedPreferences.edit().putString("encrypted_jwt", encryptedToken).apply();

        // clear password so it is not sent back to client
        user.password = "";

        return user;
    }

    private String encryptToken(@NonNull String token, @NonNull String username)
            throws GeneralSecurityException, IllegalArgumentException
    {
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // non-encrypted data that associates user with token
        byte[] associatedData = (username + ":auth_jwt").getBytes(StandardCharsets.UTF_8);

        // convert the plaintext token string to a byte array
        byte[] plaintext = token.getBytes(StandardCharsets.UTF_8);

        // encrypt the token
        byte[] ciphertext = mEncryptedDataStore.aead.encrypt(plaintext, associatedData);

        // convert ciphertext byte string to a normal string
        return base64Encode(ciphertext);
    }


    private String decryptToken(String token, String username)
            throws GeneralSecurityException, IllegalArgumentException
    {
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // non-encrypted data that associates user with token
        byte[] associatedData = (username + ":auth_jwt").getBytes(StandardCharsets.UTF_8);

        // decode the encrypted token to a byte array
        byte[] ciphertext = base64Decode(token);

        // decrypt the token
        byte[] plaintext = mEncryptedDataStore.aead.decrypt(ciphertext, associatedData);

        // convert plaintext byte string to a normal string
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private static String base64Encode(final byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    private static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }
}
