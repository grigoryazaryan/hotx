package app.hotx.networking

import app.hotx.model.Event
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Grigory Azaryan on 11/30/18.
 */


interface WebService {

    @GET(" ")
    fun getVersion(): Call<ServerResponse>

    @GET("category/all")
    fun getCategories(): Call<ServerResponse>

    @GET("studio/all")
    fun getStudios(): Call<ServerResponse>

    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("lang") lang: String, @Field("phoneModel") phoneModel: String, @Field("androidVersionCode") androidVersionCode: Int,
                 @Field("appVersionCode") appVersionCode: Int, @Field("deviceId") deviceId: String): Call<ServerResponse>

    @FormUrlEncoded
    @POST("user/auth")
    fun auth(@Field("lang") lang: String, @Field("phoneModel") phoneModel: String, @Field("androidVersionCode") androidVersionCode: Int,
                 @Field("appVersionCode") appVersionCode: Int, @Field("deviceId") deviceId: String): Call<ServerResponse>

    @FormUrlEncoded
    @POST("video/all")
    fun getAllVideos(@Field("search") search: String?, @Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @GET("video/{videoId}")
    fun getVideo(@Path("videoId") videoId: String): Call<ServerResponse>

    @FormUrlEncoded
    @POST("video/recommended")
    fun getRecommendedVideos(@Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @FormUrlEncoded
    @POST("video/{videoId}/related")
    fun getRelatedVideos(@Path("videoId") videoId: String, @Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @GET("video/{videoId}/{action}")
    fun setLike(@Path("videoId") videoId: String, @Path("action") action: String): Call<ServerResponse>  // actions: like, dislike

    @GET("video/{videoId}/setPhFavorite")
    fun setFavorite(@Path("videoId") videoId: String): Call<ServerResponse>

    @GET("video/{videoId}/isPhFavorite")
    fun isFavorite(@Path("videoId") videoId: String): Call<ServerResponse>

    @FormUrlEncoded
    @POST("video/getPhFavorites")
    fun getFavorites(@Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @FormUrlEncoded
    @POST("favorite")
    fun getUserFavorites(@Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @FormUrlEncoded
    @POST("user/history")
    fun getUserHistory(@Field("page") page: Int, @Field("limit") limit: Int): Call<ServerResponse>

    @FormUrlEncoded
    @POST("to-parse/video")
    fun uploadVideo(@Field("link") link: String): Call<ServerResponse>

    @POST("fireEvent")
    fun fireEvent(@Body event: Event): Call<ServerResponse>

}
