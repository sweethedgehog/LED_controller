package com.example.ledcontroller;

import android.os.Bundle;
import android.provider.Telephony;
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

public class PartyActivity extends AppCompatActivity {
    private static final String TAG = "Party";
    private static HashMap<String, int[]> profiles = new HashMap<>();
    public static ArrayList<String> profilesNames = new ArrayList<>();
    public static String currProfile = "";
    private static int brightness, speed;
    private static int ratio, descendingStep, minSaturation, maxSaturation, minBrightness,
        maxBrightness, baseStep, minHue, maxHue;
    private static boolean isRainbow;
    private static boolean isChangingProfile = false;

    private ImageButton modesButton;
    private ImageButton profilesButton;
    private ImageButton deleteButton;
    private TextView currProfileView;
    private EditText brightnessView;
    private SeekBar brightnessSeekBar;
    private EditText speedView;
    private SeekBar speedSeekBar;

    private EditText ratioView;
    private EditText descendingStepView;
    private EditText minSaturationView;
    private SeekBar minSaturationSeekBar;
    private EditText maxSaturationView;
    private SeekBar maxSaturationSeekBar;
    private EditText minBrightnessView;
    private SeekBar minBrightnessSeekBar;
    private EditText maxBrightnessView;
    private SeekBar maxBrightnessSeekBar;
    private Switch isRainbowView;
    private EditText baseStepView;
    private EditText minHueView;
    private SeekBar minHueSeekBar;
    private EditText maxHueView;
    private SeekBar maxHueSeekBar;

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
        setContentView(R.layout.activity_party);

        currProfileView = findViewById(R.id.profile_text_view_party);
        modesButton = findViewById(R.id.modes_button_party);
        profilesButton = findViewById(R.id.profiles_button_party);
        deleteButton = findViewById(R.id.delete_profile_button_party);
        setProfile(currProfile);

        brightnessView = findViewById(R.id.brightness_seek_bar_view_party);
        brightnessSeekBar = findViewById(R.id.brightness_seek_bar_party);
        speedView = findViewById(R.id.speed_seek_bar_view_party);
        speedSeekBar = findViewById(R.id.speed_seek_bar_party);

        ratioView = findViewById(R.id.ratio_view_party);
        descendingStepView = findViewById(R.id.descending_step_view_party);
        minSaturationView = findViewById(R.id.min_saturation_seek_bar_view_party);
        minSaturationSeekBar = findViewById(R.id.min_saturation_seek_bar_party);
        maxSaturationView = findViewById(R.id.max_saturation_seek_bar_view_party);
        maxSaturationSeekBar = findViewById(R.id.max_saturation_seek_bar_party);
        minBrightnessView = findViewById(R.id.min_brightness_seek_bar_view_party);
        minBrightnessSeekBar = findViewById(R.id.min_brightness_seek_bar_party);
        maxBrightnessView = findViewById(R.id.max_brightness_seek_bar_view_party);
        maxBrightnessSeekBar = findViewById(R.id.max_brightness_seek_bar_party);
        isRainbowView = findViewById(R.id.is_rainbow_party_toggle);
        baseStepView = findViewById(R.id.base_step_view_party);
        minHueView = findViewById(R.id.min_hue_seek_bar_view_party);
        minHueSeekBar = findViewById(R.id.min_hue_seek_bar_party);
        maxHueView = findViewById(R.id.max_hue_seek_bar_view_party);
        maxHueSeekBar = findViewById(R.id.max_hue_seek_bar_party);

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
    private void setProfile(String profileName){
        int[] buf = profiles.get(profileName);
        brightness = buf[0];
        speed = buf[1];
        ratio = buf[2];
        descendingStep = buf[3];
        minSaturation = buf[4];
        maxSaturation = buf[5];
        minBrightness = buf[6];
        maxBrightness = buf[7];
        isRainbow = buf[8] == 1;
        baseStep = buf[9];
        minHue = buf[10];
        maxHue = buf[11];
        currProfileView.setText("Профиль: " + profileName);
    }
    private void updateAll(){
        isChangingProfile = true;
        brightnessView.setText(String.valueOf(brightness));
        brightnessSeekBar.setProgress(brightness);
        speedView.setText(String.valueOf(speed));
        speedSeekBar.setProgress(speed);

        ratioView.setText(String.valueOf(ratio));
        descendingStepView.setText(String.valueOf(descendingStep));
        minSaturationView.setText(String.valueOf(minSaturation));
        minSaturationSeekBar.setProgress(minSaturation);
        maxSaturationView.setText(String.valueOf(maxSaturation));
        maxSaturationSeekBar.setProgress(maxSaturation);
        minBrightnessView.setText(String.valueOf(minBrightness));
        minBrightnessSeekBar.setProgress(minBrightness);
        maxBrightnessView.setText(String.valueOf(maxBrightness));
        maxBrightnessSeekBar.setProgress(maxBrightness);
        isRainbowView.setChecked(isRainbow);
        baseStepView.setText(String.valueOf(baseStep));
        minHueView.setText(String.valueOf(minHue));
        minHueSeekBar.setProgress(minHue);
        maxHueView.setText(String.valueOf(maxHue));
        maxHueSeekBar.setProgress(maxHue);

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
                ProjectManager.showPopupMenu(PartyActivity.this, profilesButton, profilesNames, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (profilesNames.get(i).equals("Добавить")){
                            ProjectManager.showInputDialog(PartyActivity.this,
                                    "Введите имя нового профиля", new BluetoothFunc() {
                                @Override
                                public void run(String s) {
                                    if (profilesNames.contains(s)){
                                        Toast.makeText(PartyActivity.this,
                                                "Такой профиль уже существует",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    BluetoothManager.send(("pn" + s).getBytes());
                                    Toast.makeText(PartyActivity.this, "Создан новый профиль \""
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
                ProjectManager.showPopupMenu(PartyActivity.this, modesButton, ProjectManager.modes, new AdapterView.OnItemClickListener() {
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
                    ProjectManager.hideKeyboard(PartyActivity.this, brightnessView);
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
                    ProjectManager.hideKeyboard(PartyActivity.this, speedView);
                    return true;
                }
                return false;
            }
        });

        ratioView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(ratioView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        ratioView.setText(String.valueOf(buf));
                    }
                    ratio = buf;
                    BluetoothManager.send(("sc" + buf).getBytes());
                    ProjectManager.hideKeyboard(PartyActivity.this, ratioView);
                    return true;
                }
                return false;
            }
        });
        descendingStepView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(descendingStepView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        descendingStepView.setText(String.valueOf(buf));
                    }
                    descendingStep = buf;
                    BluetoothManager.send(("sd" + buf).getBytes());
                    ProjectManager.hideKeyboard(PartyActivity.this, descendingStepView);
                    return true;
                }
                return false;
            }
        });
        minSaturationView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(minSaturationView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        minSaturationView.setText(String.valueOf(buf));
                    }
                    minSaturation = buf;
                    BluetoothManager.send(("se" + buf).getBytes());
                    minSaturationSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, minSaturationView);
                    return true;
                }
                return false;
            }
        });
        minSaturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minSaturationView.setText(String.valueOf(minSaturationSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                minSaturation = minSaturationSeekBar.getProgress();
                BluetoothManager.send(("se" + minSaturation).getBytes());
            }
        });
        maxSaturationView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(maxSaturationView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        maxSaturationView.setText(String.valueOf(buf));
                    }
                    maxSaturation = buf;
                    BluetoothManager.send(("sf" + buf).getBytes());
                    maxSaturationSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, maxSaturationView);
                    return true;
                }
                return false;
            }
        });
        maxSaturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxSaturationView.setText(String.valueOf(maxSaturationSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                maxSaturation = maxSaturationSeekBar.getProgress();
                BluetoothManager.send(("se" + maxSaturation).getBytes());
            }
        });
        minBrightnessView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(minBrightnessView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        minBrightnessView.setText(String.valueOf(buf));
                    }
                    minBrightness = buf;
                    BluetoothManager.send(("sg" + buf).getBytes());
                    minBrightnessSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, minBrightnessView);
                    return true;
                }
                return false;
            }
        });
        minBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minBrightnessView.setText(String.valueOf(minBrightnessSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                minBrightness = minBrightnessSeekBar.getProgress();
                BluetoothManager.send(("se" + minBrightness).getBytes());
            }
        });
        maxBrightnessView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(maxBrightnessView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        maxBrightnessView.setText(String.valueOf(buf));
                    }
                    maxBrightness = buf;
                    BluetoothManager.send(("sh" + buf).getBytes());
                    maxBrightnessSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, maxBrightnessView);
                    return true;
                }
                return false;
            }
        });
        maxBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxBrightnessView.setText(String.valueOf(maxBrightnessSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                maxBrightness = maxBrightnessSeekBar.getProgress();
                BluetoothManager.send(("se" + maxBrightness).getBytes());
            }
        });
        isRainbowView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isChangingProfile) return;
                isRainbow = b;
                BluetoothManager.send(("si" + (isRainbow ? 1 : 0)).getBytes());
            }
        });
        baseStepView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(baseStepView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        baseStepView.setText(String.valueOf(buf));
                    }
                    baseStep = buf;
                    BluetoothManager.send(("sj" + buf).getBytes());
                    ProjectManager.hideKeyboard(PartyActivity.this, baseStepView);
                    return true;
                }
                return false;
            }
        });
        minHueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(minHueView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        minHueView.setText(String.valueOf(buf));
                    }
                    minHue = buf;
                    BluetoothManager.send(("sk" + buf).getBytes());
                    minHueSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, minHueView);
                    return true;
                }
                return false;
            }
        });
        minHueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minHueView.setText(String.valueOf(minHueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                minHue = minHueSeekBar.getProgress();
                BluetoothManager.send(("se" + minHue).getBytes());
            }
        });
        maxHueView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if (isChangingProfile) return false;
                    int buf = Integer.parseInt(String.valueOf(maxHueView.getText()));
                    if (buf > 255) {
                        buf = 255;
                        maxHueView.setText(String.valueOf(buf));
                    }
                    maxHue = buf;
                    BluetoothManager.send(("sa" + buf).getBytes());
                    maxHueSeekBar.setProgress(buf);
                    ProjectManager.hideKeyboard(PartyActivity.this, maxHueView);
                    return true;
                }
                return false;
            }
        });
        maxHueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maxHueView.setText(String.valueOf(maxHueSeekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isChangingProfile) return;
                maxHue = maxHueSeekBar.getProgress();
                BluetoothManager.send(("se" + maxHue).getBytes());
            }
        });;
    }
    private static void addPName(String name){
        profilesNames.remove("Добавить");
        profilesNames.add(name);
        profilesNames.add("Добавить");
    }
    private void saveProfileSettings(String profileName){
        int[] buf = new int[profiles.get(profileName).length];
        buf[0] = brightness;
        buf[1] = speed;
        buf[2] = ratio;
        buf[3] = descendingStep;
        buf[4] = minSaturation;
        buf[5] = maxSaturation;
        buf[6] = minBrightness;
        buf[7] = maxBrightness;
        buf[8] = isRainbow ? 1 : 0;
        buf[9] = baseStep;
        buf[10] = minHue;
        buf[11] = maxHue;
        profiles.put(profileName, buf);
    }
    public static void clearAll(){
        profiles.clear();
        profilesNames.clear();
    }
}