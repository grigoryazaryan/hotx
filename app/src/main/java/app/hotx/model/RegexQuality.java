package app.hotx.model;

import java.io.Serializable;

public class RegexQuality implements Serializable {
    String quality;
    String regExp;

    public RegexQuality(String quality, String regex) {
        this.quality = quality;
        this.regExp = regex;
    }

    public String getRegExp() {
        return regExp;
    }

    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
