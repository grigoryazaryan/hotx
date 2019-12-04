package app.hotx.networking;

import com.google.gson.JsonElement;

public class ServerResponse {

    boolean status;
    String message;
    int code;
    JsonElement result;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JsonElement getResult() {
        return result;
    }

    public void setResult(JsonElement result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "status:" + status + ",\n" +
                "code:" + code + ",\n" +
                "result:" + result + ",\n" +
                "message:" + message + ",\n";
    }
}
