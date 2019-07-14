package com.example.eardevice;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import io.esense.esenselib.ESenseConnectionListener;
import io.esense.esenselib.ESenseEvent;
import io.esense.esenselib.ESenseManager;
import io.esense.esenselib.ESenseSensorListener;

import static com.example.eardevice.Utils.DEVICE_NAME;

public class MainActivity extends AppCompatActivity implements ESenseConnectionListener {

    private ESenseManager eSenseManager;
    private TextView text_data;
    int count = 0;
    private EditText deviceid_edittext;
    private TextView device_status;
    private ProgressBar progressBar;
    private String deviceName;
    private SensorDataDAO sensorDataDAO;
    private TextView gyroText;
    private TextView accelText;
    private Button connectButton;
    private SensorViewModel sensorViewModel;
    private TextView data_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorViewModel = ViewModelProviders.of(this).get(SensorViewModel.class);


        data_count = findViewById(R.id.data_count);

        sensorDataDAO = DataHandler.getDatabase(this).getSensorDataDAO();

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
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .check();


        sensorViewModel.getCountObserve().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                data_count.setText(integer + "");
            }
        });


    }

    private void setUpEsenseItems() {
/*
        text_data = findViewById(R.id.data_text);
*/


        deviceid_edittext = findViewById(R.id.device_id_edittext);
        device_status = findViewById(R.id.status_message);
        progressBar = findViewById(R.id.progressBar);
        gyroText = findViewById(R.id.gyroscope_value);
        accelText = findViewById(R.id.accelerometer_value);


        connectButton = findViewById(R.id.reload);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = deviceid_edittext.getText().toString();


                if (!deviceId.isEmpty()) {
                    deviceName = DEVICE_NAME + deviceId;
                    progressBar.setVisibility(View.VISIBLE);

                    device_status.setText("Connecting with " + deviceName);
                    eSenseManager = new ESenseManager(deviceName, MainActivity.this.getApplicationContext(), MainActivity.this);
                    eSenseManager.connect(100);
                } else {
                    Toast.makeText(MainActivity.this, "Device ID can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        findViewById(R.id.export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToCsv();
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                clearData();
            }
        });


        /*
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eSenseManager.getSensorConfig();

            }
        });*/
    }

    private void clearData() {

        sensorDataDAO.deleteAll();
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
    public void onConnected(final ESenseManager eSenseManager) {
        Log.e("eSense onConnected: ", eSenseManager.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                connectButton.setVisibility(View.INVISIBLE);
                device_status.setText("Connected with " + deviceName);
            }
        });

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
            short[] gyro = eSenseEvent.getGyro();
            short[] accel = eSenseEvent.getGyro();
            String gyroData = gyro[0] + ":" + gyro[1] + ":" + gyro[2];
            String accelData = accel[0] + ":" + accel[1] + ":" + accel[2];
            SensorData sensorData = new SensorData(deviceName, gyroData, accelData);

            gyroText.setText(gyroData);
            accelText.setText(accelData);
            sensorDataDAO.insert(sensorData);
        } else {
            count++;
        }


    }


    public void exportToCsv() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("loading");
        pd.show();
        File dataDirectory = Environment.getDataDirectory();
        List<SensorData> allData = sensorDataDAO.getAllData();

        String path = Environment.getExternalStorageDirectory().toString();
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh:mm:ss");
        String strDate = dateFormat.format(date);
        File file = new File(path + "/sensor_data_" + strDate + ".csv");
        CsvWriter csvWriter = new CsvWriter();

        try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            // header
            csvAppender.appendLine("Accelerometer", "Gyro", "TimeStamp");
            // 1st line in one operation
            for (SensorData item : allData) {
                csvAppender.appendLine(item.getAccl(), item.getGyro(), item.getTimeStamp() + "");
            }
            csvAppender.endLine();


            Toast.makeText(this, "Saved in " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }

        pd.dismiss();
    }

}
