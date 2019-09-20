package com.example.barcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnTakePicture, btnScanBarcode,booksBt,userBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {

        btnTakePicture = findViewById(R.id.shell1);
        btnScanBarcode = findViewById(R.id.shell2);
        booksBt = findViewById(R.id.booksBt);
        userBt = findViewById(R.id.userBt);
        btnTakePicture.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);
        booksBt.setOnClickListener(this);
        userBt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.shell1:
                startActivity(new Intent(MainActivity.this, ScannedBarCodeActivity.class));
                break;
            case R.id.shell2:
                startActivity(new Intent(MainActivity.this, ShellActivity2.class));
                break;
            case R.id.booksBt:
                startActivity(new Intent(MainActivity.this, MisplacedActivity.class));
                break;
            case R.id.userBt:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                break;

        }
    }
}
