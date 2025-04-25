package com.example.ledcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BleScanner";

    public static boolean isConnecting = false;
    private boolean initSuccess = false;
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    BluetoothFunc onDeviceFound = new BluetoothFunc(){
        @Override
        public void run(BluetoothPeripheral peripheral){
            String name = peripheral.getName();
            if (!name.equals("")) {
//                Log.i(TAG, "onConnectingPeripheral: " + peripheral.getName());
                if (!devices.contains(peripheral)) {
                    devices.add(peripheral);
                    updateListView();
                }
            }
        }
    };

    ImageButton refreshButton;
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
        isConnecting = false;
        updateListView();
        SingleColorActivity.clearAll();
        PartyActivity.clearAll();
        PerlinShowActivity.clearAll();
        if (initSuccess)
            BluetoothManager.startScan();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshButton = findViewById(R.id.refresh_button_main_activity);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devices.clear();
                updateListView();
                if (initSuccess)
                    BluetoothManager.startScan();
            }
        });
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isConnecting) return;
                isConnecting = true;
                Log.i(TAG, "Scan stopped");
                BluetoothManager.stopScan();
                BluetoothManager.connect(devices.get(i));
                devices.clear();
                Toast.makeText(MainActivity.this, "Подключение...", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ProjectManager.wasConnected || ProjectManager.wasVersionError){
                            ProjectManager.wasVersionError = false;
                            return;
                        }
                        BluetoothManager.disconnect();
                        MainActivity.isConnecting = false;
                        updateListView();
                        BluetoothManager.startScan();
                        Toast.makeText(MainActivity.this, "Неподходящее устройстсво", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });
        devices = new ArrayList<>();

        if (ProjectManager.initBluetooth(this, this, onDeviceFound)){
            BluetoothManager.startScan();
            initSuccess = true;
        }
    }

    private void updateListView(){
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