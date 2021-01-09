package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{
    private Button sireBtn;
    private Button damBtn;
    private Button pairBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sireBtn = (Button)findViewById(R.id.sireBtn);
        damBtn = (Button)findViewById(R.id.damBtn);
        pairBtn = (Button)findViewById(R.id.pairBtn);

        sireBtn.setOnClickListener(this);

        damBtn.setOnClickListener(this);

        pairBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.sireBtn :
                intent = new Intent(MainMenu.this,SireActivity.class);
                startActivity(intent);
                break;

            case R.id.damBtn :
                intent = new Intent(MainMenu.this,DamActivity.class);
                startActivity(intent);
                break;
            case R.id.pairBtn :
                intent = new Intent(MainMenu.this,PairSowActivity.class);
                startActivity(intent);
                break;
        }
    }
}