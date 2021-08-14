package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private static int changeFrogSize=0;
    ViewPager2 viewPager;

    MainAdapter mainAdapter;
    Intent intent;


    int fragmentNavigation = 1;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_paper);
        mainAdapter = new MainAdapter(this);
        viewPager.setAdapter(mainAdapter);

        viewPager.setCurrentItem(fragmentNavigation, false); //default page
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();
        fragmentNavigation = intent.getIntExtra("fragment_navigation", 1);
        viewPager.setCurrentItem(fragmentNavigation, false); //default page

    }

    //디바이스 뒤로가기 클릭 했을 때
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("EXIT").setMessage("나가시겠습니까?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed(); // 이 코드를 통해 꺼짐
                    }
        }).setNegativeButton("Cancel", null).create().show();
    }

    public static int getChangeFrogSize() {
        return changeFrogSize;
    }

    public static void setChangeFrogSize(int changeSize) {
        changeFrogSize = changeSize;
    }
}

