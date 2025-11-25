package com.example.workmanaging.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.repository.ClienteRepository;
import java.util.List;

public class ClienteViewModel extends AndroidViewModel {
    private ClienteRepository mRepository;

    public ClienteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ClienteRepository(application);
    }

    public LiveData<List<Cliente>> getClientsForUser(int userId) {
        return mRepository.getClientsForUser(userId);
    }

    public void insert(Cliente cliente) {
        mRepository.insert(cliente);
    }
    
    public void delete(Cliente cliente) {
        mRepository.delete(cliente);
    }
    
    public void update(Cliente cliente) {
        mRepository.update(cliente);
    }
}
