package com.example.ledcontroller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "menu activity";
    public static String bluetoothName;
    public static String wifiSsid;
    public static String wifiPassword;
    public static int timeZone;
    public static boolean isOn;
    private static HashMap<String, Alarm> alarms = new HashMap<>();
    private static ArrayList<String> alarmsNames = new ArrayList<>();

    private EditText nameView, wifiSsidView, wifiPasswordView, timeZoneView;
    private Switch isOnView;
    private Button rebootButton, alarmsButton;
    private ImageButton modesButton;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothManager.disconnect();
        ProjectManager.wasConnected = false;
        clearAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (alarmsNames.isEmpty())
            alarmsNames.add("Добавить");

        modesButton = findViewById(R.id.modes_button_menu_activity);
        nameView = findViewById(R.id.bluetooth_name_menu_activity);
        wifiSsidView = findViewById(R.id.wifi_ssid_menu_activity);
        wifiPasswordView = findViewById(R.id.wifi_password_menu_activity);
        timeZoneView = findViewById(R.id.time_zone_menu_activity);
        rebootButton = findViewById(R.id.reboot_button_menu_activity);
        alarmsButton = findViewById(R.id.alarms_button_menu_activity);
        isOnView = findViewById(R.id.is_on_view_menu_activity);

        updateAll();
        listenerInit();
    }
    private void listenerInit(){
        modesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(MenuActivity.this, modesButton, ProjectManager.modes, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        clearAll();
                        finish();
                        ProjectManager.setMode(i);
                    }
                });
            }
        });
        nameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    ProjectManager.hideKeyboard(MenuActivity.this, nameView);
                    bluetoothName = String.valueOf(nameView.getText());
                    BluetoothManager.send(("n" + bluetoothName).getBytes());
                    ProjectManager.hideKeyboard(MenuActivity.this, nameView);
                    return true;
                }
                return false;
            }
        });
        wifiSsidView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    wifiSsid = String.valueOf(wifiSsidView.getText());
                    BluetoothManager.send(("ws" + wifiSsid).getBytes());
                    ProjectManager.hideKeyboard(MenuActivity.this, wifiSsidView);
                    return true;
                }
                return false;
            }
        });
        wifiPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    wifiPassword = String.valueOf(wifiPasswordView.getText());
                    BluetoothManager.send(("wp" + wifiPassword).getBytes());
                    ProjectManager.hideKeyboard(MenuActivity.this, wifiPasswordView);
                    return true;
                }
                return false;
            }
        });
        timeZoneView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(timeZoneView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        timeZoneView.setText(String.valueOf(buf));
                    }
                    timeZone = buf;
                    BluetoothManager.send(("tz" + buf).getBytes());
                    ProjectManager.hideKeyboard(MenuActivity.this, timeZoneView);
                    return true;
                }
                return false;
            }
        });
        rebootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothManager.send(("r").getBytes());
                ProjectManager.wasConnected = false;
                clearAll();
                finish();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothManager.disconnect();
                    }
                }, 100);
            }
        });
        alarmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(MenuActivity.this, alarmsButton, alarmsNames, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (alarmsNames.get(i).equals("Добавить")){
                            ProjectManager.showInputDialog(MenuActivity.this,
                                    "Введите имя нового будильника",  new BluetoothFunc() {
                                @Override
                                public void run(String s) {
                                    if (alarmsNames.contains(s)){
                                        Toast.makeText(MenuActivity.this,
                                                "Будильник с таким именем уже существует",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(MenuActivity.this, "Создан новый будильник (\""
                                            + s + "\")", Toast.LENGTH_SHORT).show();
                                    addAlarm(s);
                                    showAlarmEdit(new Alarm(), s);
                                    // todo show alarm
//                                    Alarm alarm = new Alarm();
//                                    BluetoothManager.send(("tn" + s + "\t" + alarm.getRaw()).getBytes());
                                }
                            });
                            return;
                        }
                        BluetoothManager.send(("td" + alarmsNames.get(i)).getBytes());
                        showAlarmEdit(alarms.get(alarmsNames.get(i)), alarmsNames.get(i));
                    }
                });
            }
        });
        isOnView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOn = b;
                BluetoothManager.send(("b").getBytes());
            }
        });
    }
    private void updateAll(){
        nameView.setText(String.valueOf(bluetoothName));
        wifiSsidView.setText(String.valueOf(wifiSsid));
        wifiPasswordView.setText(String.valueOf(wifiPassword));
        timeZoneView.setText(String.valueOf(timeZone));
        isOnView.setChecked(isOn);
    }
    private void clearAll(){
        alarms.clear();
        alarmsNames.clear();
    }
    private static void addAlarm(String name){
        alarmsNames.remove("Добавить");
        alarmsNames.add(name);
        alarmsNames.add("Добавить");
    }
    public static void addAlarm(String name, byte[] data){
        addAlarm(name);
        alarms.put(name, new Alarm(data));
    }
    private void showAlarmEdit(Alarm alarm, String alarmName){
        View dialogView = LayoutInflater.from(MenuActivity.this).inflate(R.layout.alarm_input, null);
        EditText hour = dialogView.findViewById(R.id.hour_edit_text_alarm_edit);
        EditText minute = dialogView.findViewById(R.id.minute_edit_text_alarm_edit);
        Button mode = dialogView.findViewById(R.id.mode_button_alarm_edit);
        Button profile = dialogView.findViewById(R.id.profile_buton_alarm_edit);
        EditText razingTime = dialogView.findViewById(R.id.razing_time_alarm_edit);
        Switch isOn = dialogView.findViewById(R.id.is_on_switch_alarm_edit);
        Switch monday = dialogView.findViewById(R.id.monday_switch_alarm_edit);
        Switch tuesday = dialogView.findViewById(R.id.tuesday_switch_alarm_edit);
        Switch wednesday = dialogView.findViewById(R.id.wednesday_switch_alarm_edit);
        Switch thursday = dialogView.findViewById(R.id.thursday_switch_alarm_edit);
        Switch friday = dialogView.findViewById(R.id.friday_switch_alarm_edit);
        Switch saturday = dialogView.findViewById(R.id.saturday_switch_alarm_edit);
        Switch sunday = dialogView.findViewById(R.id.sunday_switch_alarm_edit);
        Button submit = dialogView.findViewById(R.id.done_button_alarm_edit);
        Button delete = dialogView.findViewById(R.id.delete_button_alarm_edit);

        hour.setText(String.valueOf(alarm.hour));
        String buf = String.valueOf(alarm.minute);
        if (alarm.minute < 10) buf = "0" + alarm.minute;
        minute.setText(buf);
        mode.setText(String.valueOf(ProjectManager.modes.get(alarm.mode + 1)));
        profile.setText(String.valueOf(alarm.profileName));
        razingTime.setText(String.valueOf(alarm.razingTime));
        isOn.setChecked(alarm.isOn);
        monday.setChecked(alarm.days[1]);
        tuesday.setChecked(alarm.days[2]);
        wednesday.setChecked(alarm.days[3]);
        thursday.setChecked(alarm.days[4]);
        friday.setChecked(alarm.days[5]);
        saturday.setChecked(alarm.days[6]);
        sunday.setChecked(alarm.days[0]);

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Настройка будильника \"" + alarmName + "\"")
                .setView(dialogView)
                .setCancelable(false)
                .create();

        hour.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(hour.getText()));
                    if (buf > 23) {
                        buf = 23;
                        hour.setText(String.valueOf(buf));
                    }
                    alarm.hour = buf;
                    ProjectManager.hideKeyboard(MenuActivity.this, hour);
                    return true;
                }
                return false;
            }
        });
        minute.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(minute.getText()));
                    if (buf > 59) {
                        buf = 59;
                        minute.setText(String.valueOf(buf));
                    }
                    else if (buf < 10)
                        minute.setText(String.valueOf("0" + buf));
                    alarm.minute = buf;
                    ProjectManager.hideKeyboard(MenuActivity.this, minute);
                    return true;
                }
                return false;
            }
        });
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> buf = ProjectManager.modes.subList(1, ProjectManager.modes.size());
                ProjectManager.showPopupMenu(MenuActivity.this, mode, buf, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mode.setText(String.valueOf(ProjectManager.modes.get(i + 1)));
                        alarm.mode = i;
                        profile.setText(String.valueOf("default"));
                        alarm.profileName = "default";
                    }
                });
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> buf = new ArrayList<>();
                switch (alarm.mode){
                    case 0:
                        buf = SingleColorActivity.profilesNames.subList(0, SingleColorActivity.profilesNames.size() - 1);
                        break;
                    case 1:
                        buf = PartyActivity.profilesNames.subList(0, PartyActivity.profilesNames.size() - 1);
                        break;
                    case 2:
                        buf = PerlinShowActivity.profilesNames.subList(0, PerlinShowActivity.profilesNames.size() - 1);
                        break;
                }
                List<String> finalBuf = buf;
                ProjectManager.showPopupMenu(MenuActivity.this, profile, finalBuf, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        profile.setText(finalBuf.get(i));
                        alarm.profileName = finalBuf.get(i);
                    }
                });
            }
        });
        razingTime.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(razingTime.getText()));
                    if (buf > 240) {
                        buf = 240;
                        razingTime.setText(String.valueOf(buf));
                    }
                    alarm.razingTime = buf;
                    ProjectManager.hideKeyboard(MenuActivity.this, razingTime);
                    return true;
                }
                return false;
            }
        });
        monday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[1] = b;
            }
        });
        tuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[2] = b;
            }
        });
        wednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[3] = b;
            }
        });
        thursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[4] = b;
            }
        });
        friday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[5] = b;
            }
        });
        saturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[6] = b;
            }
        });
        sunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.days[0] = b;
            }
        });
        isOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.isOn = b;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarms.put(alarmName, alarm);
                String buf1 = "tn" + alarmName + "\t";
                byte[] buf2 = alarm.getRaw();
                byte[] buf3 = new byte[buf2.length + buf1.length()];
                System.arraycopy(buf1.getBytes(), 0, buf3, 0, buf1.length());
                System.arraycopy(buf2, 0, buf3, buf1.length(), buf2.length);
                BluetoothManager.send(buf3);
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarms.remove(alarmName);
                alarmsNames.remove(alarmName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}