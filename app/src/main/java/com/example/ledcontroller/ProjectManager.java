package com.example.ledcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;

import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.GattStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;

public class ProjectManager extends AppCompatActivity {
    private static final String TAG = "Project manager";
    private static final String version = "1.0";
    public static boolean wasConnected = false;
    public static boolean wasVersionError = false;
    public static boolean isGettingSettings = false;
    private static boolean isGettingAlarms;
    public static List<String> modes = Arrays.asList("Общие настройки", "Single color", "Party", "Perlin show"/*, "Iridescent lightsIntent"*/);
    public static int currMode;
    private static int currModeSettings;
    private static Context context;
    private static final BluetoothFunc onNotify = new BluetoothFunc() {
        @Override
        public void run(BluetoothPeripheral peripheral, byte[] value, GattStatus status) {
            if (status == GattStatus.SUCCESS){
                String data = new String(value);
                if (!Arrays.equals(value, new byte[]{13, 10}) && !data.equals(peripheral.getName()) &&
                        !Arrays.equals(value, new byte[]{0, 0})) {
                    Log.i(TAG, data + "\t(" + Arrays.toString(value) + ")");
                    if (isGettingSettings){
                        int index = data.indexOf('\t');
                        if (index == -1) {
                            if (data.equals("alarms_start")){
                                isGettingAlarms = true;
                                return;
                            }
                            isGettingAlarms = false;
                            isGettingSettings = false;
                            Log.i(TAG, "finish");
                            Intent intent = new Intent(context, MenuActivity.class);
                            context.startActivity(intent);
                            return;
                        }
                        String command = new String(Arrays.copyOfRange(value, 0, index));
                        byte[] values = Arrays.copyOfRange(value, index + 1, value.length);
                        switch (command) {
                            case "isOn":
                                MenuActivity.isOn = values[0] == '1';
                                Log.d(TAG, "is on = " + MenuActivity.isOn);
                            case "name":
                                MenuActivity.bluetoothName = new String(values);
                                Log.d(TAG, "bluetooth name = " + MenuActivity.bluetoothName);
                                break;
                            case "ssid":
                                MenuActivity.wifiSsid = new String(values);
                                Log.d(TAG, "wifi ssid = " + MenuActivity.wifiSsid);
                                break;
                            case "password":
                                MenuActivity.wifiPassword = new String(values);
                                Log.d(TAG, "wifi password = " + MenuActivity.wifiPassword);
                                break;
                            case "current_mode":
                                currMode = values[0];
                                Log.d(TAG, "currMode = " + currMode);
                                break;
                            case "mode":
                                currModeSettings = values[0];
                                Log.d(TAG, "currModeSettings = " + currModeSettings);
                                break;
                            case "current_profile.txt":
                                switch (currModeSettings) {
                                    case 0:
                                        SingleColorActivity.currProfile = new String(values);
                                        Log.d(TAG, "Single color current profile = " + SingleColorActivity.currProfile);
                                        break;
                                    case 1:
                                        PartyActivity.currProfile = new String(values);
                                        Log.d(TAG, "Party current profile = " + SingleColorActivity.currProfile);
                                        break;
                                    case 2:
                                        PerlinShowActivity.currProfile = new String(values);
                                        Log.d(TAG, "Perlin show current profile = " + SingleColorActivity.currProfile);
                                        break;
                                    case 3:
                                        IridescentLightsActivity.currProfile = new String(values);
                                        Log.d(TAG, "Iridescent lights current profile = " + SingleColorActivity.currProfile);
                                        break;
                                    default:
                                        Log.e(TAG, "Mode does not exist");
                                        break;
                                }
                                break;
                            case "time_zone":
                                MenuActivity.timeZone = values[0];
                                Log.d(TAG, "time zone = " + MenuActivity.timeZone);
                                break;
                            default:
                                byte[] buf = command.getBytes();
                                if ("profile_".equals(new String(Arrays.copyOfRange(buf, 0, 8)))) {
                                    int indexOfDot = command.indexOf('.');
                                    String profileName = new String(Arrays.copyOfRange(buf, 8, indexOfDot));
                                    switch (currModeSettings) {
                                        case 0:
                                            SingleColorActivity.setProfileSettings(profileName, values);
                                            Log.d(TAG, "Single color settings has changed");
                                            break;
                                        case 1:
                                            PartyActivity.setProfileSettings(profileName, values);
                                            Log.d(TAG, "Party settings has changed");
                                            break;
                                        case 2:
                                            PerlinShowActivity.setProfileSettings(profileName, values);
                                            Log.d(TAG, "Perlin show settings has changed");
                                            break;
                                        case 3:
                                            IridescentLightsActivity.setProfileSettings(profileName, values);
                                            Log.d(TAG, "Iridescent lights settings has changed");
                                            break;
                                    }
                                }
                                else if (isGettingAlarms){
                                    MenuActivity.addAlarm(new String(Arrays.copyOfRange(command.getBytes(), 0, command.length() - 4)), values);
                                    Log.d(TAG, "got new Alarm: \"" + command + "\"");
                                }
                                else
                                    Log.e(TAG, "Unknown command (" + command + ")");
                                break;
                        }
                        return;
                    }
                    if (!wasConnected) {
                        if (data.equals(version)) {
                            BluetoothManager.send(new byte[]{(byte) ('1')});
                            wasConnected = true;
                            isGettingSettings = true;
                        }
                        else {
                            BluetoothManager.send(new byte[]{(byte) ('0')});
                            wasVersionError = true;
                            MainActivity.isConnecting = false;
                            BluetoothManager.startScan();
                            Toast.makeText(context, "Несовместимые версии", Toast.LENGTH_SHORT).show();
                            BluetoothManager.disconnect();
                        }
                    }
                    if (data.equals("alarm_activated")){
                        Intent intent = new Intent(context, AlarmOffActivity.class);
                        context.startActivity(intent);
                    }
                }
            }
            else {
                Log.e(TAG, "Received data error: " + status);
            }
        }
    };
    public static void setMode(int i){
        if (i != 0) BluetoothManager.send(("m" + (i - 1)).getBytes());
        Intent intent;
        switch (i) {
            case 0:
                intent = new Intent(context, MenuActivity.class);
                break;
            case 1:
                intent = new Intent(context, SingleColorActivity.class);
                break;
            case 2:
                intent = new Intent(context, PartyActivity.class);
                break;
            case 3:
                intent = new Intent(context, PerlinShowActivity.class);
                break;
            case 4:
                intent = new Intent(context, IridescentLightsActivity.class);
                break;
            default:
                return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static boolean initBluetooth(Activity activity, Context context, BluetoothFunc onDeviceFound){
        ProjectManager.context = context;
        return BluetoothManager.init(activity, context, onDeviceFound, onNotify);
    }
    public static void showPopupMenu(Context context, View anchorView, List<String> items, AdapterView.OnItemClickListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu, null);
        ListView listView = popupView.findViewById(R.id.popup_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int backgroundColor = typedValue.data;

        popupView.setBackgroundColor(backgroundColor);
        popupWindow.showAsDropDown(anchorView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.onItemClick(adapterView, view, i, l);
                popupWindow.dismiss();
            }
        });
    }
    public static void showInputDialog(Context context, String title, BluetoothFunc func) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);
        EditText inputField = dialogView.findViewById(R.id.inputField);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputField.getText().toString().trim();
                if (!input.isEmpty()) {
                    dialog.dismiss();
                    func.run(input);
                }
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.show();
    }
    public static void hideKeyboard(Context context, View view){
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
