package com.example.eardevice;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;


public class DataRepository {

    private SensorDataDAO zoneDAO;
    Context context;

    public DataRepository(Application context) {
        DataHandler db = DataHandler.getDatabase(context);
        zoneDAO = db.getSensorDataDAO();
        this.context = context;
    }

    public DataRepository(Context context) {
        DataHandler db = DataHandler.getDatabase(context);
        zoneDAO = db.getSensorDataDAO();

    }


    public LiveData<Integer> getRowCount() {
        return zoneDAO.getTotalCount();
    }


}