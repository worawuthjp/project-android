package com.application.myapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,MainMenu.class);
        startActivity(intent);

        /*final TextView showTxt = (TextView)findViewById(R.id.showTxt);
        final Button scanBtn = (Button)findViewById(R.id.scanBtn);
        final Button writeBtn = findViewById(R.id.writeBtn);
        final EditText writeTxt = findViewById(R.id.writeTxt);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME).build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes).build();
        }else{
            soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,5);

        }

        final int sound1 = soundPool.load(this,R.raw.barcodebeep,1);
        final int sound2 = soundPool.load(this,R.raw.serror,1);

        try {
            rfidWithUHF = RFIDWithUHF.getInstance();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        if(rfidWithUHF.init()){
            Toast.makeText(getApplicationContext(),"INIT",Toast.LENGTH_SHORT).show();

            writeTxt.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //keyCode == 139 || keyCode == 280
                    if(keyCode == 293 ){
                        soundPool.play(sound1,1,1,0,0,1);
                        rfidWithUHF.setEPCTIDUserMode(true,0,8);

                        String textID[] = rfidWithUHF.inventorySingleTagEPC_TID_USER();
                        writeTxt.setText(textID[0]+textID[1]);

                        return true;
                    }

                    //Toast.makeText(getApplicationContext(),Integer.toString(event.getKeyCode()),Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rfidWithUHF.setEPCTIDUserMode(true,0,8);

                    String textID[] = rfidWithUHF.inventorySingleTagEPC_TID_USER();
                    writeTxt.setText(textID[0]+textID[1]);
                    soundPool.play(sound1,1,1,1,0,1);

                }
            });
        }*/
    }



    /*protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }*/
}