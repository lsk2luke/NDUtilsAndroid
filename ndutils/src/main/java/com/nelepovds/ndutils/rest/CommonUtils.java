package com.nelepovds.ndutils.rest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.AlphaAnimation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    public static String getStringFromUrl(String url, String httpMethod) {
        return getStringFromUrl(url, httpMethod, null, null);
    }

    public static String getStringFromUrl(String url, String httpMethod,String user, String password) {
        String retString = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            if (user != null && password !=null){
                CredentialsProvider credProvider = new BasicCredentialsProvider();
                credProvider.setCredentials(new AuthScope(
                                AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                        new UsernamePasswordCredentials(user,
                                password));
                httpclient.setCredentialsProvider(credProvider);
            }


            HttpUriRequest httpUriRequest=null;
            if (httpMethod.equalsIgnoreCase("get")) {
                httpUriRequest = new HttpGet(url);
            } else  if (httpMethod.equalsIgnoreCase("post")) {
                httpUriRequest = new HttpPost(url);
             } else  if (httpMethod.equalsIgnoreCase("delete")) {
                httpUriRequest = new HttpDelete(url);
            } else  if (httpMethod.equalsIgnoreCase("put")) {
                httpUriRequest = new HttpPut(url);
            }

            HttpResponse response = httpclient.execute(httpUriRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataInputStream dis = new DataInputStream(entity.getContent());
                byte[] buffer = new byte[1024];// In bytes
                int realyReaded;

                while ((realyReaded = dis.read(buffer)) > -1) {
                    baos.write(buffer, 0, realyReaded);
                }
                retString = baos.toString("UTF-8");
            }

        } catch ( IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retString;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        // Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static final String DATE_TIME_FORMAT = "HH:mm";
    public static final String DATE_FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDateTime(String givenDate, String formatType) {
        Date retDate = null;

        if (givenDate != null) {
            try {
                retDate = new SimpleDateFormat(
                        formatType == null ? DATE_FULL_FORMAT : formatType)
                        .parse(givenDate);
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }
        return retDate;
    }

    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

    public static Boolean isTimeBetween(Date timeStart, Date timeEnd,
                                        Date current) {
        Boolean retBetween = false;
        Calendar cs = Calendar.getInstance();
        cs.setTime(timeStart);
        int from = cs.get(Calendar.HOUR_OF_DAY) * 100 + cs.get(Calendar.MINUTE);
        Calendar ce = Calendar.getInstance();
        ce.setTime(timeEnd);
        int to = ce.get(Calendar.HOUR_OF_DAY) * 100 + ce.get(Calendar.MINUTE);

        Calendar cc = Calendar.getInstance();
        cc.setTime(current);
        int t = cc.get(Calendar.HOUR_OF_DAY) * 100 + cc.get(Calendar.MINUTE);
        // System.out.println("CommonUtils.isTimeBetween() From:" + from +
        // " To:"
        // + to + " t:" + t);
        retBetween = to > from && t >= from && t <= to || to < from
                && (t >= from || t <= to);

        return retBetween;
    }

    public static int dayOfWeek() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = ((dayOfWeek + 5) % 7) + 1; // Transforming so that monday =
        // 1 and sunday = 7
        return dayOfWeek;
    }


    public static Boolean isPhoneAvail(Context context) {
        return (((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number() != null);

    }

    public static void setViewAlpha (View targetView, float alpha){
        setViewAlpha(targetView,alpha,alpha,0);
    }

    public static void setViewAlpha (View targetView, float alphaStart, float alphaEnd, long duration) {
        AlphaAnimation aa;

        aa = new AlphaAnimation(alphaStart, alphaEnd);
        aa.setFillBefore(true);
        aa.setFillAfter(true);
        aa.setFillEnabled(true);
        aa.setDuration(duration);
        targetView.setAnimation(aa);
    }

}
