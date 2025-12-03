package com.example.workmanaging.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.workmanaging.model.dao.ClienteDao;
import com.example.workmanaging.model.database.AppDatabase;
import com.example.workmanaging.model.entity.Cliente;
import java.util.List;

public class ClienteRepository {
    private ClienteDao mClienteDao;

    public ClienteRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mClienteDao = db.clienteDao();
    }

    public LiveData<List<Cliente>> getClientsForUser(int userId) {
        return mClienteDao.getClientsForUser(userId);
    }

    public void insert(Cliente cliente, OnInsertListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = mClienteDao.insert(cliente);
            if (listener != null) {
                listener.onInsert(id);
            }
        });
    }

    public interface OnInsertListener {
        void onInsert(long id);
    }

    public void delete(Cliente cliente) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mClienteDao.delete(cliente);
        });
    }
    
    public void update(Cliente cliente) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mClienteDao.update(cliente);
        });
    }
}
