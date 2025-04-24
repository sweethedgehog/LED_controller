package com.example.ledcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BleScanner";

    private boolean initSuccess = false;
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    BluetoothFunc onDeviceFound = new BluetoothFunc(){
        @Override
        public void run(BluetoothPeripheral peripheral){
            String name = peripheral.getName();
            if (!name.equals("")) {
                Log.i(TAG, "onConnectingPeripheral: " + peripheral.getName());
                if (!devices.contains(peripheral)) {
                    devices.add(peripheral);
                    updateListView();
                }
            }
        }
    };

    ListView listView;
    ArrayList<BluetoothPeripheral> devices;

    @Override
    protected void onStop(){
        super.onStop();
        BluetoothManager.stopScan();
        devices.clear();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateListView();
        if (initSuccess)
            BluetoothManager.startScan();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemClick: scan stopped");
                BluetoothManager.stopScan();
                BluetoothManager.connect(devices.get(i));
                devices.clear();
                Toast.makeText(MainActivity.this, "Подключение...", Toast.LENGTH_SHORT).show();
            }
        });
        devices = new ArrayList<>();

        if (ProjectManager.initBluetooth(this, this, onDeviceFound, new BluetoothFunc() {
            @Override
            public void run() {
                switch (ProjectManager.currMode){
                    case 0:
                        startActivity(ProjectManager.singleColorIntent);
                        break;
                    case 1:
                        startActivity(ProjectManager.partyIntent);
                        break;
                    case 2:
                        startActivity(ProjectManager.perlinShowIntent);
                        break;
                    case 3:
                        startActivity(ProjectManager.iridescentLightsIntent);
                        break;
                }
            }
        })){
            BluetoothManager.startScan();
            initSuccess = true;
        }
    }

    void updateListView(){
        ArrayList<String> names = new ArrayList<>();
        for (BluetoothPeripheral peripheral : devices)
            names.add(peripheral.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (BluetoothManager.allPermissionsGranted(this)) {
                initSuccess = true;
                BluetoothManager.startScan();
            } else {
                Toast.makeText(this, "Необходимые разрешения не предоставлены", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}