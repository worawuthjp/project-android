package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DamActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sowMatingBtn;
    private Button sowBirthBtn;
    private Button sowVaccineBtn;
    private Button sowPartyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dam);

        sowMatingBtn = (Button)findViewById(R.id.sowMatingBtn);
        sowBirthBtn = (Button)findViewById(R.id.sowBirthBtn);

        sowMatingBtn.setOnClickListener(this);

        sowBirthBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sowMatingBtn :
                Intent intent = new Intent(DamActivity.this,SowMatingActivity3.class);
                startActivity(intent);
                break;
            case R.id.sowBirthBtn :
                Intent intent1 = new Intent(DamActivity.this,SowBirthIndexActivity.class);
                startActivity(intent1);
                break;
        }
    }
}