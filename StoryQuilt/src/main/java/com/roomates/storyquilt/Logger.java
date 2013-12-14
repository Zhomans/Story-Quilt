package com.roomates.storyquilt;

import android.util.Log;

/**
 * Created by chris on 12/14/13.
 */
public class Logger {
    //Built in Logger
    public static void Log(String name, String... message){
        StringBuilder sb = new StringBuilder();
        for (String sub:message){
            sb.append(sub);
        }
        Log.i("Logger " + name, sb.toString().equals("") ? "Debug" : sb.toString());
    }
}
