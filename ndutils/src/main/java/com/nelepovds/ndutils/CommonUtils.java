package com.nelepovds.ndutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

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
import java.util.List;
import java.util.Locale;

public class CommonUtils {


    public static String getStringFromUrl(String url, String httpMethod) {
        return getStringFromUrl(url, httpMethod, null, null);
    }

    public static String getStringFromUrl(String url, String httpMethod, String user, String password) {
        String retString = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            if (user != null && password != null) {
                CredentialsProvider credProvider = new BasicCredentialsProvider();
                credProvider.setCredentials(new AuthScope(
                                AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                        new UsernamePasswordCredentials(user,
                                password)
                );
                httpclient.setCredentialsProvider(credProvider);
            }


            HttpUriRequest httpUriRequest = null;
            if (httpMethod.equalsIgnoreCase("get")) {
                httpUriRequest = new HttpGet(url);
            } else if (httpMethod.equalsIgnoreCase("post")) {
                httpUriRequest = new HttpPost(url);
            } else if (httpMethod.equalsIgnoreCase("delete")) {
                httpUriRequest = new HttpDelete(url);
            } else if (httpMethod.equalsIgnoreCase("put")) {
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

        } catch (IOException e) {
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
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FULL_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FULL_FORMAT_FILE_SAVE = "yyyy_MM_dd_HH_mm_ss";

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
        if (date == null) {
            date = new Date();
        }
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

    public static void setViewAlpha(View targetView, float alpha) {
        setViewAlpha(targetView, alpha, alpha, 0);
    }

    public static void setViewAlpha(View targetView, float alphaStart, float alphaEnd, long duration) {
        AlphaAnimation aa;

        aa = new AlphaAnimation(alphaStart, alphaEnd);
        aa.setFillBefore(true);
        aa.setFillAfter(true);
        aa.setFillEnabled(true);
        aa.setDuration(duration);
        targetView.setAnimation(aa);
    }

    public static void openUrl(String url, Activity activity) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    public static void email(Activity activity, String email, String subject, String title) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        activity.startActivity(Intent.createChooser(emailIntent, title));
    }

    public static void openMap(Activity activity, String address) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=%s", address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        activity.startActivity(intent);
    }

    public static void share(Activity activity, String title, String shareMessage) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent, title));
    }


    public static interface IDateTimeSetterListener {
        public void didSetNewDate(Button buttonDate, Calendar calendar);

        public void didSetNewTime(Button buttonTime, Calendar calendar);

    }

    /**
     * @param givenDate
     * @param buttonDate
     * @param buttonTime
     * @param dateTimeSetterListener
     */
    public static void loadDateTime(Date givenDate, String emptyStubTitle, final Button buttonDate, final Button buttonTime, final IDateTimeSetterListener dateTimeSetterListener) {
        final Calendar calendar = Calendar.getInstance();
        if (buttonDate != null) {
            buttonDate.setText(givenDate == null ? emptyStubTitle : CommonUtils.formatDate(givenDate, CommonUtils.DATE_FORMAT));
            buttonDate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            buttonDate.setText(CommonUtils.formatDate(calendar.getTime(), CommonUtils.DATE_FORMAT));
                            if (buttonTime != null) {
                                buttonTime.setText(CommonUtils.formatDate(calendar.getTime(), CommonUtils.DATE_TIME_FORMAT));
                            }

                            if (dateTimeSetterListener != null) {
                                dateTimeSetterListener.didSetNewDate(buttonDate, calendar);
                            }
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        }
        if (buttonTime != null) {
            buttonTime.setText(givenDate == null ? emptyStubTitle : CommonUtils.formatDate(givenDate, CommonUtils.DATE_TIME_FORMAT));
            buttonTime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);//Always reset to zero
                            if (buttonDate != null) {
                                buttonDate.setText(CommonUtils.formatDate(calendar.getTime(), CommonUtils.DATE_FORMAT));
                            }
                            buttonTime.setText(CommonUtils.formatDate(calendar.getTime(), CommonUtils.DATE_TIME_FORMAT));

                            if (dateTimeSetterListener != null) {
                                dateTimeSetterListener.didSetNewTime(buttonTime, calendar);
                            }
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                }
            });
        }

    }

    public static void showExceptionAlert(final Activity activity, final String title, final Exception ex) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity).setTitle(title).setMessage(ex.getMessage()).show();
            }
        });
    }

    /**
     * TextView secondTextView = new TextView(this);
     * Shader textShader=new LinearGradient(0, 0, 0, 20,
     * new int[]{Color.GREEN,Color.BLUE},
     * new float[]{0, 1}, TileMode.CLAMP);
     * secondTextView.getPaint().setShader(textShader);
     *
     * @param textView
     * @param gradient
     */
    public static void textViewGradient(TextView textView, LinearGradient gradient) {
        textView.getPaint().setShader(gradient);
    }

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    //Examples
    // http://android.okhelp.cz/drawbitmap-clippath-union-difference-intersect-replace-xor-android-example/
    //
    public static Bitmap imagesXOR(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap resultingImage = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(resultingImage);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap1, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Rect src = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Rect dest = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());

//        canvas.drawBitmap(bitmap2, 0, 0, paint);
        canvas.drawBitmap(bitmap2, src, dest, paint);

        return resultingImage;
    }

    public static Boolean checkEmail(String email) {
        if ((email == null) || (email.length() == 0))
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkMobilePhone(String phone) {
        if ((phone == null) || (phone.length() == 0))
            return false;
        return Patterns.PHONE.matcher(phone).matches();
    }

}
