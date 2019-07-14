package com.example.eardevice;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SensorDataDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SensorData data);


    @Query("SELECT COUNT(*) as count FROM SensorData;")
    LiveData<Integer> getTotalCount();


    @Query("SELECT *  FROM SensorData;")
    List<SensorData> getAllData();


    @Query("DELETE FROM SensorData")
    public void deleteAll();
}
