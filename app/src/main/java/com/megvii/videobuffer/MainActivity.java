package com.megvii.videobuffer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWriteExternalPerm();
            }
        });
    }


    private void requestWriteExternalPerm(){
        if (Build.VERSION.SDK_INT >= M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //进行权限
                requestPermissions(new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }else{
                requestCameraPerm();
            }
        }else{
            requestCameraPerm();
        }
    }

    private void requestCameraPerm(){
        if (Build.VERSION.SDK_INT >= M){
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                //permission apply
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
            }else{
                enterNextPage();
            }
        }else{
            enterNextPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2){
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED){

            }else{
                enterNextPage();
            }
        }else if (requestCode == 1){
            if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED){

            }else{
                requestCameraPerm();
            }
        }
    }

    private void enterNextPage(){
        Intent intent = new Intent(MainActivity.this, VideoActivity.class);
        MainActivity.this.startActivity(intent);
    }
}
