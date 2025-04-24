package com.example.ledcontroller;

import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.GattStatus;

public interface BluetoothFunc {
    default void run(){}
    default void run(String s){}
    default void run(BluetoothPeripheral peripheral){}
    default void run(BluetoothPeripheral peripheral, byte[] value, GattStatus status){}
}
