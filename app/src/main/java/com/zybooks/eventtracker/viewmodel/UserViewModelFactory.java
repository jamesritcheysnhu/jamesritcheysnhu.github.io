package com.zybooks.eventtracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.zybooks.eventtracker.apiservice.RemoteRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final RemoteRepository mRemoteRepo;

    public UserViewModelFactory(
            @NonNull Application application, @NonNull RemoteRepository remoteRepo)
    {
        mApplication = application;
        mRemoteRepo = remoteRepo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T)new UserViewModel(mApplication, mRemoteRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
