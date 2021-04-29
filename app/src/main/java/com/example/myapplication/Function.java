package com.example.myapplication;

import java.util.Calendar;

public class Function {
    public static String wishMe(){
        String s ="";
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);

        if(time >= 0 && time < 12){
            s = "Good Morning";
        }else if(time >= 12 && time < 16){
            s = "Good Afternoon";
        }else if(time >= 16 && time < 21){
            s = "Good Evening";
        }else if(time >= 21 && time < 22){
            s = "Good Night";
        }else if(time >= 22 && time < 24){
            s = "You need to take a rest... it's too late";
        }
        return s;
    }
}
