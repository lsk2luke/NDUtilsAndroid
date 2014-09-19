package com.nelepovds.ndutils.rest;

import android.app.Activity;
import android.net.Uri;
import android.webkit.URLUtil;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuilder;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class RestApi {

    public static enum HttpMethods {
        GET, POST, DELETE, PUT
    }

    public String serviceBaseUrl;

    public String login;
    public String password;

    public static interface IRestApiListener {
        public <RT extends BaseClass> void complete(String apiMethod, Select cache, RT retObject);

        public void error(String apiMethod, Select cache, Exception e);
    }

    public <RT extends BaseClass> void apiCall(final Activity activity, final String apiMethod, final HttpMethods httpMethod, final BaseClass object, final Select cache, final Class<RT> callBack, final IRestApiListener apiListener) {
        apiCall(activity, apiMethod, httpMethod, object, null, cache, callBack, apiListener);
    }

    public <RT extends BaseClass> void apiCall(final Activity activity, final String apiMethod, final HttpMethods httpMethod, final BaseClass object, final String[][] params, final Select cache, final Class<RT> callBack, final IRestApiListener apiListener) {

        if (activity != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final RT retObject = apiCall(apiMethod, httpMethod, object, cache, callBack, params);
                        if (apiListener != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    apiListener.complete(apiMethod, cache, retObject);
                                }
                            });

                        }
                    } catch (final Exception e) {
                        if (apiListener != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    apiListener.error(apiMethod, cache, e);
                                }
                            });
                        }
                    }
                }
            }).start();

        }
    }

    public <RT extends BaseClass> RT apiCall(String apiMethod, HttpMethods httpMethod, Object object, Select cache, Class<RT> callBack) throws Exception {
        return apiCall(apiMethod, httpMethod, object, cache, callBack, null);
    }

    public <RT extends BaseClass> RT apiCall(String apiMethod, HttpMethods httpMethod, Object object, Select cache, Class<RT> callBack, String[][] params) throws Exception {
        String jsonAnswer = this.apiCall(apiMethod, httpMethod, object, params);
        RT retObject = RT.fromJson(jsonAnswer, callBack, cache);
        return retObject;
    }

    public String apiCall(String apiMethod, HttpMethods httpMethod, Object object, String[][] params) throws Exception {
        //TODO: Watch
        if (object != null && object instanceof BaseClass) {
            ((BaseClass) object).__object_server_state = BaseClass.__OBJECT_STATE_SENDING;
        }
        return this.getServiceUrlString(getApiMethod(apiMethod), httpMethod, object, params);
    }

    private String getApiMethod(String apiMethod) {
        if (!apiMethod.startsWith("/")) {
            apiMethod = "/" + apiMethod;
        }
        return serviceBaseUrl + apiMethod;
    }

    public String getServiceUrlString(String url, HttpMethods httpMethod, Object object, String[][] params) throws Exception {
        return getStringFromUrl(url, httpMethod, this.login, this.password, object, params);
    }

    public static StringEntity getJsonEncoded(Object postObject) {
        StringEntity se = null;
        if (postObject != null) {
            try {
                String jsonString = postObject.toString();
                se = new StringEntity(jsonString, HTTP.UTF_8);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        "application/json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return se;
    }

    public static Integer getTimeOut() {
        return 15;
    }

    public static DefaultHttpClient client(String user, String password) {
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, getTimeOut() * 1000);
        HttpConnectionParams.setSoTimeout(my_httpParams, getTimeOut() * 1000);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        if (user != null && password != null) {
            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(
                            AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(user,
                            password)
            );
            httpclient.setCredentialsProvider(credProvider);
        }

        return httpclient;
    }

    public static ByteArrayOutputStream readResponse(HttpResponse response) throws Exception {
        ByteArrayOutputStream baos = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {

            baos = new ByteArrayOutputStream();
            DataInputStream dis = new DataInputStream(entity.getContent());
            byte[] buffer = new byte[1024];// In bytes
            int realyReaded;
            while ((realyReaded = dis.read(buffer)) > -1) {
                baos.write(buffer, 0, realyReaded);
            }

        }
        if (response.getStatusLine().getStatusCode() >= 300) {
            throw new Exception(baos.toString("UTF-8"));
        }
        return baos;
    }

    public static UrlEncodedFormEntity convertParams(String[][] params) {
        UrlEncodedFormEntity urlEncodedFormEntity = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null) {
            for (int k = 0; k < params.length; k++) {
                String key = params[k][0];
                String value = params[k][1];
                nameValuePairs.add(new BasicNameValuePair(key, value));
            }
        }


        return urlEncodedFormEntity;
    }

    public static String appendUrlParams(String url, String[][] params) {
//        URL urlBuilder = new URL(url);
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (int k = 0; k < params.length; k++) {
                String key = params[k][0];
                String value = params[k][1];
                builder = builder.appendQueryParameter(key, URLEncoder.encode(value));
            }
        }
        return builder.build().toString();
    }

    public static String getStringFromUrl(String url, HttpMethods httpMethod, String user, String password, Object object, String[][] params) throws Exception {
        String retString = null;
        DefaultHttpClient httpclient = RestApi.client(user, password);
        HttpResponse response = null;
        try {

            HttpUriRequest httpUriRequest = null;
            switch (httpMethod) {
                case POST:
                    httpUriRequest = new HttpPost(url);
                    if (object != null) {
                        ((HttpPost) httpUriRequest).setEntity(getJsonEncoded(object));
                    }
                    if (params != null) {
                        ((HttpPost) httpUriRequest).setEntity(convertParams(params));
                    }
                    break;
                case PUT:
                    httpUriRequest = new HttpPut(url);
                    if (object != null) {
                        ((HttpPut) httpUriRequest).setEntity(getJsonEncoded(object));
                    }
                    if (params != null) {
                        ((HttpPut) httpUriRequest).setEntity(convertParams(params));
                    }
                    break;
                case DELETE:
                    httpUriRequest = new HttpDelete(appendUrlParams(url, params));
                    break;
                case GET:
                    httpUriRequest = new HttpGet(appendUrlParams(url, params));
                    break;
            }


            response = httpclient.execute(httpUriRequest);
            retString = readResponse(response).toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retString;
    }

    public RestApi(String mServiceBaseUrl, String mLogin, String mPassword) {
        this.serviceBaseUrl = mServiceBaseUrl;
        this.login = mLogin;
        this.password = mPassword;
    }

    public <RT extends BaseClass> RT getObject(String apiPath, Class<RT> classObject, Select cache) throws Exception {
        return getObject(apiPath, classObject, cache, null);
    }

    public <RT extends BaseClass> RT getObject(String apiPath, Class<RT> classObject, Select cache, String[][] params) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.GET, null, params);
        retObject = RT.fromJson(jsonObject, classObject, cache);
        return retObject;
    }


    public <RT extends BaseClass> RT postObject(String apiPath, RT object, Select cache) throws Exception {
        return postObject(apiPath, object, cache, null);
    }

    public <RT extends BaseClass> RT postObject(String apiPath, RT object, Select cache, String[][] params) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.POST, object, params);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }


    public <RT extends BaseClass> RT putObject(String apiPath, RT object, Select cache) throws Exception {
        return putObject(apiPath, object, cache, null);
    }

    public <RT extends BaseClass> RT putObject(String apiPath, RT object, Select cache, String[][] params) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.PUT, object, params);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }

    public <RT extends BaseClass> RT deleteObject(String apiPath, RT object) throws Exception {
        return deleteObject(apiPath, object, null);
    }

    public <RT extends BaseClass> RT deleteObject(String apiPath, RT object, String[][] params) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.DELETE, object, params);
        object.delete();
        return retObject;
    }

    public static class ResultData<RT extends BaseClass> extends BaseClass {
        public Integer offset;
        public Integer limit;
        public Integer total;
        public String error;
        public ArrayList<RT> data;
    }

    public <RT extends BaseClass> ResultData<RT> getObjects(String apiPath, Class<RT> classObject, Select cache, Boolean justCache) throws Exception {
        return getObjects(apiPath, classObject, cache, justCache, null);
    }

    public <RT extends BaseClass> ResultData<RT> getObjects(String apiPath, Class<RT> classObject, Select cache, Boolean justCache, String[][] params) throws Exception {
        ResultData<RT> resultData = null;
        resultData = this.apiCall(apiPath, HttpMethods.GET, null, cache, ResultData.class, params);
        ArrayList<RT> tempObjects = new ArrayList<RT>();
        for (Object tempOneObject : resultData.data) {
            RT oneObj = RT.fromJsonTreeMap(tempOneObject, classObject, cache);
            if (justCache == false || cache == null) {
                tempObjects.add(oneObj);
            }
        }
        resultData.data = tempObjects;
        return resultData;
    }

    public <RT extends BaseClass> ResultData<RT> getObjects(String apiPath, Class<RT> classObject, Select cache, String[][] params) throws Exception {
        return getObjects(apiPath, classObject, cache, false, params);
    }

    public NDFile postFile(File file, String apiMethod, Select cache) throws Exception {
        String getFileString = this.postFile(file, apiMethod);
        NDFile retFile = NDFile.fromJson(getFileString, NDFile.class, cache);
        return retFile;
    }

    public String postFile(File file, String apiMethod) throws Exception {
        String retString = "";
        DefaultHttpClient client = client(this.login, this.password);
        String fullApiPath = getApiMethod(apiMethod);

        HttpPost httppost = new HttpPost(fullApiPath);
        httppost.addHeader("NDFILENAME", file.getName());
        FileEntity entity = new FileEntity(file, "binary/octet-stream");
        try {
            httppost.setEntity(entity);
            HttpResponse response = client.execute(httppost);
            response = client.execute(httppost);
            retString = readResponse(response).toString("UTF-8");
            System.out.println("RETSTRING:" + retString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        return retString;
    }

}
