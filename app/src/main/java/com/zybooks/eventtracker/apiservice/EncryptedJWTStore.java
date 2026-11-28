package com.zybooks.eventtracker.apiservice;

import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.zybooks.eventtracker.backgroundservice.EncryptedDataStore;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * EncryptedJWTStore:
 * encrypts and decrypts JWT from SharedPreferences
 * Tink AEAD uses Android Keystore to store the encryption keys
 */
public class EncryptedJWTStore {
    private static EncryptedDataStore sEncryptedDataStore = null;
    private final SharedPreferences mSharedPreferences;

    public EncryptedJWTStore (SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
        if (sEncryptedDataStore == null) {
            sEncryptedDataStore = new EncryptedDataStore(sharedPreferences);
        }
    }

    public void encryptToken(@NonNull String token, @NonNull String username)
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
        byte[] ciphertext = sEncryptedDataStore.aead.encrypt(plaintext, associatedData);

        // convert ciphertext byte string to a normal string
        String encryptedToken = base64Encode(ciphertext);

        // store encrypted token local keystore
        mSharedPreferences.edit().putString("encrypted_jwt", encryptedToken).apply();
    }


    //private String decryptToken(String token, String username)
    public String decryptToken(String username)
            throws GeneralSecurityException, IllegalArgumentException
    {
        // get encrypted token from shared preferences
        String token = mSharedPreferences.getString("encrypted_jwt", "");

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

        // decrypt the token using the encryption key stored in the encrypted data store
        byte[] plaintext = sEncryptedDataStore.aead.decrypt(ciphertext, associatedData);

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
