package com.example.ledcontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AlarmOffActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        offAlarm();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_off);
        Button offButton = findViewById(R.id.off_buttom_alarm_off_activity);
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offAlarm();
            }
        });
    }
    private void offAlarm(){
        BluetoothManager.send(("to").getBytes());
        finish();
    }
}