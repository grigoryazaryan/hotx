package app.hotx.networking;

import com.google.gson.JsonObject;

import javax.inject.Inject;

import app.hotx.app.App;
import app.hotx.helper.AppHelper;
import app.hotx.helper.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PHWebServiceCallback implements Callback<JsonObject> {

    private OnResponseListener responseListener;
    @Inject
    AppHelper appHelper;
    @Inject
    PHWebService phWebService;
    @Inject
    Utils utils;

    public PHWebServiceCallback(OnResponseListener responseListener) {
        App.getAppComponent().inject(this);
        this.responseListener = responseListener;
    }


    @Override
    public void onFailure(Call<JsonObject> call, Throwable t) {
        PHResponse response = new PHResponse();
        response.setSuccess(false);
        response.setMessage(t.getMessage());
        responseListener.onResponse(response);
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> retrofitResponse) {
        if (retrofitResponse.isSuccessful() && retrofitResponse.body() != null) {
            PHResponse response = new PHResponse();
            if (retrofitResponse.body().has("error")) {
                // {"error":{"code":7,"message":"USER IS NOT ALLOWED TO VIEW THIS VIDEO"}}
                // {"error":{"code":49,"message":"INVALID USERKEY."}}
                // {"error":{"code":14,"message":"THE USER MUST BE LOGGED TO PERFORM THIS ACTION"}}
                // {"error":{"code":48,"message":"YOU HAVE ALREADY HAVE THE MAXIMUM NUMBER OF FAVORITES ALLOWED."}} // more than 1000 favorite video
                // {"error":{"code":5,"message":"VIDEO NOT FOUND"}}
                JsonObject error = retrofitResponse.body().get("error").getAsJsonObject();
                response.setSuccess(false);
                response.setCode(error.get("code").getAsInt());
                response.setMessage(error.get("message").getAsString());
            } else {
                response.setSuccess(true);
                response.setData(retrofitResponse.body());
            }
            responseListener.onResponse(response);
        } else {
            PHResponse response = new PHResponse();
            response.setSuccess(false);
            response.setCode(retrofitResponse.code());
            response.setMessage(retrofitResponse.message());
            responseListener.onResponse(response);
        }
    }

    public interface OnResponseListener {

        void onResponse(PHResponse response);
    }
}
