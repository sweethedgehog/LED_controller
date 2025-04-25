package com.example.ledcontroller;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SingleColorActivity extends AppCompatActivity {
    private static final String TAG = "SingleColor";
    private static HashMap<String, int[]> profiles = new HashMap<>();
    public static ArrayList<String> profilesNames = new ArrayList<>();
    public static String currProfile;
    private static int brightness, speed;
    private static int hue, saturation, value, gap, tailSize, rainbowCount;
    private static boolean twoSides, direction, isRunningLight, isRainbow;
    private static boolean isChangingProfile = false;

    private ImageButton modesButton;
    private ImageButton profilesButton;
    private ImageButton deleteButton;
    private TextView currProfileView;
    private EditText brightnessView;
    private SeekBar brightnessSeekBar;
    private EditText speedView;
    private SeekBar speedSeekBar;
    private EditText hueView;
    private SeekBar hueSeekBar;
    private EditText saturationView;
    private SeekBar saturationSeekBar;
    private EditText valueView;
    private SeekBar valueSeekBar;
    private EditText gapView;
    private EditText tailSizeView;
    private EditText countOfRainbowsView;
    private Switch twoSidesView;
    private Switch directionView;
    private Switch isRunningLightView;
    private Switch isRainbowView;

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
        setContentView(R.layout.activity_single_color);

        currProfileView = findViewById(R.id.profile_text_view_single_color);
        modesButton = findViewById(R.id.modes_button_single_color);
        profilesButton = findViewById(R.id.profiles_button_single_color);
        deleteButton = findViewById(R.id.delete_profile_button_single_color);
        setProfile(currProfile);

        brightnessView = findViewById(R.id.brightness_seek_bar_view_single_color);
        brightnessSeekBar = findViewById(R.id.brightness_seek_bar_single_color);
        speedView = findViewById(R.id.speed_seek_bar_view_single_color);
        speedSeekBar = findViewById(R.id.speed_seek_bar_single_color);
        hueView = findViewById(R.id.hue_seek_bar_view_single_color);
        hueSeekBar = findViewById(R.id.hue_seek_bar_single_color);
        saturationView = findViewById(R.id.saturation_seek_bar_view_single_color);
        saturationSeekBar = findViewById(R.id.saturation_seek_bar_single_color);
        valueView = findViewById(R.id.value_seek_bar_view_single_color);
        valueSeekBar = findViewById(R.id.value_seek_bar_single_color);
        gapView = findViewById(R.id.gap_seek_bar_view_single_color);
        tailSizeView = findViewById(R.id.tail_size_seek_bar_view_single_color);
        countOfRainbowsView = findViewById(R.id.count_of_rainbows_seek_bar_view_single_color);
        twoSidesView = findViewById(R.id.two_sides_single_color_toggle);
        directionView = findViewById(R.id.direction_single_color_toggle);
        isRunningLightView = findViewById(R.id.is_running_light_single_color_toggle);
        isRainbowView = findViewById(R.id.is_rainbow_single_color_toggle);
        updateAll();
        listenerInit();
    }
    private void listenerInit(){
        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(SingleColorActivity.this, profilesButton, profilesNames, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (profilesNames.get(i).equals("Добавить")){
                            ProjectManager.showInputDialog(SingleColorActivity.this,
                                    "Введите имя нового профиля", new BluetoothFunc() {
                                @Override
                                public void run(String s) {
                                    if (profilesNames.contains(s)){
                                        Toast.makeText(SingleColorActivity.this,
                                                "Такой профиль уже существует",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    BluetoothManager.send(("pn" + s).getBytes());
                                    Toast.makeText(SingleColorActivity.this, "Создан новый профиль \""
                                            + s + "\"", Toast.LENGTH_SHORT).show();
                                    int[] buf = Arrays.copyOf(profiles.get(currProfile), profiles.get(currProfile).length);
                                    addPName(s);
                                    profiles.put(s, buf);
                                    saveProfileSettings(currProfile);
                                    currProfile = s;
                                    setProfile(currProfile);
                                    updateAll();
                                }
                            });
                            return;
                        }
                        saveProfileSettings(currProfile);
                        currProfile = profilesNames.get(i);
                        Log.e(TAG, currProfile);
                        setProfile(currProfile);
                        updateAll();
                        BluetoothManager.send(("ps" + currProfile).getBytes());
                    }
                });
            }
        });
        modesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(SingleColorActivity.this, modesButton, ProjectManager.modes, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        clearAll();
                        saveProfileSettings(currProfile);
                        finish();
                        ProjectManager.setMode(i);
                    }
                });
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothManager.send(("pd" + currProfile).getBytes());
                profilesNames.remove(currProfile);
                profiles.remove(currProfile);
                currProfile = "default";
                setProfile(currProfile);
                updateAll();
            }
        });

        directionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                direction = b;
                BluetoothManager.send(("sj" + (direction ? 1 : 0)).getBytes());
            }
        });
        isRunningLightView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                isRunningLight = b;
                BluetoothManager.send(("sk" + (isRunningLight ? 1 : 0)).getBytes());
            }
        });
        isRainbowView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                isRainbow = b;
                BluetoothManager.send(("sl" + (isRainbow ? 1 : 0)).getBytes());
            }
        });
        twoSidesView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                twoSides = b;
                BluetoothManager.send(("si" + (twoSides ? 1 : 0)).getBytes());
            }
        });
        countOfRainbowsView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(countOfRainbowsView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        countOfRainbowsView.setText(String.valueOf(buf));
                    }
                    rainbowCount = buf;
                    BluetoothManager.send(("sh" + buf).getBytes());
                    ProjectManager.hideKeyboard(SingleColorActivity.this, countOfRainbowsView);
                    return true;
                }
                return false;
            }
        });
        tailSizeView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(tailSizeView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        tailSizeView.setText(String.valueOf(buf));
                    }
                    tailSize = buf;
                    BluetoothManager.send(("sg" + buf).getBytes());
                    ProjectManager.hideKeyboard(SingleColorActivity.this, tailSizeView);
                    return true;
                }
                return false;
            }
        });
        gapView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(gapView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        gapView.setText(String.valueOf(buf));
                    }
                    gap = buf;
                    BluetoothManager.send(("sf" + buf).getBytes());
                    ProjectManager.hideKeyboard(SingleColorActivity.this, gapView);
                    return true;
                }
                return false;
            }
        });
        valueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valueView.setText(String.valueOf(valueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                value = valueSeekBar.getProgress();
                BluetoothManager.send(("se" + value).getBytes());
            }
        });
        valueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(valueView.getText()));
                    if (buf > 200) {
                        buf = 200;
                        valueView.setText(String.valueOf(buf));
                    }
                    value = buf;
                    BluetoothManager.send(("se" + buf).getBytes());
                    valueSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(SingleColorActivity.this, valueView);
                    return true;
                }
                return false;
            }
        });
        saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saturationView.setText(String.valueOf(saturationSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                saturation = saturationSeekBar.getProgress();
                BluetoothManager.send(("sd" + saturation).getBytes());
            }
        });
        saturationView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(saturationView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        saturationView.setText(String.valueOf(buf));
                    }
                    saturation = buf;
                    BluetoothManager.send(("sd" + buf).getBytes());
                    saturationSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(SingleColorActivity.this, saturationView);
                    return true;
                }
                return false;
            }
        });
        hueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hueView.setText(String.valueOf(hueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                hue = Integer.parseInt(String.valueOf(hueView.getText()));
                BluetoothManager.send(("sc" + hue).getBytes());
            }
        });
        hueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(hueView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        hueView.setText(String.valueOf(buf));
                    }
                    hue = buf;
                    BluetoothManager.send(("sc" + buf).getBytes());
                    hueSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(SingleColorActivity.this, hueView);
                    return true;
                }
                return false;
            }
        });
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speedView.setText(String.valueOf(speedSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                speed = speedSeekBar.getProgress();
                BluetoothManager.send(("sb" + speed).getBytes());
            }
        });
        speedView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(speedView.getText()));
                    if (buf > 200) {
                        buf = 200;
                        speedView.setText(String.valueOf(buf));
                    }
                    speed = buf;
                    BluetoothManager.send(("sb" + buf).getBytes());
                    speedSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(SingleColorActivity.this, speedView);
                    return true;
                }
                return false;
            }
        });
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightnessView.setText(String.valueOf(brightnessSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                brightness = brightnessSeekBar.getProgress();
                BluetoothManager.send(("sa" + brightness).getBytes());
            }
        });
        brightnessView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(brightnessView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        brightnessView.setText(String.valueOf(buf));
                    }
                    brightness = buf;
                    BluetoothManager.send(("sa" + buf).getBytes());
                    brightnessSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(SingleColorActivity.this, brightnessView);
                    return true;
                }
                return false;
            }
        });
    }
    private static void addPName(String name){
        profilesNames.remove("Добавить");
        profilesNames.add(name);
        profilesNames.add("Добавить");
    }
    public static void setProfileSettings(String profileName, byte[] settings){
        addPName(profileName);
        Log.e(TAG, profileName);
        int[] buf = new int[settings.length];
        for (int i = 0; i < settings.length; ++i){
            buf[i] = (settings[i] + 256) % 256;
        }
        profiles.put(profileName, buf);
    }
    private void setProfile(String profileName){
        int[] buf = profiles.get(profileName);
        brightness = buf[0];
        speed = buf[1];
        hue = buf[2];
        saturation = buf[3];
        value = buf[4];
        gap = buf[5];
        tailSize = buf[6];
        rainbowCount = buf[7];
        twoSides = buf[8] == 1;
        direction = buf[9] == 1;
        isRunningLight = buf[10] == 1;
        isRainbow = buf[11] == 1;
        currProfileView.setText("Профиль: " + profileName);
    }
    private static void saveProfileSettings(String profileName){
        int[] buf = new int[profiles.get(profileName).length];
        buf[0] = brightness;
        buf[1] = speed;
        buf[2] = hue;
        buf[3] = saturation;
        buf[4] = value;
        buf[5] = gap;
        buf[6] = tailSize;
        buf[7] = rainbowCount;
        buf[8] = twoSides ? 1 : 0;
        buf[9] = direction ? 1 : 0;
        buf[10] = isRunningLight ? 1 : 0;
        buf[11] = isRainbow ? 1 : 0;
        profiles.put(profileName, buf);
    }
    private void updateAll(){
        isChangingProfile = true;
        brightnessView.setText(String.valueOf(brightness));
        brightnessSeekBar.setProgress(brightness);
        speedView.setText(String.valueOf(speed));
        speedSeekBar.setProgress(speed);
        hueView.setText(String.valueOf(hue));
        hueSeekBar.setProgress(hue);
        saturationView.setText(String.valueOf(saturation));
        saturationSeekBar.setProgress(saturation);
        valueView.setText(String.valueOf(value));
        valueSeekBar.setProgress(value);
        gapView.setText(String.valueOf(gap));
        tailSizeView.setText(String.valueOf(tailSize));
        countOfRainbowsView.setText(String.valueOf(rainbowCount));
        twoSidesView.setChecked(twoSides);
        directionView.setChecked(direction);
        isRunningLightView.setChecked(isRunningLight);
        isRainbowView.setChecked(isRainbow);
        if (currProfile.equals("default"))
            deleteButton.setVisibility(View.GONE);
        else
            deleteButton.setVisibility(View.VISIBLE);
        isChangingProfile = false;
    }
    public static void clearAll(){
        profiles.clear();
        profilesNames.clear();
    }
}