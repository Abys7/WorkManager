package com.example.workmanaging.model.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import java.util.Date;

@Entity(tableName = "progetti",
        foreignKeys = {
            @ForeignKey(
                entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                entity = Cliente.class,
                parentColumns = "cliente_id",
                childColumns = "cliente_id",
                onDelete = ForeignKey.SET_NULL
            )
        })
public class Progetto {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "progetto_id")
    public int progettoId;

    @ColumnInfo(name = "user_id", index = true)
    public int userId;

    @ColumnInfo(name = "cliente_id", index = true)
    public Integer clienteId;

    @ColumnInfo(name = "titolo")
    public String titolo;

    @ColumnInfo(name = "inizio")
    public Date inizio;

    @ColumnInfo(name = "scadenza")
    public Date scadenza;

    @ColumnInfo(name = "descrizione")
    public String descrizione;

    @ColumnInfo(name = "stato")
    public JobStatus stato;

    public Progetto() {}
}
