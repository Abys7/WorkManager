package com.example.workmanaging.model.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.workmanaging.model.entity.User;
import com.example.workmanaging.model.entity.Cliente;
import com.example.workmanaging.model.entity.Progetto;
import com.example.workmanaging.model.dao.UserDao;
import com.example.workmanaging.model.dao.ClienteDao;
import com.example.workmanaging.model.dao.ProgettoDao;

@Database(entities = {User.class, Cliente.class, Progetto.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ClienteDao clienteDao();
    public abstract ProgettoDao progettoDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "work_managing_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
