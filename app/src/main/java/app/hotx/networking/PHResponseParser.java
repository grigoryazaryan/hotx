package app.hotx.networking;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import app.hotx.model.AdditionalFilterEntry;
import app.hotx.model.AutocompletePornstarEntry;
import app.hotx.model.Category;
import app.hotx.model.OrderFilterEntry;
import app.hotx.model.PHSmallVideo;
import app.hotx.model.PHVideo;
import app.hotx.model.Pornstar;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import solid.collectors.ToList;
import solid.stream.Stream;

public class PHResponseParser {

    Gson gson;

    @Inject
    public PHResponseParser(Gson gson) {
        this.gson = gson;
    }


    public List<PHSmallVideo> parseVideos(JsonObject response) {
        return gson.fromJson(response.get("videos"), new TypeToken<List<PHSmallVideo>>() {
        }.getType());
    }

    public List<PHSmallVideo> parseRelatedVideos(JsonObject response) {
        return gson.fromJson(response.get("relatedVideos"), new TypeToken<List<PHSmallVideo>>() {
        }.getType());
    }

    public PHVideo parseVideo(JsonObject response) {
        return gson.fromJson(response.get("video"), PHVideo.class);
    }

    public Pornstar parsePornstar(JsonObject response) {
        return gson.fromJson(response.get("pornstar"), Pornstar.class);
    }

    public List<Pornstar> parsePornstars(JsonObject response) {
        return gson.fromJson(response.get("items"), new TypeToken<List<Pornstar>>() {
        }.getType());
    }

    public List<OrderFilterEntry> parseOrder(JsonObject response) {

//        Map<String, Map<String, JsonElement>> entries = gson.fromJson(response.get("order"), new TypeToken<Map<String, Map<String, JsonElement>>>() {
//        }.getType());
        List<OrderFilterEntry> entryList = new ArrayList<>();
        JsonObject jsonMap = response.get("order").getAsJsonObject();
        for (Map.Entry<String, JsonElement> j : jsonMap.entrySet()) {
            OrderFilterEntry entry = new OrderFilterEntry();
            JsonObject jInner = j.getValue().getAsJsonObject();
            entry.setOrderKey(j.getKey());
            entry.setOrderTitle(jInner.get("title").getAsString());
            if (jInner.has("filter")) {
                entry.setFilterKey("filter");
                Map<String, String> map = gson.fromJson(jInner.get("filter"), new TypeToken<Map<String, String>>() {
                }.getType());
                List<Pair<String, String>> list = new ArrayList<>();
                CollectionsKt.mapTo(map.entrySet(), list, t -> new Pair<>(t.getKey(), t.getValue()));
                entry.setFilterValues(list);
            }
            if (jInner.has("letter")) {
                entry.setFilterKey("letter");
                List<String> list = gson.fromJson(jInner.get("letter"), new TypeToken<List<String>>() {
                }.getType());
                list.remove(0);
                entry.setFilterValues(Stream.stream(list).map(t -> new Pair<>(t, t)).collect(ToList.toList()));
            }
            entryList.add(entry);
        }
        return entryList;
    }

    public Map<String, AdditionalFilterEntry> parsePornstarsAdditionalFilters(JsonObject response) {
        Map<String, AdditionalFilterEntry> entryMap = new HashMap<>();
        JsonElement jsonMap = response.get("additionalFilters");
        for (Map.Entry<String, JsonElement> entry : jsonMap.getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                AdditionalFilterEntry filterEntry = gson.fromJson(entry.getValue(), AdditionalFilterEntry.class);
                if (!filterEntry.getValues().get(0).equalsIgnoreCase("All"))
                    filterEntry.getValues().add(0, "All");
                entryMap.put(entry.getKey(), filterEntry);
            }
        }
        return entryMap;
    }

    public List<Category> parseCategories(JsonObject response) {
        return gson.fromJson(response.get("all_categories"), new TypeToken<List<Category>>() {
        }.getType());
    }

    public List<AutocompletePornstarEntry> parseAutocompletePornstars(JsonObject response) {
        return gson.fromJson(response.get("results").getAsJsonObject().get("pornstar_suggestions"), new TypeToken<List<AutocompletePornstarEntry>>() {
        }.getType());
    }

    public List<String> parseAutocompleteSearch(JsonObject response) {
        return gson.fromJson(response.get("results").getAsJsonObject().get("video_queries"), new TypeToken<List<String>>() {
        }.getType());
    }
}
