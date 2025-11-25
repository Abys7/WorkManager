package com.example.workmanaging.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.workmanaging.model.entity.Cliente;
import java.util.List;

@Dao
public interface ClienteDao {
    @Insert
    void insert(Cliente cliente);

    @Update
    void update(Cliente cliente);

    @Delete
    void delete(Cliente cliente);

    @Query("SELECT * FROM clienti WHERE user_id = :userId ORDER BY nome ASC")
    LiveData<List<Cliente>> getClientsForUser(int userId);
}
