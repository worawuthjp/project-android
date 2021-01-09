package com.application.module;

public class Module {
    private String host;

    public Module(){
        this.host = "https://pilowy.myminesite.com";
    }

    public String getUrl(){
        return this.host;
    }
}
