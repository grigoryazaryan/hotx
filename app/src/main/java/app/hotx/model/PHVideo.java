package app.hotx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class PHVideo implements Serializable {
    int id;
    String vkey;
    String title;
    int duration;
    int rating;
    int viewCount;
    String urlThumbnail;
    boolean hd;
    @SerializedName("categories")
    String categoriesRaw;
    @SerializedName("tags")
    String tags;
    String production;
    String pornstars;
    String trackUrl;
    Map<String, String> encodings;
    long addedOn; // seconds since 1970
    long approvedOn; // seconds since 1970
    boolean vr;
    int vrProjectionType;
    boolean vrStereoSrc;
    int vrStereoType;
    String webm;
    boolean premium;
    boolean isPrivate;
    boolean canSeeVideo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVkey() {
        return vkey;
    }

    public void setVkey(String vkey) {
        this.vkey = vkey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public boolean isHd() {
        return hd;
    }

    public void setHd(boolean hd) {
        this.hd = hd;
    }

    public String getCategoriesRaw() {
        return categoriesRaw;
    }

    public void setCategoriesRaw(String categoriesRaw) {
        this.categoriesRaw = categoriesRaw;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getPornstars() {
        return pornstars;
    }

    public void setPornstars(String pornstars) {
        this.pornstars = pornstars;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public Map<String, String> getEncodings() {
        return encodings;
    }

    public void setEncodings(Map<String, String> encodings) {
        this.encodings = encodings;
    }

    public long getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(long addedOn) {
        this.addedOn = addedOn;
    }

    public long getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(long approvedOn) {
        this.approvedOn = approvedOn;
    }

    public boolean isVr() {
        return vr;
    }

    public void setVr(boolean vr) {
        this.vr = vr;
    }

    public int getVrProjectionType() {
        return vrProjectionType;
    }

    public void setVrProjectionType(int vrProjectionType) {
        this.vrProjectionType = vrProjectionType;
    }

    public boolean isVrStereoSrc() {
        return vrStereoSrc;
    }

    public void setVrStereoSrc(boolean vrStereoSrc) {
        this.vrStereoSrc = vrStereoSrc;
    }

    public int getVrStereoType() {
        return vrStereoType;
    }

    public void setVrStereoType(int vrStereoType) {
        this.vrStereoType = vrStereoType;
    }

    public String getWebm() {
        return webm;
    }

    public void setWebm(String webm) {
        this.webm = webm;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isCanSeeVideo() {
        return canSeeVideo;
    }

    public void setCanSeeVideo(boolean canSeeVideo) {
        this.canSeeVideo = canSeeVideo;
    }
}
