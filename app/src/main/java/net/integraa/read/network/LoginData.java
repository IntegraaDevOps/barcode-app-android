package net.integraa.read.network;

public class LoginData {
    private String code = "-1";
    private String msg = "UNKNOWN";
    private String token ="";
    private String barcode_scanner_zoom ="1.0";
    private String barcode_scanner_key ="";
    private String barcode_scanner_type ="";

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public String getToken() {
        return token;
    }

    public String getBarcode_scanner_zoom() {
        return barcode_scanner_zoom;
    }

    public String getBarcode_scanner_key() {
        return barcode_scanner_key;
    }

    public String getBarcode_scanner_type() {
        return barcode_scanner_type;
    }
}
