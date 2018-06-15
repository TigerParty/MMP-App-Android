package com.thetigerparty.argodflib.HelperClass;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class HttpProcess {
    private static final String TAG = "HttpProcess";

    public static String httpGet(String api) throws SocketTimeoutException {
        String result = "";
        BufferedReader reader = null;
        StringBuilder builder;
        try {
            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(30000);
            con.setRequestMethod("GET");
            con.connect();

            if(con.getResponseCode() == 200){
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                builder = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line + "\n");
                }

                if(getResult(builder.toString()).equals("success")){
                    result = getInfo(builder.toString());
                }
            }

            con.disconnect();
        }
        catch(SocketTimeoutException err) {
            throw err;
        }
        catch (Exception e){
            e.printStackTrace();
            result = "fail";
        }
        finally {
            if(reader != null){
                try {
                    reader.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static String httpPost(String api, String params, List<Attachment> list_attachment, Report report){
        String result = "";
        try {
            MultipartUtility multipart = new MultipartUtility(api, "UTF-8");

            multipart.addFormField("info", params);

            for (int i = 0; i < list_attachment.size(); i++){
                File file = new File(list_attachment.get(i).path);
                if(file.exists()) {
                    if (ImageProcess.checkImageSize(file)) {
                        multipart.addFilePart("attachment" + i, ImageProcess.compressImage(file, report));
                    }
                    else {
                        multipart.addFilePart("attachment" + i, file);
                    }
                }
            }

            List<String> response = multipart.finish();
            for (String line : response) {
                result = line;
            }

            Logger.d(result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }
        finally {
            ImageProcess.removeDir();
        }

        return result;
    }

    public static String httpPost(String api, String params, List<Attachment> list_attachment){
        String result = "";
        try {
            MultipartUtility multipart = new MultipartUtility(api, "UTF-8");

            multipart.addFormField("info", params);
            multipart.addFormField("tracker", "tracker");

            for (int i = 0; i < list_attachment.size(); i++){
                Attachment attachment = list_attachment.get(i);
                File file = new File(attachment.path);

                if(file.exists()) {
                    if (ImageProcess.checkImageSize(file)) {
                        multipart.addFilePart("attachment" + i, ImageProcess.compressImage(file, attachment));
                    }
                    else {
                        multipart.addFilePart("attachment" + i, file);
                    }
                }
            }

            List<String> response = multipart.finish();
            for (String line : response) {
                result = line;
            }

            Logger.d(result);
        }
        catch (Exception e) {
            e.printStackTrace();
            result = "fail";
        }
        finally {
            ImageProcess.removeDir();
        }

        return result;
    }

    public static String httpPost(String api, String params){
        String result = "";
        try{
            MultipartUtility multipart = new MultipartUtility(api, "UTF-8");
            multipart.addFormField("info", params);

            List<String> response = multipart.finish();

            for(String line : response){
                result = line;
            }

        } catch (Exception e){
            Log.e("HttpProcess", e.getMessage());
        }

        return result;
    }

    public static String post(String uri, JSONObject params) {
        String result = "";
        try{
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(params.toString().getBytes("UTF-8"));
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String response;
            while ((response = br.readLine()) != null) {
                stringBuilder.append(response + "\n");
            }
            br.close();
            connection.disconnect();

            result = stringBuilder.toString();
            Log.d(TAG, "post: " + result);

        } catch (Exception e){
            Log.e("HttpProcess", e.getMessage());
        }

        return result;
    }

    public static String getResult(String response)
    {
        String result;
        try{
            JSONObject jsObj = new JSONObject(response);
            result = jsObj.get("result").toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            result = "fail";
        }

        return result;
    }

    public static String getInfo(String response)
    {
        String info;
        try{
            JSONObject jsObj = new JSONObject(response);
            info = jsObj.get("info").toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            info = "fail";
        }

        return info;
    }
}