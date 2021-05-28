package com.example.intelligenthelmet;

public class NewUser {
    public String emergency_number, gps, last_worn, mobile_number, mpu, mp3, status;

    public NewUser(){}
    public NewUser(String emergency_number, String gps, String last_worn, String mobile_number, String mpu, String mp3, String status){
        this.emergency_number = emergency_number;
        this.gps = gps;
        this.last_worn = last_worn;
        this.mobile_number = mobile_number;
        this.mpu = mpu;
        this.mp3 = mp3;
        this.status = status;
    }
}
