package com.application.myapp.UHF;

import java.lang.String;

import android.content.Context;
import android.widget.Toast;

import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;

public class ScanUHF{
    private RFIDWithUHF rfidWithUHF;

    public ScanUHF(Context context){
        //Toast.makeText(context,"INIT",Toast.LENGTH_SHORT).show();
        try {
            rfidWithUHF = RFIDWithUHF.getInstance();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        if(rfidWithUHF.init()){
            //Toast.makeText(context,"INIT",Toast.LENGTH_SHORT).show();
            System.out.println("init");
        }
    }

    public void setPrtLen(int prt,int len){
        this.rfidWithUHF.setEPCTIDUserMode(true,0,8);
    }

    public RFIDWithUHF getRFIDWithUHF(){
        return this.rfidWithUHF;
    }

    public String[] getUHFRead(){
        return this.rfidWithUHF.inventorySingleTagEPC_TID_USER();
    }
}
