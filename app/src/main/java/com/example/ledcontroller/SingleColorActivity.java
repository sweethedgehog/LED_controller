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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleColorActivity extends AppCompatActivity {
    private static final String TAG = "SingleColor";
    private static HashMap<String, int[]> profiles = new HashMap<>();
    private static ArrayList<String> profilesNames = new ArrayList<>();
    public static String currProfile;
    private static int brightness, speed;
    private static int hue, saturation, value, gap, tailSize, rainbowCount;
    private static boolean twoSides, direction, isRunningLight, isRainbow;

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
    private ImageButton modesButton;
    private ImageButton profilesButton;

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_color);
        setProfile(currProfile);
        modesButton = findViewById(R.id.modes_button);
        modesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(SingleColorActivity.this, modesButton, ProjectManager.modes, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(SingleColorActivity.this, ProjectManager.modes.get(i), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        profilesButton = findViewById(R.id.profiles_button);
        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(SingleColorActivity.this, profilesButton, profilesNames, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        currProfile = profilesNames.get(i);
                        Log.e(TAG, currProfile);
                        setProfile(currProfile);
                        updateAll();
                        BluetoothManager.send(("ps" + currProfile).getBytes());
                    }
                });
            }
        });

        brightnessView = findViewById(R.id.brightness_seek_bar_view_single_color);
        brightnessView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(brightnessView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        brightnessView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sa" + buf).getBytes());
                    brightnessSeekBar.setProgress(buf);
                    return true;
                }
                return false;
            }
        });
        brightnessSeekBar = findViewById(R.id.brightness_seek_bar_single_color);
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
                brightness = brightnessSeekBar.getProgress();
                BluetoothManager.send(("sa" + brightness).getBytes());
            }
        });
        speedView = findViewById(R.id.speed_seek_bar_view_single_color);
        speedView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(speedView.getText()));
                    if (buf > 200) {
                        buf = 200;
                        speedView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sb" + buf).getBytes());
                    speedSeekBar.setProgress(buf);
                    return true;
                }
                return false;
            }
        });
        speedSeekBar = findViewById(R.id.speed_seek_bar_single_color);
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
                speed = speedSeekBar.getProgress();
                BluetoothManager.send(("sb" + speed).getBytes());
            }
        });
        hueView = findViewById(R.id.hue_seek_bar_view_single_color);
        hueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(hueView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        hueView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sc" + buf).getBytes());
                    hueSeekBar.setProgress(buf);
                    return true;
                }
                return false;
            }
        });
        hueSeekBar = findViewById(R.id.hue_seek_bar_single_color);
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
                hue = Integer.parseInt(String.valueOf(hueView.getText()));
                BluetoothManager.send(("sc" + hue).getBytes());
            }
        });
        saturationView = findViewById(R.id.saturation_seek_bar_view_single_color);
        saturationView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(saturationView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        saturationView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sd" + buf).getBytes());
                    saturationSeekBar.setProgress(buf);
                    return true;
                }
                return false;
            }
        });
        saturationSeekBar = findViewById(R.id.saturation_seek_bar_single_color);
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
                saturation = saturationSeekBar.getProgress();
                BluetoothManager.send(("sd" + saturation).getBytes());
            }
        });
        valueView = findViewById(R.id.value_seek_bar_view_single_color);
        valueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(valueView.getText()));
                    if (buf > 200) {
                        buf = 200;
                        valueView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("se" + buf).getBytes());
                    valueSeekBar.setProgress(buf);
                    return true;
                }
                return false;
            }
        });
        valueSeekBar = findViewById(R.id.value_seek_bar_single_color);
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
                value = valueSeekBar.getProgress();
                BluetoothManager.send(("se" + value).getBytes());
            }
        });
        gapView = findViewById(R.id.gap_seek_bar_view_single_color);
        gapView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(gapView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        gapView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sf" + buf).getBytes());
                    return true;
                }
                return false;
            }
        });
        tailSizeView = findViewById(R.id.tail_size_seek_bar_view_single_color);
        tailSizeView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(tailSizeView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        tailSizeView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sg" + buf).getBytes());
                    return true;
                }
                return false;
            }
        });
        countOfRainbowsView = findViewById(R.id.count_of_rainbows_seek_bar_view_single_color);
        countOfRainbowsView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    int buf = Integer.parseInt(String.valueOf(countOfRainbowsView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        countOfRainbowsView.setText(String.valueOf(buf));
                    }
                    BluetoothManager.send(("sh" + buf).getBytes());
                    return true;
                }
                return false;
            }
        });
        twoSidesView = findViewById(R.id.two_sides_single_color_toggle);
        twoSidesView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                twoSides = b;
                BluetoothManager.send(("si" + (twoSides ? 1 : 0)).getBytes());
            }
        });
        directionView = findViewById(R.id.direction_single_color_toggle);
        directionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                direction = b;
                BluetoothManager.send(("sj" + (direction ? 1 : 0)).getBytes());
            }
        });
        isRunningLightView = findViewById(R.id.is_running_light_single_color_toggle);
        isRunningLightView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRunningLight = b;
                BluetoothManager.send(("sk" + (isRunningLight ? 1 : 0)).getBytes());
            }
        });
        isRainbowView = findViewById(R.id.is_rainbow_single_color_toggle);
        isRainbowView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRainbow = b;
                BluetoothManager.send(("sl" + (isRainbow ? 1 : 0)).getBytes());
            }
        });
        updateAll();
    }
    public static void setProfileSettings(String profileName, byte[] settings){
        profilesNames.add(profileName);
        int[] buf = new int[settings.length];
        for (int i = 0; i < settings.length; ++i){
            buf[i] = (settings[i] + 256) % 256;
        }
        profiles.put(profileName, buf);
    }
    private static void setProfile(String profileName){
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
    }
    private void updateAll(){
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
    }
}