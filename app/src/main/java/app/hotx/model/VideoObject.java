package app.hotx.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class VideoObject implements Serializable {

    String id;
    String name;
    String previewLink;
    String link;
    Date created;
    RegexQuality[] regExp;
    int views;
    int likes;
    int duration;
    String studio;
    String[] categories;
    String[] tags;
    String[] stars;
    String[] thumbs;
    boolean favorite;
    int likeState;
    Map<String, String> parsedLinks;
    String viewKey;

    public VideoObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public RegexQuality[] getRegExp() {
        return regExp;
    }

    public void setRegExp(RegexQuality[] regExp) {
        this.regExp = regExp;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getPornstars() {
        return stars;
    }

    public void setPornstars(String[] pornstars) {
        this.stars = pornstars;
    }

    public String[] getThumbs() {
        return thumbs;
    }

    public void setThumbs(String[] thumbs) {
        this.thumbs = thumbs;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getLikeState() {
        return likeState;
    }

    public void setLikeState(int likeState) {
        this.likeState = likeState;
    }

    public Map<String, String> getParsedLinks() {
        return parsedLinks;
    }

    public void setParsedLinks(Map<String, String> parsedLinks) {
        this.parsedLinks = parsedLinks;
    }

    public String getViewKey() {
        return viewKey;
    }

    public void setViewKey(String viewKey) {
        this.viewKey = viewKey;
    }
}
