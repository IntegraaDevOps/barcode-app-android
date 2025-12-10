package net.integraa.read.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import net.integraa.read.BuildConfig;
import net.integraa.read.controller.Scanner;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.Proxy;

public class ConfigNet {
    private static final String defaultApiHost = "play.integraa.net";
    private static final String defaultApiProtocol = "https";
    private static final String defaultApiPath = "gestione/api/";
    private static final String defaultWebHost = "play.integraa.net";
    private static final String defaultWebProtocol = "https";
    private static final String defaultWebPath = "gestione/";

    private static String apiHost = null;
    private static String apiProtocol = null;
    private static String apiPath = null;
    private static String apiUrl = null;
    private static String webHost = null;
    private static String webProtocol = null;
    private static String webPath = null;
    private static String webUrl = null;
    private static File configFile = null;
    private static final String configName = ".config.net.integraa";
    private static long configTime = -1;
    protected static final String integraa_content_dir = ".content_integraa";

    public static String getToken() {
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        return preferences.getString("TOKEN","");
    }
    public static String getBarcodeScannerKey() {
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        return preferences.getString("BarcodeScannerKey","");
    }
    public static String getBarcodeScannerType() {
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        return preferences.getString("BarcodeScannerType","");
    }
    public static float getBarcodeScannerZoom() {
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        try {
            Float.parseFloat(preferences.getString("BarcodeScannerZoom","1.0"));
        }
        catch (Exception e) {}
        return 1.0f;
    }
    public static void setToken(String value) {
        if (value==null) {
            return;
        }
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        preferences.edit().putString("TOKEN",value).commit();
    }
    public static void setBarcodeScannerKey(String value) {
        if (value==null) {
            return;
        }
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        preferences.edit().putString("BarcodeScannerKey",value).commit();
    }
    public static void setBarcodeScannerType(String value) {
        if (value==null) {
            return;
        }
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        preferences.edit().putString("BarcodeScannerType",value).commit();
    }
    public static void setBarcodeScannerZoom(String value) {
        if (value==null) {
            return;
        }
        SharedPreferences preferences = Scanner.getApplication().getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        preferences.edit().putString("BarcodeScannerZoom",value).commit();
    }
    public static String requestsPath(String path) {
        return Environment.getExternalStorageDirectory()+File.separator+integraa_content_dir+ File.separator+path;
    }

    private static void setDefaults() {
        webProtocol=defaultWebProtocol;
        apiProtocol=defaultApiProtocol;
        webHost=defaultWebHost;
        apiHost=defaultApiHost;
        webPath=defaultWebPath;
        apiPath=defaultApiPath;
        apiUrl=null;
        webUrl=null;
    }

    private static void checkConfig() {
        if (configFile==null) {
            setDefaults();
            configFile = new File(requestsPath(configName));
        }
        if (!configFile.exists()) {
            setDefaults();
            configTime=-1;
        }
        if (configFile.isFile()&&configFile.exists()&&configFile.lastModified()!=configTime) {
            setDefaults();
            try {
                configTime=configFile.lastModified();
                JSONObject obj = new JSONObject(IOUtils.toString(new FileInputStream(configFile),"UTF8"));

                if(obj.has(BuildConfig.APPLICATION_ID)){
                    obj=obj.getJSONObject(BuildConfig.APPLICATION_ID);
                }
                if(obj.has("skipConfig")) {
                    return;
                }

                if(obj.has("requestProtocol")) {
                    webProtocol=apiProtocol=obj.getString("requestProtocol");
                }
                if(obj.has("requestHost")) {
                    webHost=apiHost=obj.getString("requestHost");
                }
                if(obj.has("requestPath")) {
                    webPath=apiPath=obj.getString("requestPath");
                }
                if(obj.has("apiProtocol")) {
                    apiProtocol=obj.getString("apiProtocol");
                }
                if(obj.has("apiHost")) {
                    apiHost=obj.getString("apiHost");
                }
                if(obj.has("apiPath")) {
                    apiPath=obj.getString("apiPath");
                }
                if(obj.has("webProtocol")) {
                    webProtocol=obj.getString("webProtocol");
                }
                if(obj.has("webHost")) {
                    webHost=obj.getString("webHost");
                }
                if(obj.has("webPath")) {
                    webPath=obj.getString("webPath");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getWebUrl(String path) {
        checkConfig();
        if (webUrl==null) {
            webUrl = webProtocol + "://" + webHost + "/" + webPath;
        }
        return webUrl;
    }
    public static String getApiUrl(String path) {
        checkConfig();
        if (apiUrl==null) {
            apiUrl = apiProtocol+"://"+apiHost+"/"+apiPath;
        }
        return apiUrl;
    }

    public static Proxy getProxy() {
        return null;
    }
}
