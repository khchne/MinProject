package com.example.minproject;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiDuTranslation {
    private final static String APP_ID = "20201121000622397";
    private final static String APP_SECRET = "fiEstHyQVjr1LC4Q4GBt";

    private final static String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    public static void postToBaiDu(String query, final TextView resultView) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder requestBuild = new FormBody.Builder();
        RequestBody requestBody = requestBuild.build();

        final Request request = new Request.Builder().url(getRequestUrl(query))
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MinProject", "连接失败" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body() == null) {
                    response.body().close();
                }

                String result = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String transResult = jsonObject.getString("trans_result");
                    JSONArray transResultArray = new JSONArray(transResult);
                    final JSONObject resultObject = transResultArray.getJSONObject(0);

                    resultView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                resultView.setText(resultObject.get("dst").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("BaiDu", "get  " + result);
            }

        });
    }

    private static String getRequestUrl(String query) {
        String salt = "" + Math.random() * 100;
        return url + "?q=" + query + "&fromzh&to=en&appid=" + APP_ID + "&salt="
                + salt + "&sign=" + getMD5(salt, query);
    }

    public static String getMD5(String salt, String query) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            String origin = APP_ID + query + salt + APP_SECRET;
            byte[] bytes = digest.digest(origin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int c = b & 0xff; //负数转换成正数
                String result = Integer.toHexString(c); //把十进制的数转换成十六进制的书
                if (result.length() < 2) {
                    sb.append(0); //让十六进制全部都是两位数
                }
                sb.append(result);
            }
            return sb.toString(); //返回加密后的密文
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
