package com.example.workmanaging.model.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "utente", 
        indices = {@Index(value = "email", unique = true)})
public class User {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "nome_utente")
    public String nomeUtente;

    @ColumnInfo(name = "professione")
    public String professione;

    @ColumnInfo(name = "img")
    public String img;

    public User() {}
}
