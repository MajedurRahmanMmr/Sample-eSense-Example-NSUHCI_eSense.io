package com.example.eardevice;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class SensorData {


    @PrimaryKey(autoGenerate = true)
    Long id;

    String deviceName;
    String accl;
    String gyro;
}
