package com.wangxiandeng.saviortest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wangxiandeng.savior.PatchLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将补丁文件拷贝到sd卡
        FileUtil.copyJarToFile(this);

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        //点击加载补丁
        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PatchLoader.getInstance().loadPatch(PatchUtil.PATCH_PATH);
                    Toast.makeText(MainActivity.this, "load success", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void show() {
        Toast.makeText(this, "我有bug!", Toast.LENGTH_SHORT).show();
    }
}
