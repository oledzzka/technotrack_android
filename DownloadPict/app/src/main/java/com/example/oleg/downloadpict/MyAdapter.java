package com.example.oleg.downloadpict;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by oleg on 26.03.17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ExecutorService service;
    private JSONArray jsonArray;
    private HashMap<Integer, Bitmap> tree;

    @SuppressLint("UseSparseArrays")
    public MyAdapter(JSONArray array) {

        jsonArray = array;
        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);
        tree = new HashMap<Integer, Bitmap>();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

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
        if (tree.containsKey(position)) {
            holder.setmImage(tree.get(position));
        }
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String stringUrl = (String) jsonArray.get(position);
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
                        holder.setmImage(bitmap);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (jsonArray != null) {
            return jsonArray.length();
        } else {
            return 0;
        }
    }
}
