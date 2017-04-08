package com.example.oleg.downloadpict;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private JSONArray jsonArray = null;
    final private String SOURCE = "http://188.166.49.215/tech/imglist.json";
    final private String JSON = "JSON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonArray = DownloadJson.downloadJson(SOURCE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(new MyAdapter(jsonArray));
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String jsonArrayString = data.getStringExtra(JSON);
        if ( !jsonArrayString.isEmpty() ) {
            try {
                jsonArray = new JSONObject().getJSONArray(jsonArrayString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (jsonArray != null) {
            outState.putString(JSON, jsonArray.toString());
        }
    }
}
