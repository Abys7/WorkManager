package com.example.workmanaging.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.workmanaging.model.entity.Progetto;
import java.util.List;

@Dao
public interface ProgettoDao {
    @Insert
    void insert(Progetto progetto);

    @Update
    void update(Progetto progetto);

    @Delete
    void delete(Progetto progetto);

    @Query("SELECT * FROM progetti WHERE user_id = :userId ORDER BY scadenza ASC")
    LiveData<List<Progetto>> getProjectsForUser(int userId);
}
