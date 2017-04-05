package com.example.oleg.downloadpict;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by oleg on 02.04.17.
 */

public class DownloadJson {

    public static JSONArray downloadJson(String... params) throws JSONException {
        if (params != null && params.length > 0) {
            URL url = null;
            try {
                url = new URL(params[0]);
                HttpURLConnection connection;
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setInstanceFollowRedirects(true);
                int status = connection.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    InputStream is = new BufferedInputStream(connection.getInputStream());
                    String mContent = StringUtils.readInputStream(is);
                    JSONTokener jtk = new JSONTokener(mContent);
                    JSONArray jsonArray = (JSONArray) jtk.nextValue();
                    return jsonArray;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
