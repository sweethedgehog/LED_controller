package com.example.ledcontroller;

import android.util.Log;

import java.sql.Array;
import java.util.Arrays;

public class Alarm {
    public Alarm(){}
    public Alarm(boolean isOn, int hour, int minute, boolean[] days, int razingTime, int mode, String profileName){
        this.isOn = isOn;
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        this.razingTime = razingTime;
        this.mode = mode;
        this.profileName = profileName;
    }
    public Alarm(byte[] data){
        int[] buf = new int[data.length];
        for (int i = 0; i < data.length; ++i){
            buf[i] = (data[i] + 256) % 256;
        }
        isOn = buf[0] / 128 == 1;
        for (int i = 0; i < 7; ++i)
            days[i] = (int)(buf[0] % Math.pow(2, i + 1) / Math.pow(2, i)) == 1;
        minute = buf[1];
        hour = buf[2];
        razingTime = buf[3];
        mode = buf[4];
        profileName = new String(Arrays.copyOfRange(data, 5, data.length));
    }
    public boolean isOn = true;
    public int hour = 0;
    public int minute = 0;
    public boolean[] days = new boolean[7];
    public int razingTime = 0;
    public int mode = 0;
    public String profileName = "default";
    public byte[] getRaw(){
        byte[] ans = new byte[5];
        int buf = 128 * (isOn ? 1 : 0);
        for (int i = 0; i < 7; ++i){
            buf += (int) ((days[i] ? 1 : 0) * Math.pow(2, i));
        }
        ans[0] = (byte) buf;
        ans[1] = (byte) minute;
        ans[2] = (byte) hour;
        ans[3] = (byte) razingTime;
        ans[4] = (byte) mode;
        byte[] bufArr = new byte[5 + profileName.length()];
        System.arraycopy(ans, 0, bufArr, 0, 5);
        System.arraycopy(profileName.getBytes(), 0, bufArr, 5, profileName.length());
        return bufArr;
    }
}
