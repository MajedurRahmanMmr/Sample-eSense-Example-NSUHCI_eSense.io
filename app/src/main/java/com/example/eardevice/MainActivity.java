package com.example.eardevice;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import io.esense.esenselib.ESenseConnectionListener;
import io.esense.esenselib.ESenseEvent;
import io.esense.esenselib.ESenseManager;
import io.esense.esenselib.ESenseSensorListener;

import static com.example.eardevice.Utils.DEVICE_NAME;

public class MainActivity extends AppCompatActivity implements ESenseConnectionListener {

    private ESenseManager eSenseManager;
    private TextView text_data;
    int count = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setUpEsenseItems();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

                finish();
            }


        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at Setting > Permission")
                .setPermissions(Manifest.permission.BLUETOOTH,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


    }

    private void setUpEsenseItems() {
        text_data = findViewById(R.id.data_text);

        eSenseManager = new ESenseManager(DEVICE_NAME, MainActivity.this.getApplicationContext(), MainActivity.this);
        eSenseManager.connect(100);
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
    }


    @Override
    public void onDeviceFound(ESenseManager eSenseManager) {
        Log.e("eSense onDeviceFound: ", eSenseManager.toString());

    }

    @Override
    public void onDeviceNotFound(ESenseManager eSenseManager) {
        Log.e("eSense onDeviceNotFound: ", eSenseManager.toString());

    }

    @Override
    public void onConnected(ESenseManager eSenseManager) {
        Log.e("eSense onConnected: ", eSenseManager.toString());


        eSenseManager.registerSensorListener(new ESenseSensorListener() {
            @Override
            public void onSensorChanged(ESenseEvent eSenseEvent) {


                writeData(eSenseEvent);


                Log.e(" eSense onSensorChanged", eSenseEvent.getTimestamp() + " " + eSenseEvent.getGyro().toString());
            }
        },100);

    }

    @Override
    public void onDisconnected(ESenseManager eSenseManager) {

        Log.e("eSense onDisconnected: ", eSenseManager.toString());

    }


    public void writeData(ESenseEvent eSenseEvent) {


        if (count == 10) {


            count = 0;
            text_data.setText(eSenseEvent.getAccel()[0] + " ");


        } else {
            count++;
        }


    }

}
