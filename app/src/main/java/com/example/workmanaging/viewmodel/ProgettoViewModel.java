package com.example.workmanaging.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.workmanaging.model.entity.Progetto;
import com.example.workmanaging.repository.ProgettoRepository;
import java.util.List;

public class ProgettoViewModel extends AndroidViewModel {
    private ProgettoRepository mRepository;

    public ProgettoViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ProgettoRepository(application);
    }

    public LiveData<List<Progetto>> getProjectsForUser(int userId) {
        return mRepository.getProjectsForUser(userId);
    }

    public void insert(Progetto progetto) {
        mRepository.insert(progetto);
    }
    
    public void delete(Progetto progetto) {
        mRepository.delete(progetto);
    }
    
    public void update(Progetto progetto) {
        mRepository.update(progetto);
    }
}
