package app.hotx.helper;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.hotx.model.RegexQuality;
import app.hotx.model.VideoObject;

public class BodyParser {

    private AppCompatActivity activity;
    private BodyParserListener bodyParserListener;
    private VideoObject videoObject;

    public BodyParser(AppCompatActivity activity, BodyParserListener bodyParserListener, VideoObject videoObject) {
        this.activity = activity;
        this.bodyParserListener = bodyParserListener;
        this.videoObject = videoObject;
    }

    public void parseLink(String link) {
        HtmlViewer.getInstance(activity)
                .loadUrl(link, new HtmlViewer.HtmlLoadedListener() {
                    @Override
                    public void onHtmlLoaded(String html) {
                        parseBody(html);
                    }

                    @Override
                    public void onError(String error) {
                        parseLink(link);
                    }
                });
    }

    public void parseBody(String html) {

        Map<String, String> links = new HashMap<>();

        for (int i = 0; i < videoObject.getRegExp().length; i++) {
            RegexQuality regEx = videoObject.getRegExp()[i];

            Pattern pattern = Pattern.compile(new String(Base64.decode(regEx.getRegExp(), Base64.DEFAULT)));
            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                String link = matcher.group(1).replace("\\", "");
                links.put(regEx.getQuality(), link);
            }
        }
        if (links.size() > 0) {
            if (bodyParserListener != null) {
                bodyParserListener.onParsed(links);
            }

        } else {

        }
    }

    public interface BodyParserListener {
        void onParsed(Map<String, String> links);
    }
}
