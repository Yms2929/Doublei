package com.example.doublei.Etc;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.doublei.Bar.NavigationTabBar;
import com.example.doublei.Fragment.FirstFragment;
import com.example.doublei.Fragment.HomeFragment;
import com.example.doublei.Fragment.SecondFragment;
import com.example.doublei.Fragment.ViewPagerAdapter;
import com.example.doublei.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String SignUp = "false";
    ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if(SignUp=="false") {
//            Intent intent = new Intent(this, ChildPicture.class);
//            startActivity(intent);
//        }
//        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

//        setSupportActionBar(toolbar);
        initUI();

        // SDK 23이상 일때 권한요청
        int permissionCamera = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionReadStorage = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permissionAlertWindow = ActivityCompat.checkSelfPermission(getApplicationContext(), Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

        if (permissionCamera != PackageManager.PERMISSION_GRANTED || permissionReadStorage != PackageManager.PERMISSION_GRANTED || permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

//        if (permissionAlertWindow != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
//        }
    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment());
        viewPagerAdapter.addFragments(new FirstFragment());
        viewPagerAdapter.addFragments(new SecondFragment());
        viewPager.setAdapter(viewPagerAdapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("Eye")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .title("Graph")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .title("Set")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }
}