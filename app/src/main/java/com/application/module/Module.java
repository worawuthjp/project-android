package com.application.module;

import android.content.SharedPreferences;

public class Module {
    private String host;

    public Module(){
        this.host = "https://pilowy.myminesite.com";
    }

    public String getUrl(){
        return this.host;
    }
}
