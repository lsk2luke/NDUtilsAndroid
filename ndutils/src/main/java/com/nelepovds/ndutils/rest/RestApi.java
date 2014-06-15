package com.nelepovds.ndutils.rest;

import com.activeandroid.query.Select;
import com.google.gson.Gson;

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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.String;
import java.lang.StringBuilder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;


public class RestApi {
    public static enum HttpMethods {
        GET, POST, DELETE, PUT
    }

    public String serviceBaseUrl;

    public String login;
    public String password;

    public static class RestApiRouteInfo {
        public String className;
        public String path;
        public String idName;
        public HttpMethods[] methods;

        /**
         * @param className
         * @param path
         * @param idName
         * @param methods
         */
        public RestApiRouteInfo(Class className, String path, String idName, HttpMethods[] methods) {
            this.className = className.getName();
            this.path = path;
            this.idName = idName;
            this.methods = methods;
        }
    }

    public ArrayList<RestApiRouteInfo> routes = new ArrayList<RestApiRouteInfo>();

    public String getEndPointAPIMethodPath(String apiPath) {
        if (!apiPath.startsWith("/")) {
            apiPath = "/" + apiPath;
        }
        return serviceBaseUrl + apiPath;
    }

    public String apiCall(String apiMethod, HttpMethods httpMethod, BaseClass object) throws IOException {
        return this.getServiceUrlString(getApiMethod(apiMethod), httpMethod, object.toString());
    }

    public String getApiMethod(String apiMethod) {
        if (!apiMethod.startsWith("/")) {
            apiMethod = "/" + apiMethod;
        }
        return serviceBaseUrl + apiMethod;
    }

    public String getServiceUrlString(String url, HttpMethods httpMethod, Object object) {
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

    public static String getStringFromUrl(String url, HttpMethods httpMethod, String user, String password, Object object) {
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


            HttpUriRequest httpUriRequest = new HttpGet();
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
                    if (object != null) {
                        httpUriRequest = new HttpPost(url);
                        ((HttpPost) httpUriRequest).setEntity(getJsonEncoded(object));
                    }
                    break;
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

    public RestApi(String mServiceBaseUrl, String mLogin, String mPassword) {
        this.serviceBaseUrl = mServiceBaseUrl;
        this.login = mLogin;
        this.password = mPassword;
        this.routes = fillUpRoutes();
    }

    public ArrayList<RestApiRouteInfo> fillUpRoutes() {
        return new ArrayList<RestApiRouteInfo>();
    }


    /**
     * object.php?objclass=UserClass&id=2
     *
     * @return
     */
    protected String getEndpointObjectId(Long id, Class classObject, HttpMethods httpMethod) throws Exception {
        StringBuilder retEndPoint = new StringBuilder(this.serviceBaseUrl);
        //Search route
        RestApiRouteInfo classRouteInfo = null;
        for (RestApiRouteInfo routeInfo : this.routes) {
            if (routeInfo.className.equalsIgnoreCase(classObject.getName()) && Arrays.binarySearch(routeInfo.methods, httpMethod) > -1) {
                classRouteInfo = routeInfo;
                break;
            }
        }
        if (classRouteInfo == null) {
            throw new Exception("No route found for class " + classObject.getName() + " for method " + httpMethod.name());
        }
        retEndPoint.append("/");
        if (id != null) {
            retEndPoint.append(classRouteInfo.path.replaceFirst(":" + classRouteInfo.idName, id.toString()));
        } else {
            retEndPoint.append(classRouteInfo.path);
        }

        return retEndPoint.toString();
    }

    private void checkError(String jsonObject) throws Exception {
        String errorMessage = null;
        try {
            ResultData isError = new Gson().fromJson(jsonObject, ResultData.class);
            if (isError != null && isError.error != null) {
                errorMessage = isError.error;
            }
        } catch (Exception jsonEx) {
            errorMessage = "Undefined error";
        }
        if (errorMessage != null) {
            throw new Exception(errorMessage);
        }

    }

    public <RT extends BaseClass> RT getObject(Long id, Class<RT> classObject, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(id, classObject, HttpMethods.GET), HttpMethods.GET, null);
        this.checkError(jsonObject);
        retObject = RT.fromJson(jsonObject, classObject, cache);
        return retObject;
    }


    public <RT extends BaseClass> RT postObject(RT object, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(null, object.getClass(), HttpMethods.POST), HttpMethods.POST, object);
        this.checkError(jsonObject);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }


    public <RT extends BaseClass> RT putObject(RT object, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(object.serverId, object.getClass(), HttpMethods.PUT), HttpMethods.PUT, object);
        this.checkError(jsonObject);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }

    public <RT extends BaseClass> RT deleteObject(RT object, Select cache) throws Exception {
        RT retObject = null;
        String jsonObject = this.getServiceUrlString(this.getEndpointObjectId(object.serverId, object.getClass(), HttpMethods.DELETE), HttpMethods.DELETE, null);
        this.checkError(jsonObject);
        retObject = (RT) RT.fromJson(jsonObject, object.getClass(), cache);
        return retObject;
    }

    public static class ResultData<T> {
        public Integer offset;
        public Integer limit;
        public Integer total;
        public ArrayList<T> data;
        public String error;

        public static <T extends BaseClass> ResultData<T> fromJson(String jsonObject, Class<T> classObject, Select cache) {
            ResultData<T> retData = new ResultData();
            ArrayList<T> tempData = new ArrayList<T>();

            retData = new Gson().fromJson(jsonObject, ResultData.class);
            for (Object obj : retData.data) {
                String jsonStrObj = new Gson().toJson(obj);
                tempData.add(BaseClass.fromJson(jsonStrObj, classObject, cache));
            }

            retData.data = tempData;
            return retData;
        }

    }

    protected String getEndpointList(Class<? extends BaseClass> classObject, NDRestFilter restFilter) {
        StringBuilder retEndPoint = new StringBuilder(this.serviceBaseUrl);
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

    public <T extends Object> ResultData listObjects(Class<T> classObject, String apiPath) {
        ResultData<T> retData = new ResultData();
        String jsonObject = this.getServiceUrlString(getEndPointAPIMethodPath(apiPath), HttpMethods.GET, null);
        ArrayList<T> tempData = new ArrayList<T>();

        retData = new Gson().fromJson(jsonObject, ResultData.class);
        for (Object obj : retData.data) {
            String jsonStrObj = new Gson().toJson(obj);
            T objT = new Gson().fromJson(jsonStrObj, classObject);
            tempData.add(objT);
        }
        retData.data = tempData;

        return retData;
    }

    public <T extends BaseClass> ResultData listObjects(Class<T> classObject, NDRestFilter restFilter, Select cache) {
        ResultData<T> retData = new ResultData();
        String jsonObject = this.getServiceUrlString(this.getEndpointList(classObject, restFilter), HttpMethods.GET, null);
        ArrayList<T> tempData = new ArrayList<T>();

        retData = new Gson().fromJson(jsonObject, ResultData.class);
        for (Object obj : retData.data) {
            String jsonStrObj = new Gson().toJson(obj);
            tempData.add(BaseClass.fromJson(jsonStrObj, classObject, cache));
        }

        retData.data = tempData;
        return retData;
    }

}
