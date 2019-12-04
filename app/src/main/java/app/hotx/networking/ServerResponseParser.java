package app.hotx.networking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import app.hotx.model.Category;
import app.hotx.model.LikeDislikeResponse;
import app.hotx.model.RegisterResponse;
import app.hotx.model.VideoObject;
import app.hotx.model.VideosListResponse;

public class ServerResponseParser {

    Gson gson;

    @Inject
    public ServerResponseParser(Gson gson) {
        this.gson = gson;
    }

    public RegisterResponse parseRegisterResponse(ServerResponse response) {
        return gson.fromJson(response.getResult(), RegisterResponse.class);
    }

    public VideosListResponse parseVideosList(ServerResponse response) {
        return gson.fromJson(response.getResult(), VideosListResponse.class);
    }

    public VideoObject parseVideo(ServerResponse response) {
        return gson.fromJson(response.getResult(), VideoObject.class);
    }

    public LikeDislikeResponse parseLikeDislike(ServerResponse response) {
        return gson.fromJson(response.getResult(), LikeDislikeResponse.class);
    }

    public List<Category> parseCategories(ServerResponse response) {
        List<Category> categories = gson.fromJson(response.getResult(), new TypeToken<List<Category>>() {
        }.getType());
        return categories;
    }
}
