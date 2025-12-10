package net.integraa.read.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReadData {
    private String code = "-1";
    private String msg = "UNKNOWN";
    private String token = null;

    @SerializedName("items")
    private List<ReadItem> items;

    public List<ReadItem> getItems() {
        return items;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public String getToken() {
        return token;
    }
}
