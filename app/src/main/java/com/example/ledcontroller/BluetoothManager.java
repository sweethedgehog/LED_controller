package com.example.ledcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.GattStatus;
import com.welie.blessed.WriteType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BluetoothManager {

    private static final String TAG = "Bluetooth manager";
    public static BluetoothCentralManager centralManager;
    private static BluetoothPeripheralCallback callback;
    private static BluetoothPeripheral bluetoothPeripheral;
    private static BluetoothGattCharacteristic sendCharacteristic;
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static boolean init(Activity activity, Context context, BluetoothFunc onDeviceFound,
                               BluetoothFunc onNotify) {
        centralManager = new BluetoothCentralManager(context, new BluetoothCentralManagerCallback() {
            @Override
            public void onDiscoveredPeripheral(BluetoothPeripheral peripheral, ScanResult scanResult) {
                onDeviceFound.run(peripheral);
            }
        }, new Handler(Looper.getMainLooper()));

        callback = new BluetoothPeripheralCallback() {
            @Override
            public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
                super.onServicesDiscovered(peripheral);
                List<BluetoothGattService> services = peripheral.getServices();
                peripheral.requestMtu(517);
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        int properties = characteristic.getProperties();
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            peripheral.setNotify(characteristic, true);
                        }
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                            peripheral.readCharacteristic(characteristic);
                        }
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                            sendCharacteristic = characteristic;
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral,
                                               @NotNull byte[] value,
                                               @NotNull BluetoothGattCharacteristic characteristic,
                                               @NotNull GattStatus status) {
                super.onCharacteristicUpdate(peripheral, value, characteristic, status);
                onNotify.run(peripheral, value, status);
            }
        };
        if (!allPermissionsGranted(context)){
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static void startScan() {
        centralManager.scanForPeripherals();
    }

    public static void stopScan() {
        centralManager.stopScan();
    }

    public static void connect(BluetoothPeripheral peripheral) {
        bluetoothPeripheral = peripheral;
        centralManager.connectPeripheral(peripheral, callback);
    }

    public static void send(byte[] value) {
        bluetoothPeripheral.writeCharacteristic(sendCharacteristic, value, WriteType.WITH_RESPONSE);
    }

    public static int getMtu() {
        return bluetoothPeripheral.getCurrentMtu();
    }

    public static void disconnect() {
        if (bluetoothPeripheral != null/* && bluetoothPeripheral.isConnected()*/) {
            centralManager.cancelConnection(bluetoothPeripheral);
            bluetoothPeripheral = null;
            sendCharacteristic = null;
        }
    }
    public static boolean allPermissionsGranted(Context context) {
        for (String permission : REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }
}
