package app.hotx.networking;

import com.google.gson.JsonObject;

/**
 * Created by Grigory Azaryan on 10/18/18.
 */

public class PHResponse {
    private boolean success;
    private JsonObject data;
    private int code;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
