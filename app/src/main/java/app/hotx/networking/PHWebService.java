package app.hotx.networking;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Grigory Azaryan on 10/18/18.
 */

public interface PHWebService {

    int ERR_CODE_PREMIUM_CONTENT = 7;

    @GET("login")
    Call<JsonObject> login(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("username") String username, @Query("password") String password);

    @GET("getVideos")
    Call<JsonObject> getVideos(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("limit") int limit, @Query("offset") int offset,
                               @Query("search") String search, @Query("c") String category, @Query("order") String order, @Query("filter") String filter,
                               @QueryMap Map<String, String> additionalFilters);

    @GET("getPornstar")
    Call<JsonObject> getPornstar(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("limit") int limit, @Query("offset") int offset,
                                 @Query("order") String order, @Query("slug") String slug);

    @GET("getRelatedVideos")
    Call<JsonObject> getRelatedVideos(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("vkey") String vkey,
                                      @Query("limit") int limit, @Query("offset") int offset);

    @GET("getVideo")
    Call<JsonObject> getVideo(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("vkey") String vkey);

    @GET("getUserVideos")
    Call<JsonObject> getUserVideos(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("userId") String userId,
                                   @Query("userKey") String userKey, @Query("limit") int limit, @Query("offset") int offset,
                                   @Query("targetUserId") String targetUserId, @Query("show") String showOption);

    @GET("isFavoriteVideo")
    Call<JsonObject> isFavoriteVideo(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("userId") String userId,
                                     @Query("userKey") String userKey, @Query("vkey") String vkey);

    @GET("removeFavoriteVideo")
    Call<JsonObject> removeFavoriteVideo(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("userId") String userId,
                                         @Query("userKey") String userKey, @Query("vkey") String vkey);

    @GET("addFavoriteVideo")
    Call<JsonObject> addFavoriteVideo(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("userId") String userId,
                                      @Query("userKey") String userKey, @Query("vkey") String vkey);

    @GET("categories")
    Call<JsonObject> getCategories(@Query("appKey") String appKey, @Query("uuid") String uuid);

    @GET("getPornstars")
    Call<JsonObject> getPornstars(@Query("appKey") String appKey, @Query("uuid") String uuid, @Query("limit") int limit, @Query("offset") int offset,
                                  @QueryMap Map<String, String> orderFilters, @QueryMap Map<String, String> additionalFilters,
                                  @Query("search") String search);

    @GET("searchAutocomplete")
    Call<JsonObject> searchAutocomplete(@Query("appKey") String appKey, @Query("uuid") String uuid,
                                        @Query("search") String search, @Query("source") String source); // source of {video, pornstar}

}
