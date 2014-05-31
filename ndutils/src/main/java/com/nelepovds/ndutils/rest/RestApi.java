package com.nelepovds.ndutils.rest;

import com.activeandroid.query.Select;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Class;import java.lang.Exception;import java.lang.Integer;import java.lang.Object;import java.lang.String;import java.lang.StringBuilder;
import java.net.URLEncoder;
import java.util.ArrayList;


public class RestApi {
    public static enum HttpMethods{
        GET,POST,DELETE,PUT
    }

    public String serviceBaseUrl;

    public String login;
    public String password;

    public String getEndPointAPIMethodPath(String apiPath){
        if (!apiPath.startsWith("/")){
            apiPath="/"+apiPath;
        }
        return serviceBaseUrl+apiPath;
    }

    public  String getServiceUrlString(String url, HttpMethods httpMethod, Object object) {
        return getStringFromUrl(url, httpMethod.name(), this.login, this.password, object);
    }

    public static StringEntity getJsonEncoded(Object postObject){
        StringEntity se=null;
        if (postObject!=null){

            try {

                String jsonString = postObject.toString();
                se = new StringEntity(jsonString, HTTP.UTF_8);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        "application/json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  se;
    }

    public static  String getStringFromUrl(String url, String httpMethod,String user, String password, Object object) {
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


            HttpUriRequest httpUriRequest=new HttpPut();
            if (httpMethod.equalsIgnoreCase("get")) {
                httpUriRequest = new HttpPost(url);
                if (object!=null) {
                    ((HttpPost) httpUriRequest).setEntity(getJsonEncoded(object));
                }
            } else  if (httpMethod.equalsIgnoreCase("post")) {
                httpUriRequest = new HttpPost(url);
                if (object!=null) {
                    ((HttpPost) httpUriRequest).setEntity(getJsonEncoded(object));
                }
            } else  if (httpMethod.equalsIgnoreCase("delete")) {
                httpUriRequest = new HttpDelete(url);
            } else  if (httpMethod.equalsIgnoreCase("put")) {
                httpUriRequest = new HttpPut(url);
                if (object!=null) {
                    ((HttpPut) httpUriRequest).setEntity(getJsonEncoded(object));
                }
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

    public RestApi(String mServiceBaseUrl, String mLogin, String mPassword){
        this.serviceBaseUrl = mServiceBaseUrl;
        this.login = mLogin;
        this.password=mPassword;
    }


    /**
     * object.php?objclass=UserClass&id=2
     * @return
     */
    protected String getEndpointObjectId(Integer id,Class classObject){
        StringBuilder retEndPoint=new StringBuilder(this.serviceBaseUrl);
        retEndPoint.append("/object.php?objclass=");
        RestApiClassName restApiClassName = (RestApiClassName) classObject.getAnnotation(RestApiClassName.class);
        retEndPoint.append(restApiClassName.value());
        if (id!=null) {
            retEndPoint.append("&id=");
            retEndPoint.append(id.toString());
        }
        return retEndPoint.toString();
    }

    public <RT extends BaseClass> RT getObject(Integer id,Class<RT> classObject, Select cache){
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(id, classObject), HttpMethods.GET, null);
        retObject = RT.fromJson(jsonObject,classObject,cache);
        return  retObject;
    }



    public <RT extends BaseClass> RT postObject(RT object,Select cache){
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(null, object.getClass()), HttpMethods.POST, object);
        retObject = (RT) RT.fromJson(jsonObject,object.getClass(),cache);
        return  retObject;
    }

    public <RT extends BaseClass> RT putObject(RT object,Select cache){
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(object.serverId, object.getClass()), HttpMethods.PUT, object);
        retObject = (RT) RT.fromJson(jsonObject,object.getClass(),cache);
        return  retObject;
    }

    public <RT extends BaseClass> RT deleteObject(RT object, Select cache){
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(object.serverId, object.getClass()), HttpMethods.DELETE, null);
        retObject = (RT) RT.fromJson(jsonObject,object.getClass(),cache);
        return  retObject;
    }

    public static class ResultData <T> {
        public Integer offset;
        public Integer limit;
        public Integer total;
        public ArrayList<T> data;
    }

    protected String getEndpointList(Class<? extends BaseClass> classObject,NDRestFilter restFilter){
        StringBuilder retEndPoint=new StringBuilder(this.serviceBaseUrl);
        retEndPoint.append("/list.php?objclass=");
        RestApiClassName restApiClassName = (RestApiClassName) classObject.getAnnotation(RestApiClassName.class);
        retEndPoint.append(restApiClassName.value());
        //Filter
        if (restFilter != null) {
            retEndPoint.append("&Filter=");
            String filterString = new Gson().toJson(restFilter);
            retEndPoint.append(URLEncoder.encode(filterString));
        }

        return retEndPoint.toString();
    }

    public <T extends Object> ResultData listObjects(Class<T> classObject,String apiPath){
        ResultData<T> retData= new ResultData();
        String jsonObject = this.getServiceUrlString(getEndPointAPIMethodPath(apiPath),HttpMethods.GET,null);
        ArrayList<T> tempData =new ArrayList<T>();

        retData = new Gson().fromJson(jsonObject,ResultData.class);
        for (Object obj : retData.data){
            String jsonStrObj = new Gson().toJson(obj);
            T objT = new Gson().fromJson(jsonStrObj,classObject);
            tempData.add(objT);
        }
        retData.data = tempData;

        return retData;
    }

    public <T extends BaseClass> ResultData listObjects(Class<T> classObject,NDRestFilter restFilter, Select cache){
        ResultData<T> retData= new ResultData();
        String jsonObject = this.getServiceUrlString(this.getEndpointList(classObject,restFilter),HttpMethods.GET,null);
        ArrayList<T> tempData =new ArrayList<T>();

        retData = new Gson().fromJson(jsonObject,ResultData.class);
        for (Object obj:retData.data){
            String jsonStrObj = new Gson().toJson(obj);
            tempData.add(BaseClass.fromJson(jsonStrObj,classObject,cache));
        }

        retData.data = tempData;
        return  retData;
    }

}
