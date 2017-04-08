package com.example.oleg.downloadpict;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by oleg on 26.03.17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ExecutorService service;
    private ArrayList<String> jsonArray;
    private Map<Integer, Bitmap> tree;
    private Handler handler;

    @SuppressLint("UseSparseArrays")
    public MyAdapter(JSONArray array) {
        if (array == null) {
            jsonArray = new ArrayList<>(1);
            jsonArray.add(null);
        } else {
            jsonArray = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                try {
                    jsonArray.add(array.getString(i));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        int threadsNum = Math.max(1, Runtime.getRuntime().availableProcessors()/2);
        service = Executors.newFixedThreadPool(threadsNum);
        tree = new ConcurrentHashMap<>();
        handler = new Handler();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public void setImage(int resId) {
            mImage.setImageResource(resId);
        }

        public void setmImage(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
        }

        private ImageView mImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.icon);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_element,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Bitmap image = tree.get(position);
        if (image != null) {
            holder.setmImage(image);
        }
        else {
            holder.setImage(android.R.drawable.sym_def_app_icon);
            final String stringUrl = jsonArray.get(position);
            if (stringUrl != null) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(stringUrl);
                            HttpURLConnection connection;
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            connection.setInstanceFollowRedirects(true);
                            int responseCode = connection.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                InputStream is = new BufferedInputStream(connection.getInputStream());
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                tree.put(position, bitmap);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyItemChanged(position);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
    }
}
