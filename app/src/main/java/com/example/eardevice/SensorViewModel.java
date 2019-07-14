package com.example.eardevice;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;


public class SensorViewModel extends AndroidViewModel {

    private DataRepository mRepository;

    public SensorViewModel(Application application) {
        super(application);
        mRepository = new DataRepository(application);
    }


    public LiveData<Integer> getCountObserve() {
        return mRepository.getRowCount();
    }

}