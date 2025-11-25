package com.example.workmanaging.model.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "clienti",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ))
public class Cliente {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cliente_id")
    public int clienteId;

    @ColumnInfo(name = "user_id", index = true)
    public int userId;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "azienda")
    public String azienda;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "telefono")
    public String telefono;

    @ColumnInfo(name = "descrizione")
    public String descrizione;

    @ColumnInfo(name = "img")
    public String img;

    public Cliente() {}
}
