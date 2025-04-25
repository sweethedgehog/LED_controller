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

public class PerlinShowActivity extends AppCompatActivity {
    private static final String TAG = "PerlinShow";
    private static HashMap<String, int[]> profiles = new HashMap<>();
    public static ArrayList<String> profilesNames = new ArrayList<>();
    public static String currProfile = "";
    private static int brightness, speed;
    private static int gridWidth, yScale, hueScale, hueOffset, saturation, gap, tailSize;
    private static boolean isCircle, isRunningLight, direction;
    private static boolean isChangingProfile = false;

    private ImageButton modesButton;
    private ImageButton profilesButton;
    private ImageButton deleteButton;
    private TextView currProfileView;
    private EditText brightnessView;
    private SeekBar brightnessSeekBar;
    private EditText speedView;
    private SeekBar speedSeekBar;

    private EditText gridWidthView;
    private Switch isCircleView;
    private EditText yScaleView;
    private EditText hueScaleView;
    private EditText hueOffsetView;
    private SeekBar hueOffsetSeekBar;
    private EditText saturationView;
    private SeekBar saturationSeekBar;
    private Switch isRunningLightView;
    private EditText gapView;
    private EditText tailSizeView;
    private Switch directionView;

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
        setContentView(R.layout.activity_perlin_show);

        currProfileView = findViewById(R.id.profile_text_view_perlin_show);
        modesButton = findViewById(R.id.modes_button_perlin_show);
        profilesButton = findViewById(R.id.profiles_button_perlin_show);
        deleteButton = findViewById(R.id.delete_profile_button_perlin_show);
        setProfile(currProfile);

        brightnessView = findViewById(R.id.brightness_seek_bar_view_perlin_show);
        brightnessSeekBar = findViewById(R.id.brightness_seek_bar_perlin_show);
        speedView = findViewById(R.id.speed_seek_bar_view_perlin_show);
        speedSeekBar = findViewById(R.id.speed_seek_bar_perlin_show);

        gridWidthView = findViewById(R.id.grid_width_view_perlin_show);
        isCircleView = findViewById(R.id.is_circle_perlin_show_toggle);
        yScaleView = findViewById(R.id.y_scale_view_perlin_show);
        hueScaleView = findViewById(R.id.hue_scale_view_perlin_show);
        hueOffsetView = findViewById(R.id.hue_offset_seek_bar_view_perlin_show);
        hueOffsetSeekBar = findViewById(R.id.hue_offset_seek_bar_perlin_show);
        saturationView = findViewById(R.id.saturation_seek_bar_view_perlin_show);
        saturationSeekBar = findViewById(R.id.saturation_seek_bar_perlin_show);
        isRunningLightView = findViewById(R.id.is_running_light_perlin_show_toggle);
        gapView = findViewById(R.id.gap_view_perlin_show);
        tailSizeView = findViewById(R.id.tail_size_view_perlin_show);
        directionView = findViewById(R.id.direction_perlin_show_toggle);

        updateAll();
        listenerInit();
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
    private static void addPName(String name){
        profilesNames.remove("Добавить");
        profilesNames.add(name);
        profilesNames.add("Добавить");
    }
    public static void clearAll(){
        profiles.clear();
        profilesNames.clear();
    }
    private void setProfile(String profileName){
        int[] buf = profiles.get(profileName);
        brightness = buf[0];
        speed = buf[1];
        gridWidth = buf[2];
        isCircle = buf[3] == 1;
        yScale = buf[4];
        hueScale = buf[5];
        hueOffset = buf[6];
        saturation = buf[7];
        isRunningLight = buf[8] == 1;
        gap = buf[9];
        tailSize = buf[10];
        direction = buf[11] == 1;
        currProfileView.setText("Профиль: " + profileName);
    }
    private void updateAll(){
        isChangingProfile = true;
        brightnessView.setText(String.valueOf(brightness));
        brightnessSeekBar.setProgress(brightness);
        speedView.setText(String.valueOf(speed));
        speedSeekBar.setProgress(speed);

        gridWidthView.setText(String.valueOf(gridWidth));
        isCircleView.setChecked(isCircle);
        yScaleView.setText(String.valueOf(yScale));
        hueScaleView.setText(String.valueOf(hueScale));
        hueOffsetView.setText(String.valueOf(hueOffset));
        hueOffsetSeekBar.setProgress(hueOffset);
        saturationView.setText(String.valueOf(saturation));
        saturationSeekBar.setProgress(saturation);
        isRunningLightView.setChecked(isRunningLight);
        gapView.setText(String.valueOf(gap));
        tailSizeView.setText(String.valueOf(tailSize));
        directionView.setChecked(direction);

        if (currProfile.equals("default"))
            deleteButton.setVisibility(View.GONE);
        else
            deleteButton.setVisibility(View.VISIBLE);
        isChangingProfile = false;
    }
    private void listenerInit(){
        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectManager.showPopupMenu(PerlinShowActivity.this, profilesButton, profilesNames, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (profilesNames.get(i).equals("Добавить")){
                            ProjectManager.showInputDialog(PerlinShowActivity.this,
                                    "Введите имя нового профиля", new BluetoothFunc() {
                                @Override
                                public void run(String s) {
                                    if (profilesNames.contains(s)){
                                        Toast.makeText(PerlinShowActivity.this,
                                                "Такой профиль уже существует",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    BluetoothManager.send(("pn" + s).getBytes());
                                    Toast.makeText(PerlinShowActivity.this, "Создан новый профиль \""
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
                ProjectManager.showPopupMenu(PerlinShowActivity.this, modesButton, ProjectManager.modes, new AdapterView.OnItemClickListener() {
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
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, brightnessView);
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
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, speedView);
                    return true;
                }
                return false;
            }
        });

        gridWidthView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(gridWidthView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        gridWidthView.setText(String.valueOf(buf));
                    }
                    gridWidth = buf;
                    BluetoothManager.send(("sc" + buf).getBytes());
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, gridWidthView);
                    return true;
                }
                return false;
            }
        });
        isCircleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                isCircle = b;
                BluetoothManager.send(("sd" + (isCircle ? 1 : 0)).getBytes());
            }
        });
        yScaleView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(yScaleView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        yScaleView.setText(String.valueOf(buf));
                    }
                    yScale = buf;
                    BluetoothManager.send(("se" + buf).getBytes());
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, yScaleView);
                    return true;
                }
                return false;
            }
        });
        hueScaleView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(hueScaleView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        hueScaleView.setText(String.valueOf(buf));
                    }
                    hueScale = buf;
                    BluetoothManager.send(("sf" + buf).getBytes());
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, hueScaleView);
                    return true;
                }
                return false;
            }
        });
        hueOffsetView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(hueOffsetView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        hueOffsetView.setText(String.valueOf(buf));
                    }
                    hueOffset = buf;
                    BluetoothManager.send(("sg" + buf).getBytes());
                    hueOffsetSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, hueOffsetView);
                    return true;
                }
                return false;
            }
        });
        hueOffsetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hueOffsetView.setText(String.valueOf(hueOffsetSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                hueOffset = hueOffsetSeekBar.getProgress();
                BluetoothManager.send(("sg" + hueOffset).getBytes());
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
                    BluetoothManager.send(("sh" + buf).getBytes());
                    saturationSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, saturationView);
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
                BluetoothManager.send(("sh" + saturation).getBytes());
            }
        });
        isRunningLightView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                isRunningLight = b;
                BluetoothManager.send(("si" + (isRunningLight ? 1 : 0)).getBytes());
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
                    BluetoothManager.send(("sj" + buf).getBytes());
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, gapView);
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
                    BluetoothManager.send(("sk" + buf).getBytes());
                    ProjectManager.hideKeyboard(PerlinShowActivity.this, tailSizeView);
                    return true;
                }
                return false;
            }
        });
        directionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                direction = b;
                BluetoothManager.send(("sl" + (direction ? 1 : 0)).getBytes());
            }
        });
    }
    private void saveProfileSettings(String profileName){
        int[] buf = new int[profiles.get(profileName).length];
        buf[0] = brightness;
        buf[1] = speed;
        buf[2] = gridWidth;
        buf[3] = isCircle ? 1 : 0;
        buf[4] = yScale;
        buf[5] = hueScale;
        buf[6] = hueOffset;
        buf[7] = saturation;
        buf[8] = isRunningLight ? 1 : 0;
        buf[9] = gap;
        buf[10] = tailSize;
        buf[11] = direction ? 1: 0;
        profiles.put(profileName, buf);
    }
}