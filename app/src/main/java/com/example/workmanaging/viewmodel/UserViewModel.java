package com.example.workmanaging.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.workmanaging.model.entity.User;
import com.example.workmanaging.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = new UserRepository(application);
    }

    public LiveData<User> login(String email, String password) {
        return mRepository.login(email, password);
    }

    public LiveData<User> getUserById(int id) {
        return mRepository.getUserById(id);
    }

    public void insert(User user) {
        mRepository.insert(user);
    }
    
    public void update(User user) {
        mRepository.update(user);
    }
}
