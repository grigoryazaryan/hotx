package app.hotx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Grigory Azaryan on 10/18/18.
 */
@Entity
public class PHSmallVideo implements Serializable {
    @Id
    @SerializedName("_idd")
    public long id;
    private String vkey;
    private String title;
    private int duration;
    private int rating;
    private int viewCount;
    private String urlThumbnail;
    private boolean hd;
    private boolean premium;
    private long approvedOn;
    private String webm;
    private boolean isPrivate;
    private boolean vr;
    private boolean canSeeVideo;

    public PHSmallVideo() {
    }

    public PHSmallVideo(PHVideo phVideo) {
        this.vkey = phVideo.getVkey();
        this.title = phVideo.getTitle();
        this.duration = phVideo.getDuration();
        this.rating = phVideo.getRating();
        this.viewCount = phVideo.getViewCount();
        this.urlThumbnail = phVideo.getUrlThumbnail();
        this.hd = phVideo.isHd();
        this.approvedOn = phVideo.getApprovedOn();
        this.webm = phVideo.getWebm();
        this.premium = phVideo.isPremium();
        this.isPrivate = phVideo.isPrivate();
        this.vr = phVideo.isVr();
        this.canSeeVideo = phVideo.isCanSeeVideo();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public long getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(long approvedOn) {
        this.approvedOn = approvedOn;
    }

    public String getWebm() {
        return webm;
    }

    public void setWebm(String webm) {
        this.webm = webm;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isVr() {
        return vr;
    }

    public void setVr(boolean vr) {
        this.vr = vr;
    }

    public boolean isCanSeeVideo() {
        return canSeeVideo;
    }

    public void setCanSeeVideo(boolean canSeeVideo) {
        this.canSeeVideo = canSeeVideo;
    }
}
