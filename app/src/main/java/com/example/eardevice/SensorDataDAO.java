package com.example.eardevice;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

@Dao
public interface SensorDataDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SensorData data);
}
