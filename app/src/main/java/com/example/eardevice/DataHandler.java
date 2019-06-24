package com.example.eardevice;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {SensorData.class}, version = 1)
public abstract class DataHandler extends RoomDatabase {
    public abstract SensorDataDAO getSensorDataDAO();

    private static volatile DataHandler INSTANCE;

    static DataHandler getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DataHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DataHandler.class, "sensor_database.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}