package com.nelepovds.ndutils.rest;

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
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
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


    public <RT extends BaseClass> RT apiCall(String apiMethod, HttpMethods httpMethod, BaseClass object, Select cache, Class<RT> callBack) throws Exception {
        String jsonAnswer = this.apiCall(apiMethod, httpMethod, object);
        RT retObject = RT.fromJson(jsonAnswer, callBack, cache);
        return retObject;
    }

    public String apiCall(String apiMethod, HttpMethods httpMethod, BaseClass object) throws Exception {
        return this.getServiceUrlString(getApiMethod(apiMethod), httpMethod, object);
    }

    private String getApiMethod(String apiMethod) {
        if (!apiMethod.startsWith("/")) {
            apiMethod = "/" + apiMethod;
        }
        return serviceBaseUrl + apiMethod;
    }

    public String getServiceUrlString(String url, HttpMethods httpMethod, Object object) throws Exception {
        return getStringFromUrl(url, httpMethod, this.login, this.password, object);
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

    public static DefaultHttpClient client(String user, String password) {
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

    public static String getStringFromUrl(String url, HttpMethods httpMethod, String user, String password, Object object) throws Exception {
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
                    break;
                case PUT:
                    httpUriRequest = new HttpPut(url);
                    if (object != null) {
                        ((HttpPut) httpUriRequest).setEntity(getJsonEncoded(object));
                    }
                    break;
                case DELETE:
                    httpUriRequest = new HttpDelete(url);
                    break;
                case GET:
                    httpUriRequest = new HttpGet(url);
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
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.GET, null);
        retObject = RT.fromJson(jsonObject, classObject, cache);
        return retObject;
    }


    public <RT extends BaseClass> RT postObject(String apiPath, RT object, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.POST, object);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }


    public <RT extends BaseClass> RT putObject(String apiPath, RT object, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.PUT, object);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }

    public <RT extends BaseClass> RT deleteObject(String apiPath, RT object) throws Exception {
        RT retObject = null;
        String jsonObject = this.apiCall(apiPath, HttpMethods.DELETE, object);
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
        ResultData<RT> resultData = null;
        resultData = this.apiCall(apiPath, HttpMethods.GET, null, cache, ResultData.class);
        ArrayList<RT> tempObjects = new ArrayList<RT>();
        for (Object tempOneObject : resultData.data) {
            RT oneObj = RT.fromJsonTreeMap(tempOneObject, classObject, cache);
            if (justCache == false) {
                tempObjects.add(oneObj);
            }
        }
        resultData.data = tempObjects;
        return resultData;
    }

    public <RT extends BaseClass> ResultData<RT> getObjects(String apiPath, Class<RT> classObject, Select cache) throws Exception {
        return getObjects(apiPath, classObject, cache, false);
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
