package com.example.eardevice;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class SensorData {


    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String deviceName;
    private String accl;
    private String gyro;

    private long timeStamp= System.currentTimeMillis();

    public SensorData() {
        this.timeStamp = System.currentTimeMillis();
    }

    public SensorData(String deviceName, String gyroData, String accelData) {
        this.deviceName = deviceName;
        this.accl = accelData;
        this.timeStamp = System.currentTimeMillis();
        this.gyro = gyroData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAccl() {
        return accl;
    }

    public void setAccl(String accl) {
        this.accl = accl;
    }

    public String getGyro() {
        return gyro;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setGyro(String gyro) {
        this.gyro = gyro;
    }
}
