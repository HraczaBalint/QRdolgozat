package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Button buttonScan, buttonKiir;
    private TextView textViewEredmeny;
    private boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("QR CODE SCAN");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });

        buttonKiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writePermission){
                    try {
                        Naplozas.kiir(textViewEredmeny.getText().toString());
                        Toast.makeText(MainActivity.this, "Sikeres szöveg kiíratás", Toast.LENGTH_SHORT).show();
                        textViewEredmeny.setText(""); // Kis extra a szöveg eltántetésére kiíratás után. Így szebb szerintem.
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Sikertelen szöveg kiíratás: " + e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Kiléptél a scanből", Toast.LENGTH_SHORT).show();
            } else {
                textViewEredmeny.setText(result.getContents());
                try {
                    Uri url = Uri.parse(result.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                } catch (Exception exception){
                    Log.d("URI ERROR", exception.toString());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init() {
        buttonScan = findViewById(R.id.button_scan);
        buttonKiir = findViewById(R.id.button_kiir);
        textViewEredmeny = findViewById(R.id.text_view_eredmeny);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            writePermission = false;
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};ActivityCompat.requestPermissions(this, permissions, 1);
        }else{
            writePermission = true;
        }
    }




}