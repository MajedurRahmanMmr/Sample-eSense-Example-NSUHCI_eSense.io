package com.example.eardevice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.esense.esenselib.ESenseConnectionListener;
import io.esense.esenselib.ESenseEvent;
import io.esense.esenselib.ESenseManager;
import io.esense.esenselib.ESenseSensorListener;

public class MainActivity extends AppCompatActivity implements ESenseConnectionListener {

    private ESenseManager eSenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        eSenseManager = new ESenseManager("eSense-0289", MainActivity.this.getApplicationContext(), this);
        eSenseManager.connect(100);


        Log.e("onCreate: ", eSenseManager.isConnected() + "");


        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eSenseManager.connect(100);
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                eSenseManager.getSensorConfig();

            }
        });


        eSenseManager.registerSensorListener(new ESenseSensorListener() {
            @Override
            public void onSensorChanged(ESenseEvent eSenseEvent) {


                Log.e("onSensorChanged", eSenseEvent.toString());
            }
        }, 1000);

    }


    @Override
    public void onDeviceFound(ESenseManager eSenseManager) {
        Log.e("onDeviceFound: ", eSenseManager.toString());

    }

    @Override
    public void onDeviceNotFound(ESenseManager eSenseManager) {
        Log.e("onDeviceNotFound: ", eSenseManager.toString());

    }

    @Override
    public void onConnected(ESenseManager eSenseManager) {
        Log.e("onConnected: ", eSenseManager.toString());


        eSenseManager.registerSensorListener(new ESenseSensorListener() {
            @Override
            public void onSensorChanged(ESenseEvent eSenseEvent) {



                Log.e("onSensorChanged" , eSenseEvent.getTimestamp() +" " + eSenseEvent.getGyro().toString() );
            }
        },100);

    }

    @Override
    public void onDisconnected(ESenseManager eSenseManager) {

        Log.e("onDisconnected: ", eSenseManager.toString());

    }
}
