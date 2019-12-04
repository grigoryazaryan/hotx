package app.hotx.networking

import app.hotx.app.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Grigory Azaryan on 11/30/18.
 */

abstract class WebServiceCallback : Callback<ServerResponse> {

    override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
        val response = ServerResponse()
        response.setStatus(false)
        response.setMessage(t.message)
        onResponse(response)
    }

    override fun onResponse(call: Call<ServerResponse>, retrofitResponse: Response<ServerResponse>) {
        if (retrofitResponse.isSuccessful && retrofitResponse.body() != null) {
            if (retrofitResponse.body()!!.code == 401) {
                App.getAppComponent().appHelper.performLogout()
            }
            onResponse(retrofitResponse.body()!!)
        } else {
            val response = ServerResponse()
            response.setStatus(false)
            response.setCode(retrofitResponse.code())
            response.setMessage(retrofitResponse.message())
            onResponse(response)
        }
    }

    abstract fun onResponse(response: ServerResponse)
}