package com.chinaoly.histogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HistogramView histogramView = findViewById(R.id.histogram_view);

        List<Float> singleList = new ArrayList<>();
        Random random = new Random();
        while (singleList.size() < 12) {
            int randomInt = random.nextInt(100);
            singleList.add((float) randomInt);
        }
        histogramView.setList(singleList);
    }
}
