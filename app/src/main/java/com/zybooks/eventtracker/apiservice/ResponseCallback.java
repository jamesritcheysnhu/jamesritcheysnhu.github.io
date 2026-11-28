package com.zybooks.eventtracker.apiservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zybooks.eventtracker.clientmodel.User;

public interface ResponseCallback {
    void onSuccess(@NonNull User user, @NonNull String message);
    void onError(@NonNull User user, @NonNull String message);
}
